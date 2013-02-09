<%
	ui.decorateWith("emr", "standardEmrPage")

    ui.includeCss("emr", "patientDashboard.css")

    ui.includeJavascript("emr", "patient.js")

    def tabs = [
        [ id: "visits", label: ui.message("emr.patientDashBoard.visits") ],
        [ id: "contactInfo", label: ui.message("emr.patientDashBoard.contactinfo") ]
    ]

%>
<script type="text/javascript">

    jq(function(){
        jq(".tabs").tabs();
        createPaperRecordDialog(${patient.id});
        ko.applyBindings( sessionLocationModel, jq('#request-paper-record-dialog').get(0) );
    });

</script>

${ ui.includeFragment("emr", "patientHeader", [ patient: patient.patient ]) }
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
            ${ ui.includeFragment("emr", "patientdashboard/" + it.id) }
        </div>
        <% } %>

    </div>
    <aside>
        <div class="actions">
            <strong>${ ui.message("emr.patientDashBoard.actions") }</strong>
                <% availableTasks.each {
                    def url = it.getUrl(emrContext)
                    if (!url.startsWith("javascript:")) {
                        url = "/" + contextPath + "/" + url
                    }
                %>
            <div><a href="${ url }">${ it.getLabel(emrContext) }</a></div>
            <% } %>
        </div>
    </aside>
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

