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

<!-- Encounter templates -->
<%
	ui.includeJavascript("emr", "fragments/encounterTemplates.js")
%>
<script type="text/javascript">
	jq(function() {
		<% encounterTemplateExtensions.each { extension -> 
			extension.extensionParams.supportedEncounterTypes?.each { encounterType -> %>
		encounterTemplates.setTemplate('${encounterType.key}', '${extension.extensionParams.templateId}');
				<% encounterType.value.each { parameter -> %>
		encounterTemplates.setParameter('${encounterType.key}', '${parameter.key}', '${parameter.value}');
				<% }
			}
		} %>
		encounterTemplates.setDefaultTemplate('defaultEncounterTemplate');
	});
</script>
<% encounterTemplateExtensions.each { extension -> %>
	${ui.includeFragment(extension.extensionParams.templateFragmentProviderName, extension.extensionParams.templateFragmentId)}
<% } %>
<!-- End of encounter templates -->

<script type="text/template" id="visitDetailsTemplate">
    {{ if (stopDatetime) { }}
        <div class="status-container">
            <i class="icon-time small"></i> ${ ui.message("emr.visitDetails", '{{- startDatetime }}', '{{- stopDatetime }}')}
        </div>
    {{ } else { }}

        <div class="status-container">
            <span class="status active"></span> ${ui.message("emr.activeVisit")}
            <i class="icon-time small"></i>
            ${ ui.message("emr.activeVisit.time", '{{- startDatetime }}')}
            
        </div>
        <div class="visit-actions">
            <% activeVisitTasks.each{task -> def url = task.getUrl(emrContext)
                if (!url.startsWith("javascript:")) {
                    url = "/" + contextPath + "/" + url
                }
                // toggle surgical operative note
                if (featureToggles.isFeatureEnabled("surgicalOperativeNote")
                    || task.id != "mirebalais.surgicalOperativeNote") {
            %>
                <a href="${ url }" class="button task">
                    <i class="${task.getIconUrl(emrContext)}"></i> ${ task.getLabel(emrContext) }
                </a>
            <%  }
              }%>
        </div>
   {{  } }}

    <h4>${ ui.message("emr.patientDashBoard.encounters")} </h4>
    <ul id="encountersList">
        {{ _.each(encounters, function(encounter) { }}
            {{ if (!encounter.voided) { }}
            {{= encounterTemplates.displayEncounter(encounter, patient) }}
            {{  } }}
        {{ }); }}
    </ul>
</script>

<script type="text/javascript">
    jq(function(){
        loadTemplates(${ emrContext.activeVisit != null });
    });
</script>

<ul id="visits-list" class="left-menu">
    <% patient.allVisitsUsingWrappers.each { wrapper ->
        def primaryDiagnoses = wrapper.primaryDiagnoses
    %>
        <li class="menu-item viewVisitDetails" visitId="${wrapper.visit.visitId}">
            <span class="menu-date">
                <i class="icon-time"></i>
                ${dateFormat.format(wrapper.visit.startDatetime)}
                <% if(wrapper.visit.stopDatetime != null) { %>
                    - ${dateFormat.format(wrapper.visit.stopDatetime)}
                <% } else { %>
                    (${ ui.message("emr.patientDashBoard.activeSince")} ${timeFormat.format(wrapper.visit.startDatetime)})
                <% } %>
            </span>
            <span class="menu-title">
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

<div id="visit-details" class="main-content">
    <% if (!emrContext.activeVisit) { %>
        <h4>${ ui.message('emr.noActiveVisit') }</h4>
        <p class="spaced">${ ui.message('emr.noActiveVisit.description') }</p>
        <p class="spaced">
            <a href="javascript:visit.showQuickVisitCreationDialog()" class="button task">
                <i class="icon-check-in small"></i>${ ui.message("emr.task.startVisit.label") }
            </a>
        </p>
    <% } %>
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
