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
                <span class="status active"></span> ${ui.message("emr.activeVisit")}
                <i class="icon-time small"></i> ${ ui.message("emr.activeVisit.time", '{{- startDatetime }}')}
                
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

    <h4>${ ui.message("emr.patientDashBoard.encounters")} </h4>
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
                            <i class="{{- getEncounterIcon(encounter.encounterType) }}"></i>
                            {{- encounter.encounterType }}
                        </strong>
                    </span>
                    <span>
                        ${ ui.message("emr.by") }
                        <strong>{{- encounter.encounterProviders[0] ? encounter.encounterProviders[0].provider : '' }}</strong>
                        ${ ui.message("emr.in") }
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

            if (visitElement != null && visitElement.attr('visitId') != undefined) {

                visitDetailsSection.html("<i class=\"icon-spinner icon-spin icon-2x pull-left\"></i>");
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

  function getEncounterIcon(encounterType) {
        var encounterIconMap = {
            "Vitals": "icon-vitals",
            "Check-in": "icon-check-in",
            "Consultation": "icon-stethoscope",
            "Radiology Order": "icon-x-ray",
            "Patient Registration": "icon-register",
            "Payment Encounter": "icon-money",
            "Post-operative note": "icon-user-md",
            "Primary Care Visit": "icon-calendar"
        };

        return encounterIconMap[encounterType] || "icon-time";
    };
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
                    (${ ui.message("emr.patientDashBoard.activeSince")} ${timeFormat.format(wrapper.visit.startDatetime)})
                <% } %>
            </span>
            <span class="visit-primary-diagnosis">
                ${ ui.message("emr.patientDashBoard.noDiagnosis")}
            </span>
            <span class="arrow-border"></span>
            <span class="arrow"></span>
        </li>
    <% } %>
    <% if(patient.allVisitsUsingWrappers.size == 0) { %>
        ${ ui.message("emr.patientDashBoard.noVisits")} 
    <% } %>
</ul>
<div id="visit-details">
</div>