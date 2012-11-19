<%
    ui.decorateWith("emr", "standardEmrPage", [ title: ui.message("emr.mergePatients") ])
    ui.includeCss("mirebalais", "mergePatients.css")
%>

<script type="text/javascript">
    jq(function() {
        jq('#cancel-button').click(function() {
            emr.navigateTo({ page: 'systemAdministration' });
        });
    });
</script>

<script type="text/javascript">
    jq(function() {
        jq('input[type=text]').first().focus();
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

    <h3>Select two patients to merge...</h3>

    ${ ui.includeFragment("emr", "field/autocomplete", [
            label: "First",
            formFieldName: "patient1",
            fragment: "findPatient",
            action: "search",
            itemValueProperty: "patientId",
            itemLabelFunction: "labelFunction"
    ])}

    <br/>

    ${ ui.includeFragment("emr", "field/autocomplete", [
            label: "Second",
            formFieldName: "patient2",
            fragment: "findPatient",
            action: "search",
            itemValueProperty: "patientId",
            itemLabelFunction: "labelFunction"
    ])}

    <br/>

    <input class="button secondary" type="button" id="cancel-button" value="${ ui.message("emr.cancel") }"/>

    <input class="button primary" type="submit" value="${ ui.message("emr.continue") }"/>

</form>