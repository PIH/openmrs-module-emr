<%
    ui.decorateWith("emr", "standardEmrPage")

    ui.includeJavascript("emr", "knockout-2.1.0.js")
    ui.includeJavascript("emr", "paperrecord/recordRequest.js")
    ui.includeJavascript("emr", "paperrecord/archivesRoom.js")

    ui.includeCss("emr", "archivesRoom.css")

%>

<div id="tabs" xmlns="http://www.w3.org/1999/html">

        <ul>
            <li><a id="tab-selector-create" href="#tab-create">${ ui.message("emr.archivesRoom.newRecords.label") }</a></li>
            <li><a id="tab-selector-pull" href="#tab-pull">${ ui.message("emr.archivesRoom.existingRecords.label") }</a></li>
            <li><a id="tab-selector-merge" href="#tab-merge">${ ui.message("emr.archivesRoom.mergingRecords.label") }</a></li>
            <li><a id="tab-selector-return" href="#tab-return">${ ui.message("emr.archivesRoom.returnRecords.label") }</a></li>
        </ul>

        <div id="tab-create">
            <span id="createrequest">
                <div class="box-align">
                    <h2 id="create_requests">${ ui.message("emr.archivesRoom.newRequests.label") }</h2>

                    <table id="create_requests_table">
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
                    <button id="assign-to-create-button" data-bind="css: { disabled: !isValid() }, enable: isValid()">
                        <i class="icon-chevron-right"></i> <span>${ ui.message("emr.archivesRoom.createSelected") }</span>
                    </button>
                </div>
            </span>

            <div id="assignedcreaterequest" class="box-align">
                <h2 id="under_creation">${ ui.message("emr.archivesRoom.underCreation") }</h2>
                <table id="assigned_create_requests_table">
                    <thead>
                    <tr>
                        <th>${ ui.message("emr.person.name") }</th>
                        <th>${ ui.message("emr.patient.paperRecordIdentifier") }</th>
                        <th>${ ui.message("emr.location") }</th>
                        <th>${ ui.message("emr.time") }</th>
                        <th>&nbsp;</th>
                    </tr>
                    </thead>
                    <tbody data-bind="foreach: assignedRecordsToCreate">
                    <tr data-bind="css:{attr:{'id': dossierNumber}, selected: selected(), hover: hovered(), even: (\$index() % 2 == 0) }, event: { mouseover: \$root.hoverRecords, mouseout: \$root.unHoverRecords }, click: \$root.selectRecordsToBeCreated" >
                        <td><span data-bind="text: patientName"></span></td>
                        <td><span data-bind="text: dossierNumber"></span></td>
                        <td><span data-bind="text: sendToLocation"></span></td>
                        <td><span data-bind="text: timeRequested"></span></td>
                        <td><button data-bind="click: \$root.printLabel" class="print" title="${ui.message("emr.archivesRoom.reprint")}"><i class="icon-print"></i> </button></td>
                    </tr>
                    </tbody>
                </table>
                <div class="sending-records">
                    <h2>${ ui.message("emr.archivesRoom.sendingRecords.label") }</h2>

                    <div id="scan-create-records" class="container">
                        ${ ui.message("emr.archivesRoom.typeOrIdentifyBarCode.label") }
                        <br/>
                        <form class="mark-as-pulled">
                            <input type="text" size="40" name="mark-as-pulled-identifier" class="mark-as-pulled-identifier"/>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <div id="tab-pull">
            <span id="pullrequest">
                <div class="box-align">
                    <h2 id="pull_requests">${ ui.message("emr.archivesRoom.pullRequests.label") }</h2>
                    <table id="pull_requests_table">
                        <thead>
                        <tr>
                            <th>${ ui.message("emr.archivesRoom.surname.label") }</th>
                            <th>${ ui.message("emr.patient.paperRecordIdentifier") }</th>
                            <th>${ ui.message("emr.archivesRoom.requestedBy.label") }</th>
                            <th>${ ui.message("emr.time") }</th>
                        </tr>
                        </thead>
                        <tbody data-bind="foreach: recordsToPull">
                        <tr data-bind="css:{attr:{'id': dossierNumber}, selected: selected(), hover: hovered(), even: (\$index() % 2 == 0) }, event: { mouseover: \$root.hoverRecords, mouseout: \$root.unHoverRecords  }, click: \$root.selectRequestToBePulled" >
                            <td><span data-bind="text: patientName"></span></td>
                            <td><span data-bind="text: dossierNumber"></span></td>
                            <td><span data-bind="text: sendToLocation"></span></td>
                            <td><span data-bind="text: timeRequested"></span></td>
                        </tr>
                        </tbody>
                    </table>
                </div>
                <div class="box-align btn">
                    <button id="assign-to-pull-button" class="print" data-bind="css: { disabled: !isValid() }, enable: isValid()">
                        <i class="icon-chevron-right"></i> <span>${ ui.message("emr.archivesRoom.pullSelected") }</span>
                    </button>
                </div>
            </span>
                <div id="assignedpullrequest" class="box-align">
                    <h2 id="assigned_pull_requests">${ ui.message("emr.archivesRoom.assignedPullRequests.label") }</h2>


                    <table id="assigned_pull_requests_table" >
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
                            <td><button data-bind="click: \$root.printLabel" class="print" title="${ui.message("emr.archivesRoom.reprint")}"><i class="icon-print"></i> </button></td>
                        </tr>
                        </tbody>
                    </table>
                    <div class="sending-records">
                        <h2>${ ui.message("emr.archivesRoom.sendingRecords.label") }</h2>

                        <div id="scan-pull-records" class="container">
                            ${ ui.message("emr.archivesRoom.typeOrIdentifyBarCode.label") }
                            <br/>
                            <form class="mark-as-pulled">
                                <input type="text" size="40" name="mark-as-pulled-identifier" class="mark-as-pulled-identifier"/>
                            </form>
                        </div>
                    </div>
                </div>
        </div>
        <div id="tab-merge">
            <div id="mergeRequests">
                <h2> Physical records missing merge: </h2>
                <ul>
                    <li class="date">
                       <h4>12/01</h4>
                    </li>
                    <li class="row">
                        <div class="patient">
                           <div class="content"> <i class="icon-folder-open medium"></i></div><div class="content"> #A001231 <br/> Smith, John </div>
                        </div>
                        <div class="arrow">
                            <i class="icon-chevron-right medium"></i>
                        </div>
                        <div class="patient">
                            <div class="content"> <i class="icon-folder-open medium"></i></div><div class="content"> #A001231 <br/> Smith, John </div>
                        </div>
                        <div class="checkbox-done">
                            <input type="checkbox" />
                            <label>Done</label>
                        </div>
                    </li>
                </ul>
            </div>
        </div>

        <div id="tab-return">

                <h2>${ ui.message("emr.archivesRoom.returningRecords.label") }</h2>

                <div id="scan-returned-records" class="box-align">
                    ${ ui.message("emr.archivesRoom.typeOrIdentifyBarCode.label") }
                    <br/>
                    <form class="mark-as-returned">
                        <input type="text" size="40" name="mark-as-returned-identifier" class="mark-as-returned-identifier"/>
                    </form>
                </div>

        </div>

</div>
