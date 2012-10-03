<%
	ui.decorateWith("emr", "standardEmrPage")
    ui.includeCss("emr", "findPatient.css")

    def interpolate = { "<%= " + it + " %" + ">" }
%>

<script id="patient-result-template" type="text/template">
    <div class="patient-result">
        <input type="hidden" name="patientId" value="${ interpolate("patientId") }"/>
        <span class="name">${ interpolate("preferredName.fullName") }</span>
        <span class="gender">${ interpolate("gender") }</span>
        <span class="age">${ interpolate("age")} year(s)</span>
    </div>
</script>

<script type="text/javascript">
    var patientResultTemplate = _.template(jq('#patient-result-template').html());

    function doPatientSearch() {
        emr.ajaxSearch({
            fragment: 'findPatient',
            action: 'search',
            query: jq('#find-patient-form').serializeArray(),
            resultTarget: '#results',
            resultTemplate: patientResultTemplate
        });
    }

    jq(function() {
        jq('#find-patient-form :input').change(doPatientSearch);

        jq('#results').on('click', '.patient-result', function(event) {
            var ptId = jq(this).find('input[name=patientId]').val();
            emr.navigateTo({
                provider: 'emr',
                page: 'patient',
                query: { patientId: ptId }
            });
        });

        // do an initial search
        doPatientSearch();
    });
</script>

Find a patient:
<form id="find-patient-form">
    <select name="checkedInAt">
        <option value="">All patients</option>
        <% context.locationService.getAllLocations(false).each { %>
            <option value="${ it.id }">Checked in at ${ ui.format(it) }</option>
        <% } %>
    </select>
    <input type="text" size="40" name="q"/>
</form>

<div id="results">
</div>
