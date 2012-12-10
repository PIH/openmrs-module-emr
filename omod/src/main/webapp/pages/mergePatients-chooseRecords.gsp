<%
    ui.decorateWith("emr", "standardEmrPage", [ title: ui.message("emr.mergePatients") ])
    ui.includeCss("mirebalais", "mergePatients.css")

    def id = ""
    def fullName = ""

    if (patient1 != null){
         id = patient1.patient.id
         fullName = patient1.primaryIdentifiers.collect{ it.identifier }.join(',') + "-" + ui.format(patient1.patient)
    }

%>

<script type="text/javascript">
    jq(function() {
        jq('input[type=text]').first().focus();

        jq('#cancel-button').click(function() {
            window.history.back();
        });
    });

    function labelFunction(item) {
        var id = item.patientId;
        if (item.primaryIdentifiers[0]) {
            id = item.primaryIdentifiers[0].identifier;
        }
        return id + ' - ' + item.preferredName.fullName;
    }
</script>

<form method="get">

    <h3>${ ui.message("emr.mergePatients.selectTwo") }</h3>
    <input type= "hidden" name= "unknown-patient" value= "${isUnknownPatient}"/>


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
            value: fullName
    ])}

    <br/>

    ${ ui.includeFragment("emr", "field/autocomplete", [
            id: "choose-second",
            label: ui.message("emr.mergePatients.chooseSecondLabel"),
            formFieldName: "patient2",
            fragment: "findPatient",
            action: "search",
            itemValueProperty: "patientId",
            itemLabelFunction: "labelFunction"
    ])}

    <br/>

    <p>
        <input class="cancel" type="button" id="cancel-button" value="${ ui.message("emr.cancel") }"/>

        <input class="confirm" type="submit" value="${ ui.message("emr.continue") }"/>
    </p>

</form>