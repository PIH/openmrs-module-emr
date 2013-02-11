<%
    def dateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy")
%>

<script type="text/template" id="visitDetailsTemplate">
    <h3>${ui.message("emr.patientDashBoard.visitDetails")}</h3>
    <p>
        {{ if (stopDatetime) { }}
            ${ ui.message("emr.visitDetails", '{{- startDatetime }}', '{{- stopDatetime }}', '{{- location }}') }
        {{ } else { }}
            ${ ui.message("emr.activeVisit", '{{- startDatetime }}', '{{- location }}') }
        {{ } }}
    </p>

    <h4>${ui.message("emr.patientDashBoard.encounters")}</h4>
    <table id="encountersList">
        <thead>
        <tr>
            <th>${ ui.message("emr.patientDashBoard.date")}</th>
            <th>${ ui.message("emr.patientDashBoard.time")}</th>
            <th>${ ui.message("emr.patientDashBoard.type")}</th>
            <th>${ ui.message("emr.patientDashBoard.location")}</th>
            <th>${ ui.message("emr.patientDashBoard.provider")}</th>
        </tr>
        </thead>
        <tbody>
        {{ _.each(encounters, function(encounter) { }}
        <tr>
            <td>{{- encounter.encounterDate }}</td>
            <td>{{- encounter.encounterTime }}</td>
            <td>{{- encounter.encounterType }}</td>
            <td>{{- encounter.location }}</td>
            <td>{{- encounter.encounterProviders[0].provider }}</td>
        </tr>
        {{ }); }}
        </tbody>
    </table>
</script>

<script type="text/javascript">
    jq(function() {
        var visitDetailsTemplate = _.template(jq('#visitDetailsTemplate').html());
        var visitsSection = jq("#visits-list");
        var visitDetailsSection = jq("#visit-details");
        visitDetailsSection.hide();

        jq('a.viewVisitDetails').click(function() {
            jq.getJSON(emr.fragmentActionLink("emr", "visit/visitDetails", "getVisitDetails", {visitId:jq(this).attr('visitId')}) )
                .success(function(data) {
                    visitDetailsSection.html(visitDetailsTemplate(data));
                    visitsSection.hide();
                    visitDetailsSection.show();
                })
                .error(function(err) {
                    emr.errorMessage(err);
                }
            );
            return false;
        });
    });
</script>

<div id="visits-list">
    <h3>${ui.message("emr.patientDashBoard.visits")}</h3>

    <table>
        <thead>
        <tr>
            <th>${ui.message("emr.patientDashBoard.date")}</th>
            <th>${ui.message("emr.patientDashBoard.actions")}</th>
        </tr>
        </thead>
        <% patient.allVisitsUsingWrappers.each { wrapper -> %>
        <tr>
            <td>${dateFormat.format(wrapper.visit.startDatetime)}</td>
            <td><a href="#" class="viewVisitDetails" visitId="${wrapper.visit.visitId}">${ ui.message("emr.visitDetailsLink")} <i class="icon-chevron-right link"></i></a></td>
        </tr>
        <% } %>
    </table>
</div>

<div id="visit-details">
</div>