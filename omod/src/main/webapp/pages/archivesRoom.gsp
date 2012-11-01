<%
    ui.decorateWith("emr", "standardEmrPage")

    ui.includeCss("emr", "dataTable.css")

    ui.includeJavascript("emr", "knockout-2.1.0.js")
    ui.includeJavascript("emr", "custom/recordRequest.js")

    def timeFormat = new java.text.SimpleDateFormat("HH:mm")

%>

<script type="text/javascript">
    jq(document).ready( function() {

        jq("#tabs").tabs();

        <% if (createPaperMedicalRecord) {      %>
            jq('#tabs').tabs('select', '#tab-createrequest');
        <% }  %>

        var requests = [];
        <% openRequests.each { %>
             requests.push(RecordRequestModel(
                ${it.requestId}, "${ui.format(it.patient)}", "${ui.format(it.patient.getPatientIdentifier(primaryIdentifierType).identifier)}", "${it.identifier}", "${ui.format(it.requestLocation)}", "${timeFormat.format(it.dateCreated)}", ${it.dateCreated.time}
            ));
        <% } %>

        var pullRequestViewModel = RecordRequestsViewModel(requests);
        ko.applyBindings(pullRequestViewModel, document.getElementById('tab-pullrequest'));
        pullRequestViewModel.selectNumber(10);

        var recordsToCreate = [];
        <% requestsToCreate.each { %>
        recordsToCreate.push(RecordRequestModel(
                ${it.requestId}, "${ui.format(it.patient)}", "${ui.format(it.patient.getPatientIdentifier(primaryIdentifierType).identifier)}", "${it.identifier}", "${ui.format(it.requestLocation)}", "${timeFormat.format(it.dateCreated)}", ${it.dateCreated.time}
        ));
        <% } %>

        var createRequestViewModel = RecordsCreationViewModel(recordsToCreate);
        ko.applyBindings(createRequestViewModel, document.getElementById('tab-createrequest'));
    });
</script>
<style>
    .selected {
        background-color: yellow;
    }

    #pull_requests {
        float:left;
        padding: 0 50px 0 50px;
    }

    #post_requests input[type=submit] {
        margin-top: 30px;
        font-size: 20pt;
    }
</style>

<div id="tabs">

        <ul>
            <li><a href="#tab-pullrequest">Pull Record Requests</a></li>
            <li><a href="#tab-createrequest">Create Record Requests</a></li>
        </ul>

        <div id="tab-pullrequest">
            <h1 id="pull_requests">Pull Record Requests</h1>

            <form id="post_requests" method="post">
                <input type="hidden" name="assignTo" value="${ context.authenticatedUser.person.id }"/>
                <span style="display: none" data-bind="foreach: selectedRequests()">
                    <input type="hidden" name="requestId" data-bind="value: requestId"/>
                </span>
                <input type="submit" value="${ ui.message("emr.pullRecords.pullSelected") }" data-bind="enable: selectedRequests().length > 0"/>
            </form>


            <table id="requests_table" class="dataTable">
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>Dossier ID</th>
                        <th>Send To</th>
                        <th>Time Requested</th>
                    </tr>
                </thead>
                <tbody data-bind="foreach: recordsToPull">
                    <tr data-bind="css:{ selected: selected(), even: (\$index() % 2 == 0) }, click: \$root.selectRequestToBePulled" >
                        <td><span data-bind="text: patientName"></span></td>
                        <td><span data-bind="text: dossierNumber"></span></td>
                        <td><span data-bind="text: sendToLocation"></span></td>
                        <td><span data-bind="text: timeRequested"></span></td>
                    </tr>
                </tbody>
            </table>
        </div>


        <div id="tab-createrequest">
            <h1 id="create_requests">Create Record Requests</h1>

            <form id="post_create_requests" method="post">
                <input type="hidden" name="assignTo" value="${ context.authenticatedUser.person.id }"/>
                <span style="display: none" data-bind="foreach: selectedRequests()">
                    <input type="hidden" name="requestId" data-bind="value: requestId"/>
                </span>
                <input type="hidden" name="createPaperMedicalRecord" value="true">
                <input type="submit" value="${ ui.message("emr.pullRecords.createSelected") }" data-bind="enable: selectedRequests().length > 0"/>
            </form>


            <table id="create_requests_table" class="dataTable">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Patient ID</th>
                    <th>Send To</th>
                    <th>Time Requested</th>
                </tr>
                </thead>
                <tbody data-bind="foreach: recordsToCreate">
                <tr data-bind="css:{ selected: selected(), even: (\$index() % 2 == 0) }, click: \$root.selectRecordsToBeCreated" >
                    <td><span data-bind="text: patientName"></span></td>
                    <td><span data-bind="text: patientId"></span></td>
                    <td><span data-bind="text: sendToLocation"></span></td>
                    <td><span data-bind="text: timeRequested"></span></td>
                </tr>
                </tbody>
            </table>
        </div>
</div>