<%
	ui.decorateWith("emr", "standardEmrPage")
    ui.includeCss("emr", "findPatient.css")
%>
<script id="patient-result-template" type="text/template">
    <div class="patient-result">
        <input type="hidden" name="patientId" value="{{- patientId }}"/>
        <span class="icon"><img width="32" src="<%= ui.resourceLink("uilibrary", "images/patient_{{- gender }}.gif") %>"/></span>
        <span class="name">{{- preferredName.fullName }}</span>
        <br/>
        <span class="gender">{{- gender }}</span>
        <span class="age">{{- age }} year(s)</span>
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
        jq('#find-patient-form').submit(function() { return false; });

        jq('#find-patient-form :input').keypress(doPatientSearch);

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

<div id="search-box" class="container">
    ${ ui.message("emr.findPatient.which.heading") }
    <%
        def opts = [] + [ id: null, label: ui.message("emr.findPatient.allPatients") ]
        opts.addAll(locationsThatSupportVisits.collect { [ id: it.id, label: ui.message("emr.activeVisitAtLocation", ui.format(it)) ] })

        opts.each {
            def selected = it.id == checkedInAt?.id
    %>
            <div class="location-option<% if (selected) { %> selected<% } %>">
                <% if (!selected) { %>
                    <a href="?checkedInAt=${ it.id ?: "" }">
                <% } %>
                ${ it.label }
                <% if (!selected) { %>
                    </a>
                <% } %>
            </div>
        <% } %>

</div>

<div id="results-box" class="container">
    <form id="find-patient-form">
        <input type="hidden" name="checkedInAt" value="${ checkedInAt?.id ?: "" }"/>
        ${ ui.message("emr.findPatient.search") }: <input type="text" size="40" name="q"/>
    </form>

    <div id="results">
    </div>
</div>
