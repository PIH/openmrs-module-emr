<%
    ui.decorateWith("emr", "standardEmrPage")

    ui.includeJavascript("emr", "knockout-2.1.0.js")
    ui.includeJavascript("emr", "consult/consult.js")

    def patient = emrContext.currentPatient
%>
<style type="text/css">
    #entry-fields, #display-diagnoses {
        display: inline-block;
        width: 48%;
    }
    #buttons {
        margin-top: 1em;
    }

    .matched-name {
        display: block;
    }
    .code {
        float: right;
    }

    .ui-menu .ui-menu-item {
        border: 1px black solid;
    }
</style>
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

    var formatChosenItem = function(item) {
        return formatAutosuggestion(item);
    };

    var valueToSubmit = function(item) {
        return item ? item.valueToSubmit() : null;
    };

    var mapTypeOrder = [ "SAME-AS", "NARROWER-THAN" ]
    var findConceptMapping = function(concept, sourceName) {
        var matches = _.filter(concept.conceptMappings, function(item) {
            return item.conceptReferenceTerm.conceptSource.name == sourceName
        });
        if (!matches) {
            return "";
        }
        return _.sortBy(matches, function(item) {
            var temp = _.indexOf(mapTypeOrder, item.conceptMapType);
            return temp < 0 ? 9999 : temp;
        })[0].conceptReferenceTerm.code;
    };

    var viewModel = ConsultFormViewModel();

    jq(function() {
        formatTemplate = _.template(jq('#autocomplete-render-template').html());
        ko.applyBindings(viewModel, jq('#contentForm').get(0));
        jq('#diagnosis-search').focus();
        jq('#contentForm .cancel').click(function(event) {
            emr.navigateTo({ page: "patient", query: { patientId: ${ patient.id } } });
        });
    });
</script>

${ ui.includeFragment("emr", "patientHeader", [ patient: patient ]) }

<div id="contentForm">
    <h1>${ ui.message("emr.consult.title") }</h1>
    <form method="post">
        <div id="entry-fields">
            <label for="diagnosis-search">Add a diagnosis:</label>
            <input id="diagnosis-search" type="text" data-bind="autocomplete: searchTerm, itemFormatter: formatAutosuggestion"/>

            <br/><br/>
            (Optional) comments
            <textarea rows="5" cols="40" name="freeTextComments"></textarea>
        </div>

        <div id="display-diagnoses">
            <h3>Primary Diagnosis:</h3>
            <div data-bind="visible: !primaryDiagnosis()">
                Not chosen
            </div>
            <div data-bind="visible: primaryDiagnosis, html: formatChosenItem(primaryDiagnosis())"></div>
            <button data-bind="visible: primaryDiagnosis, click: removePrimaryDiagnosis">Remove</button>
            <input type="hidden" name="primaryDiagnosis" data-bind="value: valueToSubmit(primaryDiagnosis())">

            <br/>

            <h3>Secondary Diagnoses:</h3>
            <ul data-bind="foreach: secondaryDiagnoses">
                <li>
                    <span data-bind="html: formatChosenItem(\$data)"></span>
                    <button data-bind="click: \$parent.removeDiagnosis">Remove</button>
                    <input type="hidden" name="secondaryDiagnoses" data-bind="value: valueToSubmit(\$data)"/>
                </li>
            </ul>
        </div>

        <div id="buttons">
            <button type="submit" class="confirm right" data-bind="css: { disabled: !isValid() }, enable: isValid()">${ ui.message("emr.save") }</button>
            <button type="button" class="cancel">${ ui.message("emr.cancel") }</button>
        </div>
    </form>
</div>

<script type="text/template" id="autocomplete-render-template">
    <span class="code">
        {{- findConceptMapping(item.concept, "ICD-10-WHO") }}
    </span>
    <strong class="matched-name">
        {{- item.conceptName.name }}
    </strong>
    {{ if (!item.nameIsPreferred) { }}
        <span class="preferred-name">
           &rarr; {{- item.concept.preferredName }}
        </span>
    {{ } }}
</script>