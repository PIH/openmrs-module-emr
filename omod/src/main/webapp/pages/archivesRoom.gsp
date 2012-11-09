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

        var recordsToPull = [];
        <% openRequestsToPull.each { %>
             recordsToPull.push(RecordRequestModel(
                ${it.requestId}, "${ui.format(it.patient)}", "${ui.format(it.patient.getPatientIdentifier(primaryIdentifierType).identifier)}", "${it.identifier}", "${ui.format(it.requestLocation)}", "${timeFormat.format(it.dateCreated)}", ${it.dateCreated.time}
            ));
        <% } %>

        var pullRequestsViewModel = PullRequestsViewModel(recordsToPull);
        ko.applyBindings(pullRequestsViewModel, document.getElementById('tab-pullrequest'));
        pullRequestsViewModel.selectNumber(10);

        var recordsToCreate = [];
        <% openRequestsToCreate.each { %>
            recordsToCreate.push(RecordRequestModel(
                    ${it.requestId}, "${ui.format(it.patient)}", "${ui.format(it.patient.getPatientIdentifier(primaryIdentifierType).identifier)}", "${it.identifier}", "${ui.format(it.requestLocation)}", "${timeFormat.format(it.dateCreated)}", ${it.dateCreated.time}
            ));
        <% } %>

        var createRequestsViewModel = CreateRequestsViewModel(recordsToCreate);
        ko.applyBindings(createRequestsViewModel, document.getElementById('tab-createrequest'));

        var assignedRecordsToPull = [];
        <% assignedRequestsToPull.each { %>
            assignedRecordsToPull.push(RecordRequestModel(
                    ${it.requestId}, "${ui.format(it.patient)}", "${ui.format(it.patient.getPatientIdentifier(primaryIdentifierType).identifier)}", "${it.identifier}", "${ui.format(it.requestLocation)}", "${timeFormat.format(it.dateCreated)}", ${it.dateCreated.time}
            ));
        <% } %>

        var assignedPullRequestsViewModel = AssignedPullRequestsViewModel(assignedRecordsToPull);
        ko.applyBindings(assignedPullRequestsViewModel, document.getElementById('tab-assignedpullrequest'));
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
            <li><a id="tab-selector-createrequest" href="#tab-createrequest">${ ui.message("emr.archivesRoom.openCreateRequests.label") }</a></li>
            <li><a id="tab-selector-pullrequest" href="#tab-pullrequest">${ ui.message("emr.archivesRoom.openPullRequests.label") }</a></li>
            <li><a id="tab-selector-assignedpullrequest" href="#tab-assignedpullrequest">${ ui.message("emr.archivesRoom.assignedPullRequests.label") }</a></li>
        </ul>


        <div id="tab-createrequest">
            <h1 id="create_requests">${ ui.message("emr.archivesRoom.openCreateRequests.label") }</h1>

            <form id="post_create_requests" method="post">
                <input type="hidden" name="assignTo" value="${ context.authenticatedUser.person.id }"/>
                <span style="display: none" data-bind="foreach: selectedRequests()">
                    <input type="hidden" name="requestId" data-bind="value: requestId"/>
                </span>
                <input type="hidden" name="createPaperMedicalRecord" value="true">
                <input id="create_record_requests_button" type="submit" value="${ ui.message("emr.archivesRoom.createSelected") }"
                       data-bind="enable: selectedRequests().length > 0"/>
            </form>


            <table id="create_requests_table" class="dataTable">
                <thead>
                <tr>
                    <th>${ ui.message("emr.person.name") }</th>
                    <th>${ ui.message("emr.patient.identifier") }</th>
                    <th>${ ui.message("emr.location") }</th>
                    <th>${ ui.message("emr.time") }</th>
                </tr>
                </thead>
                <tbody data-bind="foreach: recordsToCreate">
                <tr data-bind="css:{attr:{'id': dossierNumber}, selected: selected(), even: (\$index() % 2 == 0) }, click: \$root.selectRecordsToBeCreated" >
                    <td><span data-bind="text: patientName"></span></td>
                    <td><span data-bind="text: patientId"></span></td>
                    <td><span data-bind="text: sendToLocation"></span></td>
                    <td><span data-bind="text: timeRequested"></span></td>
                </tr>
                </tbody>
            </table>
        </div>

        <div id="tab-pullrequest">
            <h1 id="pull_requests">${ ui.message("emr.archivesRoom.openPullRequests.label") }</h1>

            <form id="post_pull_requests" method="post">
                <input type="hidden" name="assignTo" value="${ context.authenticatedUser.person.id }"/>
                <!-- ko foreach:selectedRequests -->
                <input type="hidden" name="requestId" data-bind="value: requestId"/>
                <!-- /ko -->
                <input id="pull_record_requests_button" type="submit" value="${ ui.message("emr.archivesRoom.pullSelected") }"
                       data-bind="enable: selectedRequests().length > 0"/>
            </form>


            <table id="pull_requests_table" class="dataTable">
                <thead>
                    <tr>
                        <th>${ ui.message("emr.person.name") }</th>
                        <th>${ ui.message("emr.patient.paperRecordIdentifier") }</th>
                        <th>${ ui.message("emr.location") }</th>
                        <th>${ ui.message("emr.time") }</th>
                    </tr>
                </thead>
                <tbody data-bind="foreach: recordsToPull">
                    <tr data-bind="attr:{'id': dossierNumber}, css:{ selected: selected(), even: (\$index() % 2 == 0) }, click: \$root.selectRequestToBePulled" >
                        <td><span data-bind="text: patientName"></span></td>
                        <td><span data-bind="text: dossierNumber"></span></td>
                        <td><span data-bind="text: sendToLocation"></span></td>
                        <td><span data-bind="text: timeRequested"></span></td>
                    </tr>
                </tbody>
            </table>
        </div>


        <div id="tab-assignedpullrequest">
            <h1 id="assigned_pull_requests">${ ui.message("emr.archivesRoom.assignedPullRequests.label") }</h1>


            <table id="assigned_pull_requests_table" class="dataTable">
                <thead>
                <tr>
                    <th>${ ui.message("emr.person.name") }</th>
                    <th>${ ui.message("emr.patient.paperRecordIdentifier") }</th>
                    <th>${ ui.message("emr.location") }</th>
                    <th>${ ui.message("emr.time") }</th>
                </tr>
                </thead>
                <tbody data-bind="foreach: assignedRecordsToPull">
                <tr data-bind="css:{attr:{'id': dossierNumber}, selected: selected(), even: (\$index() % 2 == 0) }, click: \$root.selectRecordsToBeCreated" >
                    <td><span data-bind="text: patientName"></span></td>
                    <td><span data-bind="text: dossierNumber"></span></td>
                    <td><span data-bind="text: sendToLocation"></span></td>
                    <td><span data-bind="text: timeRequested"></span></td>
                </tr>
                </tbody>
            </table>
        </div>


</div>