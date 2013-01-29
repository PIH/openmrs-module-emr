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
        requestPaperRecordDialog = emr.setupConfirmationDialog({
            selector: '#request-paper-record-dialog',
            actions: {
                confirm: function() {
                    emr.getFragmentActionWithCallback('emr', 'paperrecord/requestPaperRecord', 'requestPaperRecord', { patientId: ${ patient.id }, locationId: sessionLocationModel.id() }, function(data) {
                        emr.successMessage(data.message);
                        requestPaperRecordDialog.close();
                    });
                },
                cancel: function() {
                    requestPaperRecordDialog.close();
                }
            }
        });

        ko.applyBindings( sessionLocationModel, jq('#request-paper-record-dialog').get(0) );
    });

    var showRequestChartDialog = function() {
        requestPaperRecordDialog.show();
        return false;
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

