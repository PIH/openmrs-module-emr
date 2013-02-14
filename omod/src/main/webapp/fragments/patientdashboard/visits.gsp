<%
    def dateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy")
    def timeFormat = new java.text.SimpleDateFormat("HH:mm")
%>

<script type="text/javascript">
    breadcrumbs.push({ label: "${ui.message("emr.patientDashBoard.visits")}" , link:'${ui.pageLink("emr", "patient", [patientId: patient.id])}'});
</script>

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
        function loadVisit(visitElement) {
            jq.getJSON(
                emr.fragmentActionLink("emr", "visit/visitDetails", "getVisitDetails", {
                    visitId: visitElement.attr('visitId')
                }) 
            ).success(function(data) {
                jq('.viewVisitDetails').removeClass('selected');
                visitElement.addClass('selected');
                visitDetailsSection.html(visitDetailsTemplate(data));
                visitDetailsSection.show();
            }).error(function(err) {
                emr.errorMessage(err);
            });
        }

        var visitDetailsTemplate = _.template(jq('#visitDetailsTemplate').html());
        var visitsSection = jq("#visits-list");
        var visitDetailsSection = jq("#visit-details");

        //load first visit
        loadVisit(jq('.viewVisitDetails').first());

        jq('.viewVisitDetails').click(function() {
            loadVisit(jq(this));
            return false;
        });
    });
</script>

<ul id="visits-list">
    <% patient.allVisitsUsingWrappers.each { wrapper -> %>
        <li class="viewVisitDetails" visitId="${wrapper.visit.visitId}">
            <span class="visit-date">
                <i class="icon-time"></i>
                ${dateFormat.format(wrapper.visit.startDatetime)}
                <% if(wrapper.visit.stopDatetime != null) { %>
                    - ${dateFormat.format(wrapper.visit.stopDatetime)}
                <% } else { %>
                    (active since ${timeFormat.format(wrapper.visit.startDatetime)})
                <% } %>
            </span>
            <span class="visit-primary-diagnosis">
                No diagnosis yet.
            </span>
            <span class="arrow-border"></span>
            <span class="arrow"></span>
        </li>
    <% } %>
</ul>
<div id="visit-details">
</div>