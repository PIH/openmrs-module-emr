<%
    def dateFormat = new java.text.SimpleDateFormat("dd MMM yyyy")
    def timeFormat = new java.text.SimpleDateFormat("hh:mm a")
    def formatDiagnoses = {
        it.collect{ ui.escapeHtml(it.diagnosis.formatWithoutSpecificAnswer(context.locale)) } .join(", ")
    }
    ui.includeJavascript("emr", "fragments/visitDetails.js")
%>

<script type="text/javascript">
    breadcrumbs.push({ label: "${ui.message("emr.patientDashBoard.visits")}" , link:'${ui.pageLink("emr", "patient", [patientId: patient.id])}'});

    jq(".collapse").collapse();
</script>

<script type="text/template" id="encounterDetailsTemplate">
    {{ _.each(observations, function(observation) { }}
        {{ if(observation.answer != null) {}}
            <p><small>{{- observation.question}}</small><span>{{- observation.answer}}</span></p>
        {{}}}
    {{ }); }}

    {{ _.each(diagnoses, function(diagnosis) { }}
        {{ if(diagnosis.answer != null) {}}
            <p><small>{{- diagnosis.question}}</small><span>{{- diagnosis.answer}}</span></p>
    {{}}}
    {{ }); }}

    {{ _.each(orders, function(order) { }}
         <p><small>${ ui.message("emr.patientDashBoard.order")}</small><span>{{- order.concept }}</span></p>
    {{ }); }}
</script>

<script type="text/template" id="visitDetailsTemplate">
    {{ if (stopDatetime) { }}
        <div class="visit-status">
            <i class="icon-time small"></i> ${ ui.message("emr.visitDetails", '{{- startDatetime }}', '{{- stopDatetime }}')}
        </div>
    {{ } else { }}

        <div class="visit-status">
            <span class="status active"></span> ${ui.message("emr.activeVisit")}
            <i class="icon-time small"></i>
            ${ ui.message("emr.activeVisit.time", '{{- startDatetime }}')}
            
        </div>
        <div class="visit-actions">
            <% activeVisitTasks.each{task -> def url = task.getUrl(emrContext)
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

    <h4>${ ui.message("emr.patientDashBoard.encounters")} </h4>
    <ul id="encountersList">
    {{ var i = 1;}}
        {{ _.each(encounters, function(encounter) { }}
            {{ if (!encounter.voided) { }}
            <li>
                <div class="encounter-date">
                    <i class="icon-time"></i>
                    <strong>
                        {{- encounter.encounterTime }}
                    </strong>
                    {{- encounter.encounterDate }}
                </div>
                <ul class="encounter-details">
                    <li> 
                        <div class="encounter-type">
                            <strong>
                                <i class="{{- getEncounterIcon(encounter.encounterType.uuid) }}"></i>
                                <span class="encounter-name" data-encounter-id="{{- encounter.encounterId }}">{{- encounter.encounterType.name }}</span>
                            </strong>
                        </div>
                    </li>
                    <li>
                        <div>
                            ${ ui.message("emr.by") }
                            <strong>
                                {{- encounter.encounterProviders[0] ? encounter.encounterProviders[0].provider : '' }}
                            </strong>
                            ${ ui.message("emr.in") }
                            <strong>{{- encounter.location }}</strong>
                        </div>
                    </li>
                    {{ if (encounter.encounterType.uuid != "873f968a-73a8-4f9c-ac78-9f4778b751b6") {}}
                    <li>
                        <div class="details-action">
                            <a class="view-details collapsed" href='javascript:void(0);' data-encounter-id="{{- encounter.encounterId }}" data-encounter-form="{{- encounter.form != null}}" data-target="#encounter-summary{{- i }}" data-toggle="collapse" data-target="#encounter-summary{{- i }}">
                                <span class="show-details">${ ui.message("emr.patientDashBoard.showDetails")}</span>
                                <span class="hide-details">${ ui.message("emr.patientDashBoard.hideDetails")}</span>
                                <i class="icon-caret-right"></i>
                            </a>
                            
                        </div>
                    </li>
                    {{}}}
                </ul>
                {{ if ( encounter.canDelete ) { }}
                <span>
                    <i class="deleteEncounterId delete-item icon-remove" data-encounter-id="{{- encounter.encounterId }}" title="${ ui.message("emr.delete") }"></i>
                </span>
                {{  } }}
                <div id="encounter-summary{{- i }}" class="collapse">
                    <div class="encounter-summary-container"></div>
		        </div>
                {{ i++; }}
            </li>
            {{  } }}
        {{ }); }}
    </ul>
</script>
<script type="text/javascript">
    jq(function(){
        loadTemplates();
    });
</script>

<ul id="visits-list">
    <% patient.allVisitsUsingWrappers.each { wrapper ->
        def primaryDiagnoses = wrapper.primaryDiagnoses
    %>
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
                <i class="icon-stethoscope"></i>
                <% if (primaryDiagnoses) { %>
                    ${ formatDiagnoses(primaryDiagnoses) }
                <% } else { %>
                    ${ ui.message("emr.patientDashBoard.noDiagnosis")}
                <% } %>
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
<div id="delete-encounter-dialog" class="dialog" style="display: none">
    <div class="dialog-header">
        <h3>${ ui.message("emr.patientDashBoard.deleteEncounter.title") }</h3>
    </div>
    <div class="dialog-content">
        <input type="hidden" id="encounterId" value=""/>
        <ul>
            <li class="info">
                <span>${ ui.message("emr.patientDashBoard.deleteEncounter.message") }</span>
            </li>

        </ul>

        <button class="confirm right">${ ui.message("emr.yes") }</button>
        <button class="cancel">${ ui.message("emr.no") }</button>
    </div>
</div>
