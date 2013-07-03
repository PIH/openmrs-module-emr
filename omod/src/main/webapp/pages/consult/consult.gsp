<%
    ui.decorateWith("appui", "standardEmrPage")

    ui.includeJavascript("emr", "knockout-2.1.0.js")
    ui.includeJavascript("emr", "consult/consult.js")

    ui.includeCss("mirebalais", "consult.css", -200)

    def patient = emrContext.currentPatient

    def dateFormat = new java.text.SimpleDateFormat("dd MMM yyyy K:mm a")
    def editDateFormat = new java.text.SimpleDateFormat("dd-MM-yyyy")
    def isThisVisitActive = emrContext.activeVisit && emrContext.activeVisit.visit == visit
    def encounterDate = isThisVisitActive ? new Date() : visit.startDatetime
    def now = dateFormat.format(encounterDate)
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.format(patient.familyName) }, ${ ui.format(patient.givenName) }", link:'${ ui.pageLink("coreapps", "patientdashboard/patientDashboard", [patientId: patient.id]) }' },
        { label: "${ ui.message(title) }" }
    ];

    var formatTemplate;

    var formatAutosuggestion = function(item) {
        return item ? formatTemplate({ item: item }) : "";
    };

    var viewModel = ConsultFormViewModel();

    jq(function() {
        formatTemplate = _.template(jq('#autocomplete-render-template').html());
        ko.applyBindings(viewModel, jq('#contentForm').get(0));
        jq('#diagnosis-search').focus();
        jq('#contentForm .cancel').click(function(event) {
            emr.navigateTo({ provider:"coreapps", page: "patientdashboard/patientDashboard", query: { patientId: ${ patient.id } } });
        });

        jq('#consult-note').submit(function() {
            var valid = viewModel.isValid();
            if (valid) {
                viewModel.startSubmitting();
            }
            return valid;
        });
    });
</script>

${ ui.includeFragment("coreapps", "patientHeader", [ patient: patient ]) }

<div id="contentForm">
    <h2>${ ui.message(title) }</h2>
    <form id="consult-note" method="post">
        <table id="who-where-when-view"<% if (!isThisVisitActive) { %>  class="hidden" <% } %> ><tr>
            <td>
                <label>${ ui.message("emr.patientDashBoard.provider") }</label>
                <span>${ ui.format(emrContext.currentProvider) }</span>
            </td>
            <td>
                <label>${ ui.message("emr.location") }</label>
                <span>${ ui.format(sessionContext.sessionLocation) }</span>
            </td>
            <td>
                <label>${ ui.message("emr.patientDashBoard.date") }</label>
                <span>${ now }</span>
            </td>
        </tr></table>

        <div id="who-where-when-edit"<% if (isThisVisitActive) { %>  class="hidden" <% } %> >
            ${ ui.includeFragment("emr", "field/dropDown", [
                    id: "consultProvider",
                    label: "emr.patientDashBoard.provider",
                    formFieldName: "consultProviderId",
                    options: providers,
                    classes: ['required'],
                    initialValue: isThisVisitActive ? emrContext.currentProvider.providerId : null
            ])}

            ${ ui.includeFragment("emr", "field/location", [
                    id: "consultLocation",
                    label: "emr.location",
                    formFieldName: "consultLocationId",
                    classes: ['required'],
                    withTag: "Login Location",
                    initialValue: isThisVisitActive ? sessionContext.sessionLocationId : null
            ])}

            ${ ui.includeFragment("uicommons", "field/datetimepicker", [
                    id: "consultDate",
                    label: "emr.patientDashBoard.date",
                    formFieldName: "consultDate",
                    useTime: false,
                    defaultDate: encounterDate,
                    startDate: editDateFormat.format(encounterStartDateRange),
                    endDate: editDateFormat.format(encounterEndDateRange),
                    classes: ['required']
            ])}
        </div>

        <div id="entry-fields">
            <p>
                <label for="diagnosis-search">${ ui.message("emr.consult.addDiagnosis") }</label>
                <input id="diagnosis-search" type="text" placeholder="${ ui.message("emr.consult.addDiagnosis.placeholder") }" data-bind="autocomplete: searchTerm, itemFormatter: formatAutosuggestion"/>
            </p>

            <% if (dispositions && featureToggles.isFeatureEnabled("consultNoteDispositions")) { %>
                <%
                    def dispositionOptions = []
                    dispositions.each { disposition ->
                        dispositionOptions << [label: ui.message(disposition.name), value: disposition.uuid ]
                    }
                %>
                ${ ui.includeFragment("uicommons", "field/dropDown", [
                        id: 'disposition',
                        label: 'emr.consult.disposition',
                        classes: [ isThisVisitActive ? 'required' : '' ],
                        formFieldName: 'disposition',
                        dependency: true,
                        options: dispositionOptions
                ])}

                <% dispositions.each { disposition -> %>
                    <% if (disposition.clientSideActions) { %>
                        <% disposition.clientSideActions.each { action ->
                            def includeConfig = [
                                    observable: action.fragmentConfig.formFieldName,
                                    depends: [ variable: "disposition", value: "${ disposition.uuid }", enable: "${ disposition.uuid }" ]
                            ] << action.fragmentConfig
                            if (("${ disposition.uuid }" == "markPatientDead") &&
                                (action.fragment == "field/datetimepicker") ) {
                                includeConfig.put( "defaultDate", encounterDate)
                                includeConfig.put( "startDate", editDateFormat.format(encounterStartDateRange))
                                includeConfig.put( "endDate", editDateFormat.format(encounterEndDateRange))
                            }

                        %>
                            ${ ui.includeFragment(action.module, action.fragment, includeConfig ) }
                        <% } %>
                    <% } %>
                <% } %>
            <% } %>

            <% additionalObservationsConfig.each { config -> %>
                ${ ui.includeFragment("emr", "widget/observation", config) }
            <% } %>

            <p>
                <label for="free-text-comments">${ ui.message("emr.consult.freeTextComments") }</label>
                <textarea id="free-text-comments" rows="5" name="freeTextComments"></textarea>
            </p>
        </div>

        <div id="display-diagnoses">
            <h3>${ ui.message("emr.consult.primaryDiagnosis") }</h3>
            <div data-bind="visible: primaryDiagnoses().length == 0">
                ${ ui.message("emr.consult.primaryDiagnosis.notChosen") }
            </div>
            <ul data-bind="template: { name: 'selected-diagnosis-template', foreach: primaryDiagnoses }"></ul>
            <br/>

            <h3>${ ui.message("emr.consult.secondaryDiagnoses") }</h3>
            <div data-bind="visible: secondaryDiagnoses().length == 0">
                ${ ui.message("emr.consult.secondaryDiagnoses.notChosen") }
            </div>
            <ul data-bind="template: { name: 'selected-diagnosis-template', foreach: secondaryDiagnoses }"></ul>
        </div>

        <div id="buttons">
            <button type="submit" class="confirm right" data-bind="css: { disabled: !canSubmit() }, enable: canSubmit()">${ ui.message("emr.save") }</button>
            <button type="button" class="cancel">${ ui.message("emr.cancel") }</button>
        </div>
    </form>
