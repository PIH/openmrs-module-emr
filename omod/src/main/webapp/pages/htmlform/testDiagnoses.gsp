<%
    ui.decorateWith("appui", "standardEmrPage")
    ui.includeJavascript("emr", "angular.min.js")
    ui.includeJavascript("emr", "diagnoses/diagnoses.js")
    ui.includeJavascript("emr", "diagnoses/diagnoses-angular.js")
%>

<% /* This is an underscore template, since I don't know how to use angular templates programmatically */ %>
<script type="text/template" id="autocomplete-render-item">
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

<div data-ng-app="diagnoses">

    <script type="text/ng-template" id="selected-diagnosis">
        <div class="diagnosis" data-ng-class="{primary: d.primary}">
            <span class="code">
                <span data-ng-show="d.diagnosis.code">{{ d.diagnosis.code }}</span>
                <span data-ng-show="!d.diagnosis.code && d.diagnosis.concept">
                    ${ ui.message("emr.consult.codedButNoCode") }
                </span>
                <span data-ng-show="!d.diagnosis.code && !d.diagnosis.concept">
                    ${ ui.message("emr.consult.nonCoded") }
                </span>
            </span>
            <strong class="matched-name">{{ d.diagnosis.matchedName }}</strong>
            <span class="preferred-name" data-ng-show="d.diagnosis.preferredName">
                <small>${ ui.message("emr.consult.synonymFor") }</small>
                <span>{{ d.diagnosis.concept.preferredName }}</span>
            </span>
            <div class="actions">
                <label>
                    <input type="checkbox" data-ng-model="d.primary"/>
                    ${ ui.message("emr.Diagnosis.Order.PRIMARY") }
                </label>
                <label>
                    <input type="checkbox" data-ng-model="d.confirmed"/>
                    ${ ui.message("emr.Diagnosis.Certainty.CONFIRMED") }
                </label>
            </div>
        </div>
        <i data-ng-click="removeDiagnosis(d)" tabindex="-1" class="icon-remove delete-item"></i>
    </script>

    <div data-ng-controller="DiagnosesController">

        <input type="text" autocomplete itemFormatter="autocomplete-render-item"/>

        <div id="display-diagnoses">
            <h3>${ ui.message("emr.consult.primaryDiagnosis") }</h3>
            <div data-ng-show="encounterDiagnoses.primaryDiagnoses().length == 0">
                ${ ui.message("emr.consult.primaryDiagnosis.notChosen") }
            </div>
            <ul>
                <li data-ng-repeat="d in encounterDiagnoses.primaryDiagnoses()">
                    <span data-ng-include="'selected-diagnosis'"></span>
                </li>
            </ul>
            <ul data-bind="template: { name: 'selected-diagnosis-template', foreach: primaryDiagnoses }"></ul>
            <br/>

            <h3>${ ui.message("emr.consult.secondaryDiagnoses") }</h3>
            <div data-ng-show="encounterDiagnoses.secondaryDiagnoses().length == 0">
                ${ ui.message("emr.consult.secondaryDiagnoses.notChosen") }
            </div>
            <ul>
                <li data-ng-repeat="d in encounterDiagnoses.secondaryDiagnoses()">
                    <span data-ng-include="'selected-diagnosis'"></span>
                </li>
            </ul>
            <ul data-bind="template: { name: 'selected-diagnosis-template', foreach: secondaryDiagnoses }"></ul>
        </div>

        <input type="hidden" data-ng-bind="valueToSubmit()"/>
        <textarea data-ng-bind="valueToSubmit()"></textarea>
    </div>
</div>