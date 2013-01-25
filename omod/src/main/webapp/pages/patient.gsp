<%
	ui.decorateWith("emr", "standardEmrPage")

    ui.includeCss("emr", "patientDashboard.css")

    def tabs = [
        [ id: "visits", label: ui.message("emr.patientDashBoard.visits") ],
        [ id: "contactInfo", label: ui.message("emr.patientDashBoard.contactinfo") ]
    ]

%>
<script type="text/javascript">
    var requestPaperRecordDialog = null;
    jq(function() {
        requestPaperRecordDialog = emr.createConfirmationDialog({
            selector: '#request-paper-record-dialog',
            confirmAction: function() {
                emr.getFragmentActionWithCallback('emr', 'paperrecord/requestPaperRecord', 'requestPaperRecord', { patientId: ${ patient.id }, locationId: ${ emrContext.sessionLocation.id } }, function(data) {
                    emr.successMessage(data.message);
                    requestPaperRecordDialog.hide();
                })
            },
            cancelAction: function() {
                requestPaperRecordDialog.hide();
            }
        });
    });

    var showRequestChartDialog = function() {
        requestPaperRecordDialog.show();
    }
</script>

${ ui.includeFragment("emr", "patientHeader", [ patient: patient.patient ]) }

<div class="dashboard-container">
    <aside class="menu">
        <ul class="options">
            <% tabs.each { %>
                <li <% if (it.id == selectedTab) { %> class="selected" <% } %> >
                    <a href="${ ui.pageLink("emr", "patient", [ patientId: patient.id, tab: it.id ]) }">
                        ${ it.label }
                    </a>
                </li>
            <% } %>
        </ul>
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


    <div class="dashboard">

        ${ ui.includeFragment("emr", "patientdashboard/" + selectedTab) }

    </div>
</div>

<div id="request-paper-record-dialog" title="${ ui.message("emr.patientDashBoard.requestPaperRecord.title") }" style="display: none">
    <p>
        <em>${ ui.message("emr.patientDashBoard.requestPaperRecord.confirmTitle") }</em>
    </p>
    <ul>
        <li class="info">
            <span>${ ui.message("emr.patient") }</span>
            <h5>${ ui.format(patient.patient) }</h5>
        </li>
        <li class="info">
            <span>${ ui.message("emr.location") }</span>
            <h5>${ ui.format(emrContext.sessionLocation) }</h5>
        </li>
    </ul>
</div>

