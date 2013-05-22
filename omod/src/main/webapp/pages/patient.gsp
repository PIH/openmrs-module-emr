<%
	ui.decorateWith("appui", "standardEmrPage")

    ui.includeCss("mirebalais", "patientDashboard.css")

    ui.includeJavascript("emr", "patient.js")
    ui.includeJavascript("emr", "custom/visits.js")
    ui.includeJavascript("emr", "bootstrap-collapse.js")
    ui.includeJavascript("emr", "bootstrap-transition.js")

    def tabs = []

    tabs.add([ id: "visits", label: ui.message("emr.patientDashBoard.visits"), app: "emr", fragment: "patientdashboard/visits" ])

    if (featureToggles.isFeatureEnabled("radiologyTab")) {
        tabs.add([ id: "radiologyTab", label: ui.message("radiologyapp.radiology.label"), app: "radiologyapp", fragment: "radiologyTab" ])
    }

    tabs.add([ id: "contactInfo", label: ui.message("emr.patientDashBoard.contactinfo"), app: "emr", fragment: "patientdashboard/contactInfo" ])


%>
<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.format(patient.patient.familyName) }, ${ ui.format(patient.patient.givenName) }" , link: '${ui.pageLink("emr", "patient", [patientId: patient.patient.id])}'}
    ];

    jq(function(){
        jq(".tabs").tabs();
        createPaperRecordDialog(${patient.id});
        visit.createQuickVisitCreationDialog(${patient.id});
        ko.applyBindings( sessionLocationModel, jq('#request-paper-record-dialog').get(0) );
    });

    var patient = { id: ${ patient.id } };
</script>



${ ui.includeFragment("emr", "patientHeader", [ patient: patient.patient ]) }

<div class="actions">
    <span class="dropdown-name"><i class="icon-cog"></i>${ ui.message("emr.actions") }<i class="icon-sort-down"></i></span>
    <ul>
        <% if (!emrContext.activeVisit) { %>
            <li>
                <a href="javascript:visit.showQuickVisitCreationDialog()">
                    <i class="icon-check-in"></i>${ ui.message("emr.task.startVisit.label") }
                </a>
            </li>
        <% } %>

        <% availableTasks.each {
            def url = it.getUrl(emrContext)
            if (!url.startsWith("javascript:")) {
                url = "/" + contextPath + "/" + url
            }
        %>
        <li>
            <a href="${ url }">
                <i class="${ it.getIconUrl(emrContext) }"></i>
                ${ it.getLabel(emrContext) }
            </a>
        </li>
        <% } %>
    </ul>
</div>

<div class="tabs" xmlns="http://www.w3.org/1999/html">
    <div class="dashboard-container">

        <ul>
            <% tabs.each { %>
                <li>
                    <a href="#${ it.id }">
                        ${ it.label }
                    </a>
                </li>
            <% } %>
        </ul>

        <% tabs.each { %>
        <div id="${it.id}">
            ${ ui.includeFragment(it.app, it.fragment) }
        </div>
        <% } %>
    </div>
</div>

<div id="request-paper-record-dialog" class="dialog" style="display: none">
    <div class="dialog-header">
        <i class="icon-folder-open"></i>
        <h3>${ ui.message("emr.patientDashBoard.requestPaperRecord.title") }</h3>
    </div>
    <div class="dialog-content">
        <p class="dialog-instructions">${ ui.message("emr.patientDashBoard.requestPaperRecord.confirmTitle") }</p>
        <ul>
            <li class="info">
                <span>${ ui.message("emr.patient") }</span>
                <h5>${ ui.format(patient.patient) }</h5>
            </li>
            <li class="info">
                <span>${ ui.message("emr.location") }</span>
                <h5 data-bind="text: text"></h5>
            </li>
        </ul>

        <button class="confirm right">${ ui.message("emr.confirm") }</button>
        <button class="cancel">${ ui.message("emr.cancel") }</button>
    </div>
</div>

<div id="quick-visit-creation-dialog" class="dialog">
    <div class="dialog-header">
        <h3>
            <i class="icon-folder-open"></i>
            ${ ui.message("emr.visit.createQuickVisit.title") }</h3>
    </div>
    <div class="dialog-content">
        <p class="dialog-instructions">${ ui.message("emr.task.startVisit.message", ui.format(patient.patient)) }</p>

        <button class="confirm right">${ ui.message("emr.confirm") }</button>
        <button class="cancel">${ ui.message("emr.cancel") }</button>
    </div>
</div>
