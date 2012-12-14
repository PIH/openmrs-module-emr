<%
    ui.decorateWith("emr", "standardEmrPage")

    ui.includeJavascript("emr", "knockout-2.1.0.js")
    ui.includeJavascript("emr", "custom/recordRequest.js")

    def timeFormat = new java.text.SimpleDateFormat("HH:mm")

%>

<script type="text/javascript">
    jq(document).ready( function() {

        jq("#tabs").tabs();

        jq('#tabs').tabs('select', '#tab-${ activeTab }');


        var recordsToPull = [];
        <% openRequestsToPull.each { %>
             recordsToPull.push(RecordRequestModel(
                ${it.requestId}, "${ui.format(it.patient)}", "${ui.format(it.patient.getPatientIdentifier(primaryIdentifierType).identifier)}", "${it.identifier}", "${ui.format(it.requestLocation)}", "${timeFormat.format(it.dateCreated)}", ${it.dateCreated.time}
            ));
        <% } %>

        var pullRequestsViewModel = PullRequestsViewModel(recordsToPull);
        ko.applyBindings(pullRequestsViewModel, document.getElementById('pullrequest'));
        pullRequestsViewModel.selectNumber(10);

        var recordsToCreate = [];
        <% openRequestsToCreate.each { %>
            recordsToCreate.push(RecordRequestModel(
                    ${it.requestId}, "${ui.format(it.patient)}", "${ui.format(it.patient.getPatientIdentifier(primaryIdentifierType).identifier)}", "${it.identifier}", "${ui.format(it.requestLocation)}", "${timeFormat.format(it.dateCreated)}", ${it.dateCreated.time}
            ));
        <% } %>

        var createRequestsViewModel = CreateRequestsViewModel(recordsToCreate);
        ko.applyBindings(createRequestsViewModel, document.getElementById('createrequest'));

        var assignedRecordsToPull = [];
        <% assignedRequestsToPull.each { %>
            assignedRecordsToPull.push(RecordRequestModel(
                    ${it.requestId}, "${ui.format(it.patient)}", "${ui.format(it.patient.getPatientIdentifier(primaryIdentifierType).identifier)}", "${it.identifier}", "${ui.format(it.requestLocation)}", "${timeFormat.format(it.dateCreated)}", ${it.dateCreated.time}
            ));
        <% } %>

        var assignedPullRequestsViewModel = AssignedPullRequestsViewModel(assignedRecordsToPull);
        ko.applyBindings(assignedPullRequestsViewModel, document.getElementById('assignedpullrequest'));

        var assignedRecordsToCreate = [];
        <% assignedRequestsToCreate.each { %>
        assignedRecordsToCreate.push(RecordRequestModel(
                ${it.requestId}, "${ui.format(it.patient)}", "${ui.format(it.patient.getPatientIdentifier(primaryIdentifierType).identifier)}", "${it.identifier}", "${ui.format(it.requestLocation)}", "${timeFormat.format(it.dateCreated)}", ${it.dateCreated.time}
        ));
        <% } %>

        var assignedCreateRequestsViewModel = AssignedCreateRequestsViewModel(assignedRecordsToCreate);
        ko.applyBindings(assignedCreateRequestsViewModel, document.getElementById('assignedcreaterequest'));

        // handle entering bar codes
        jq("#mark-as-pulled").submit(function (e) {

            e.preventDefault();

            var identifier = jq('#mark-as-pulled-identifier').val();

            if (identifier) {
                jq.ajax({
                    url: '${ ui.actionLink('emr','paperrecord/archivesRoom','markPaperRecordRequestAsSent') }',
                    data: { identifier: identifier },
                    dataType: 'json',
                    type: 'POST'
                })
                        .success(function(data) {
                            jq('#mark-as-pulled-identifier').val('');
                            var options = [];
                            options.close = function () { window.location.href = '${ ui.pageLink('emr','paperrecord/archivesRoom', [ activeTab : 'pull' ]) }'; }
                            emr.successAlert(data.message, options);
                        })
                        .error(function(xhr, status, err) {
                            jq('#mark-as-pulled-identifier').val('');
                            emr.errorAlert(jq.parseJSON(xhr.responseText).globalErrors[0]);
                        })
            }
        });

        // if an alphanumeric character is pressed, send focus to the mark-as-pulled-identifier input box
        jq(document).keydown(function(event) {
            if (event.which > 47 && event.which < 91) {
                jq("#mark-as-pulled-identifier").focus();
            }
        })

    });
