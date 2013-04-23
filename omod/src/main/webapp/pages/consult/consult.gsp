<%
    ui.decorateWith("emr", "standardEmrPage")

    ui.includeJavascript("emr", "knockout-2.1.0.js")
    ui.includeJavascript("emr", "consult/consult.js")

    ui.includeCss("mirebalais", "consult.css", -200)

    def patient = emrContext.currentPatient
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.format(patient.familyName) }, ${ ui.format(patient.givenName) }", link:'${ ui.pageLink("emr", "patient", [patientId: patient.id]) }' },
        { label: "${ ui.message("emr.consult.title") }" }
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
            emr.navigateTo({ page: "patient", query: { patientId: ${ patient.id } } });
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

${ ui.includeFragment("emr", "patientHeader", [ patient: patient ]) }

<div id="contentForm">
    <h2>${ ui.message("emr.consult.title") }</h2>
    <form id="consult-note" method="post">
        <div id="entry-fields">
            <p>
                <label for="diagnosis-search">${ ui.message("emr.consult.addDiagnosis") }</label>
                <input id="diagnosis-search" type="text" placeholder="${ ui.message("emr.consult.addDiagnosis.placeholder") }" data-bind="autocomplete: searchTerm, itemFormatter: formatAutosuggestion"/>
            </p>
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