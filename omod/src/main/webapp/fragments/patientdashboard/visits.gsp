<%
    def dateFormat = new java.text.SimpleDateFormat("dd MMM yyyy")
    def timeFormat = new java.text.SimpleDateFormat("hh:mm a")
%>

<script type="text/javascript">
    breadcrumbs.push({ label: "${ui.message("emr.patientDashBoard.visits")}" , link:'${ui.pageLink("emr", "patient", [patientId: patient.id])}'});
</script>

<script type="text/template" id="visitDetailsTemplate">
    <p>
        {{ if (stopDatetime) { }}
            <div class="visit-status">
                <i class="icon-time small"></i> ${ ui.message("emr.visitDetails", '{{- startDatetime }}', '{{- stopDatetime }}')}
            </div>
        {{ } else { }}

            <div class="visit-status">
                <i class="icon-time small"></i> ${ ui.message("emr.activeVisit.time", '{{- startDatetime }}')}
                <span class="status active"></span> ${ui.message("emr.activeVisit")}
            </div>

            <div class="visit-actions">

                <%
                    activeVisitTasks.each{task ->
                        def url = task.getUrl(emrContext)

                        if (!url.startsWith("javascript:")) {
                            url = "/" + contextPath + "/" + url
                        }
                %>

                <a href="${ url }" class="button task">
                    <i class="${task.getIconUrl(emrContext)}"></i> ${ task.getLabel(emrContext) }
                </a>

                 <% } %>
            </div>
       {{  } }}
</p>

    <h4>Encounters</h4>
    <ul id="encountersList">
        {{ _.each(encounters, function(encounter) { }}
            <li>
                <div class="encounter-date">
                    <i class="icon-time"></i>
                    {{- encounter.encounterTime }}
                    {{- encounter.encounterDate }}
                </div>
                <div class="encounter-details">
                    <span class="encounter-type"> 
                        <strong>
                            {{- encounter.encounterType }}
                        </strong>
                    </span>
                    <span>
                        by
                        <strong>{{- encounter.encounterProviders[0].provider }}</strong>
                        in
                        <strong>{{- encounter.location }}</strong>
                    </span>  
                </div>
            </li>
        {{ }); }}
    </ul>
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
    <% if(patient.allVisitsUsingWrappers.size == 0) { %>
        No visits yet.
    <% } %>
</ul>
<div id="visit-details">
</div>