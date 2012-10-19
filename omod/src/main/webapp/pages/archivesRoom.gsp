<%
    ui.decorateWith("emr", "standardEmrPage")

    ui.includeJavascript("emr", "knockout-2.1.0.js")
    ui.includeJavascript("emr", "custom/recordRequest.js")

    def timeFormat = new java.text.SimpleDateFormat("HH:mm")

%>

<script type="text/javascript">
    jq(document).ready( function() {
        var requests = [];

        <% openRequests.each { %>
            requests.push(RecordRequestModel(
                ${it.requestId}, "${ui.format(it.patient)}", "${it.identifier}", "${ui.format(it.requestLocation)}", "${timeFormat.format(it.dateCreated)}", ${it.dateCreated.time}
            ));
        <% } %>

        var viewModel = RecordRequestsViewModel(requests);
        ko.applyBindings(viewModel);
    });
</script>
<style>
    .selected {
        background-color: yellow;
    }
</style>

<h1>Pull Record Requests</h1>
<table >
    <thead>
        <tr>
            <th>Name</th>
            <th>Dossier ID</th>
            <th>Send To</th>
            <th>Time Requested</th>
        </tr>
    </thead>
    <tbody data-bind="foreach: requests">
        <tr data-bind="css:{ selected: selected() }, click: \$root.selectRequestToBePulled" >
            <td><span data-bind="text: patientName"></span></td>
            <td><span data-bind="text: dossierNumber"></span></td>
            <td><span data-bind="text: sendToLocation"></span></td>
            <td><span data-bind="text: timeRequested"></span></td>
        </tr>
    </tbody>
</table>

<form method="post">
    <span style="display: none" data-bind="foreach: selectedRequests">
        <input type="hidden" name="requestId" data-bind="value: requestId"/>
    </span>
    <input type="submit" value="${ ui.message("emr.pullRecords.pullSelected") }"/>
</form>