</div>

<% /* This is a knockout template, so we can use data-binds inside */ %>
<script type="text/html" id="selected-diagnosis-template">
    <li>
        <div class="diagnosis" data-bind="css: { primary: primary }">
            <span class="code">
                <span data-bind="if: diagnosis().code, text: diagnosis().code"></span>
                <span data-bind="if: !diagnosis().code && diagnosis().concept">
                    ${ ui.message("emr.consult.codedButNoCode") }
                </span>
                <span data-bind="if: !diagnosis().code && !diagnosis().concept">
                    ${ ui.message("emr.consult.nonCoded") }
                </span>
            </span>
            <strong class="matched-name" data-bind="text: diagnosis().matchedName"></strong>
            <span class="preferred-name" data-bind="if: diagnosis().preferredName">
                <small>${ ui.message("emr.consult.synonymFor") }</small>
                <span data-bind="text: diagnosis().concept.preferredName"></span>
            </span>
            <% if (featureToggles.isFeatureEnabled("consult_note_confirm_diagnoses")) { %>
                <div class="actions">
                    <label>
                        <input type="checkbox" data-bind="checked: primary"/>
                        ${ ui.message("emr.Diagnosis.Order.PRIMARY") }
                    </label>
                    <label>
                        <input type="checkbox" data-bind="checked: confirmed"/>
                        ${ ui.message("emr.Diagnosis.Certainty.CONFIRMED") }
                    </label>
                </div>
            <% } %>
        </div>
        <i data-bind="click: \$parent.removeDiagnosis" tabindex="-1" class="icon-remove delete-item"></i>
        <input type="hidden" name="diagnosis" data-bind="value: valueToSubmit()">
    </li>
</script>

<% /* This is an underscore template */ %>
<script type="text/template" id="autocomplete-render-template">
    <span class="code">
        {{ if (item.code) { }}
            {{- item.code }}
        {{ } else if (item.concept) { }}
            ${ ui.message("emr.consult.codedButNoCode") }
        {{ } else { }}
            ${ ui.message("emr.consult.nonCoded") }
        {{ } }}
    </span>
    <strong class="matched-name">
        {{- item.matchedName }}
    </strong>
    {{ if (item.preferredName) { }}
        <span class="preferred-name">
            <small>${ ui.message("emr.consult.synonymFor") }</small>
            {{- item.concept.preferredName }}
        </span>
    {{ } }}
</script>