</script>
<div id="tabs">

        <ul>
            <li><a id="tab-selector-create" href="#tab-create">${ ui.message("emr.archivesRoom.newRecords.label") }</a></li>
            <li><a id="tab-selector-pull" href="#tab-pull">${ ui.message("emr.archivesRoom.existingRecords.label") }</a></li>
        </ul>

        <div id="tab-create">
            <span id="createrequest">
                <div class="box-align">
                    <h2 id="create_requests">${ ui.message("emr.archivesRoom.newRequests.label") }</h2>

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
                        <tr data-bind="css:{attr:{'id': dossierNumber}, selected: selected(), hover: hovered(), even: (\$index() % 2 == 0) }, event: { mouseover: \$root.hoverRecords, mouseout: \$root.unHoverRecords }, click: \$root.selectRecordsToBeCreated" >
                            <td><span data-bind="text: patientName"></span></td>
                            <td><span data-bind="text: patientId"></span></td>
                            <td><span data-bind="text: sendToLocation"></span></td>
                            <td><span data-bind="text: timeRequested"></span></td>
                        </tr>
                        </tbody>
                    </table>
                </div>
                <div class="box-align btn">
                    <form id="post_create_requests" action="archivesRoom.page" method="post">
                        <input type="hidden" name="assignTo" value="${ context.authenticatedUser.person.id }"/>
                        <span style="display: none" data-bind="foreach: selectedRequests()">
                            <input type="hidden" name="requestId" data-bind="value: requestId"/>
                        </span>
                        <input type="hidden" name="activeTab" value="create">
                        <button id="next" data-bind="css: { disabled: !isValid() }, enable: isValid()"><i class="icon-chevron-right"></i> <span>${ ui.message("emr.archivesRoom.createSelected") }</span></button>
                    </form>
                </div>
            </span>

            <div id="assignedcreaterequest" class="box-align">
                <h2 id="under_creation">${ ui.message("emr.archivesRoom.underCreation") }</h2>
                <table id="assigned_create_requests_table" class="dataTable">
                    <thead>
                    <tr>
                        <th>${ ui.message("emr.person.name") }</th>
                        <th>${ ui.message("emr.patient.identifier") }</th>
                        <th>${ ui.message("emr.location") }</th>
                        <th>${ ui.message("emr.time") }</th>
                    </tr>
                    </thead>
                    <tbody data-bind="foreach: assignedRecordsToCreate">
                    <tr data-bind="css:{attr:{'id': dossierNumber}, selected: selected(), even: (\$index() % 2 == 0) }, click: \$root.selectRecordsToBeCreated" >
                        <td><span data-bind="text: patientName"></span></td>
                        <td><span data-bind="text: patientId"></span></td>
                        <td><span data-bind="text: sendToLocation"></span></td>
                        <td><span data-bind="text: timeRequested"></span></td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>

        <div id="tab-pull">

            <div id="pullrequest">
                <h2 id="pull_requests">${ ui.message("emr.archivesRoom.openPullRequests.label") }</h2>

                <form id="post_pull_requests" action="archivesRoom.page" method="post">
                    <input type="hidden" name="assignTo" value="${ context.authenticatedUser.person.id }"/>
                    <!-- ko foreach:selectedRequests -->
                    <input type="hidden" name="requestId" data-bind="value: requestId"/>
                    <!-- /ko -->
                    <input type="hidden" name="activeTab" value="pull">
                    <input id="pull_record_requests_button" type="submit" value="${ ui.message("emr.archivesRoom.pullSelected") }"
                           data-bind="enable: selectedRequests().length > 0"/>
                </form>


                <table id="pull_requests_table" class="dataTable">
                    <thead>
                    <tr>
                        <th>${ ui.message("emr.archivesRoom.surname.label") }</th>
                        <th>${ ui.message("emr.patient.paperRecordIdentifier") }</th>
                        <th>${ ui.message("emr.archivesRoom.requestedBy.label") }</th>
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

            <div id="assignedpullrequest">
                <h2 id="assigned_pull_requests">${ ui.message("emr.archivesRoom.assignedPullRequests.label") }</h2>


                <table id="assigned_pull_requests_table" class="dataTable">
                    <thead>
                    <tr>
                        <th>${ ui.message("emr.archivesRoom.surname.label") }</th>
                        <th>${ ui.message("emr.patient.paperRecordIdentifier") }</th>
                        <th>${ ui.message("emr.archivesRoom.requestedBy.label") }</th>
                        <th>${ ui.message("emr.time") }</th>
                        <th>&nbsp;</th>
                    </tr>
                    </thead>
                    <tbody data-bind="foreach: assignedRecordsToPull">
                    <tr data-bind="css:{attr:{'id': dossierNumber}, selected: selected(), even: (\$index() % 2 == 0) }" >
                        <td><span data-bind="text: patientName"></span></td>
                        <td><span data-bind="text: dossierNumber"></span></td>
                        <td><span data-bind="text: sendToLocation"></span></td>
                        <td><span data-bind="text: timeRequested"></span></td>
                        <td><button>${ ui.message('emr.archivesRoom.reprint.button') }</button></td>
                    </tr>
                    </tbody>
                </table>

                <h2>${ ui.message("emr.archivesRoom.sendingRecords.label") }</h2>

                <div id="scan-pull-records" class="container">
                    ${ ui.message("emr.archivesRoom.typeOrIdentifyBarCode.label") }
                    <br/>
                    <form id="mark-as-pulled">
                        <input type="text" size="40" id="mark-as-pulled-identifier" name="mark-as-pulled-identifier"/>
                    </form>
                </div>

            </div>

        </div>

</div>
