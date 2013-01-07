<%
    ui.decorateWith("emr", "standardEmrPage", [ title: ui.message("emr.mergePatients") ])
    ui.includeCss("mirebalais", "mergePatients.css")
    ui.includeJavascript("emr", "mergePatients.js")

    def id = ""
    def fullName = ""

    if (patient1 != null){
         id = patient1.patient.id
         fullName = patient1.primaryIdentifiers.collect{ it.identifier }.join(',') + "-" + ui.format(patient1.patient)
    }

%>


<form method="get">

    <h3>${ ui.message("emr.mergePatients.selectTwo") }</h3>
    <input type= "hidden" name= "isUnknownPatient" value= "${isUnknownPatient}"/>


    ${ ui.includeFragment("emr", "field/autocomplete", [
            id: "choose-first",
            label: ui.message("emr.mergePatients.chooseFirstLabel"),
            formFieldName: "patient1",
            fragment: "findPatient",
            action: "search",
            itemValueProperty: "patientId",
            itemLabelFunction: "labelFunction",
            patientId: id,
            disabled: isUnknownPatient,
            value: fullName,
            function: "verifyPatientsToMerge('" + ui.message("emr.patient.notFound") + "', items, fieldId);"
    ])}

    <br/>

    ${ ui.includeFragment("emr", "field/autocomplete", [
            id: "choose-second",
            label: ui.message("emr.mergePatients.chooseSecondLabel"),
            formFieldName: "patient2",
            fragment: "findPatient",
            action: "search",
            itemValueProperty: "patientId",
            itemLabelFunction: "labelFunction",
            function: "verifyPatientsToMerge('" + ui.message("emr.patient.notFound") + "', items, fieldId);"
    ])}

    <br/>

    <p>
        <input class="cancel" type="button" id="cancel-button" value="${ ui.message("emr.cancel") }"/>

        <input class="confirm disabled" type="submit" disabled="disabled" id="confirm-button" value="${ ui.message("emr.continue") }"/>
    </p>

</form>