<%
	ui.decorateWith("emr", "standardEmrPage")

    def interpolate = { "<%= " + it + " %" + ">" }
%>

<style type="text/css">
    .patient-result {
        cursor: pointer;
        padding: 1em;
        margin-bottom: 1em;
        border: 1px black solid;
        border-radius: 0.2em;
    }

    .patient-result:nth-child(even) {
        background-color: #e0e0e0;
    }

    .patient-result:nth-child(odd) {
        background-color: #f0f0f0;
    }

    .patient-result .name {
        font-weight: bold;
        font-size: 1.2em;
    }
</style>

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
        var query = jq('#find-patient-form').serializeArray();
        jq.getJSON('${ ui.actionLink('emr', 'findPatient', 'search') }', query)
                .success(function(data) {
                    jq('#results').html('');
                    jq.each(data, function(i, patient) {
                        jq(patientResultTemplate(patient)).appendTo(jq('#results'));
                    });
                })
                .error(function(err) {
                    emr.showError(err);
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
