<%
    def dateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy")
    def timeFormat = new java.text.SimpleDateFormat("HH:mm")
%>

<script type="text/template" id="visitDetailsTemplate">
    <h3>${ui.message("emr.patientDashBoard.visitDetails")}</h3>
    <ul>
        <li><span class="label">${ ui.message("emr.patientDashBoard.visit.startDatetime")}</span> {{- startDatetime }}</li>
        <li><span class="label">${ ui.message("emr.patientDashBoard.visit.stopDatetime")}</span> {{- stopDatetime }}</li>
        <li><span class="label">${ ui.message("emr.patientDashBoard.location")}</span> {{- location }}</li>
    </ul>

    <h3>${ui.message("emr.patientDashBoard.encounters")}</h3>
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
        {{ _.each(encounters, function(enc) { }}
        <tr>
            <td>{{- enc.encounterDate }}</td>
            <td>{{- enc.encounterTime }}</td>
            <td>{{- enc.encounterType }}</td>
            <td>{{- enc.location }}</td>
            <td>{{- enc.encounterProviders[0].provider }}</td>
        </tr>
        {{ }); }}
        </tbody>
    </table>
</script>

<script type="text/javascript">
    jq(function() {
        var visitDetailsTemplate = _.template(jq('#visitDetailsTemplate').html());
        var visitsSection = jq("#visitsList");
        var visitDetailsSection = jq("#visitDetails");
        visitDetailsSection.hide();

        var fillEncountersTable = function(encounters) {
            jq.each(encounters, function(index, encounter) {
                jq(visitDetailsTemplate(encounter)).appendTo(tbody);
            })
        }

        jq('.viewVisitDetails').click(function() {
            jq.getJSON(emr.fragmentActionLink("emr", "visit/visitDetails", "getVisitDetails", {visitId:jq(this).attr('visitId')}) )
                    .success(function(data) {
                        visitDetailsSection.html(visitDetailsTemplate(data));
                        visitsSection.hide();
                        visitDetailsSection.show();
                    })
                    .error(function(err) {
                        emr.errorMessage(err);
                    });
        });
    });
</script>

<h3>${ui.message("emr.patientDashBoard.visits")}</h3>

<table>
    <thead>
    <tr>
        <th>${ui.message("emr.patientDashBoard.date")}</th>
        <th>${ui.message("emr.patientDashBoard.startTime")}</th>
        <th>${ui.message("emr.patientDashBoard.location")}</th>
    </tr>
    </thead>
    <% patient.allVisitsUsingWrappers.each { wrapper -> %>
    <tr>
        <td>${dateFormat.format(wrapper.visit.startDatetime)} <br>(${wrapper.differenceInDaysBetweenCurrentDateAndStartDate} days ago) </td>
        <td>${timeFormat.format(wrapper.visit.startDatetime)}</td>
        <td>${ ui.format(wrapper.visit.location) }</td>
    </tr>
    <% } %>
</table>

<div id="visitDetails">
</div>