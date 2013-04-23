<%
    ui.decorateWith("emr", "standardEmrPage")

    ui.includeJavascript("emr", "knockout-2.1.0.js")
    ui.includeJavascript("emr", "paperrecord/recordRequest.js")
    ui.includeJavascript("emr", "paperrecord/archivesRoom.js")

    ui.includeCss("mirebalais", "archivesRoom.css")

%>
<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("emr.app.archivesRoom.label")}"}
    ];
</script>
<div id="tabs" xmlns="http://www.w3.org/1999/html">

        <ul>
            <li><a id="tab-selector-create" href="#tab-create">${ ui.message("emr.archivesRoom.newRecords.label") }</a></li>
            <li><a id="tab-selector-pull" href="#tab-pull">${ ui.message("emr.archivesRoom.existingRecords.label") }</a></li>
            <li><a id="tab-selector-return" href="#tab-return">${ ui.message("emr.archivesRoom.returnRecords.label") }</a></li>
            <li><a id="tab-selector-merge" href="#tab-merge">${ ui.message("emr.archivesRoom.mergingRecords.label") }</a></li>
        </ul>

        <div id="tab-create">
            <span id="createrequest">
                <div class="instructions">
                    <span class="instruction">
                        <strong>1.</strong>
                        <span class="instruction-text">${ ui.message("emr.archivesRoom.selectRecords.label") }</span>
                    </span>
                    <span class="instruction">
                        <strong>2.</strong>
                        <span class="instruction-text">${ ui.message("emr.archivesRoom.printRecords.label") }</span>
                    </span>
                    <span class="instruction">
                        <strong>3.</strong>
                        <span class="instruction-text">${ ui.message("emr.archivesRoom.sendRecords.label") }</span>
                    </span>
                </div>
                <div class="box-align">
                    <table id="create_requests_table">
                        <thead>
                        <tr>
                            <th>${ ui.message("emr.person.name") }</th>
                            <th>${ ui.message("emr.patient.identifier") }</th>
                            <th>${ ui.message("emr.location") }</th>
                            <th>${ ui.message("emr.time") }</th>
                            <th>&nbsp;</th>
                        </tr>
                        </thead>
                        <tbody data-bind="foreach: recordsToCreate">
                        <tr data-bind="css:{attr:{'id': dossierNumber}, selected: selected(), hover: hovered() }" >
                            <td data-bind="event: { mouseover: \$root.hoverRecords, mouseout: \$root.unHoverRecords }, click: \$root.selectRecordsToBeCreated"><span data-bind="text: patientName"></span></td>
                            <td data-bind="event: { mouseover: \$root.hoverRecords, mouseout: \$root.unHoverRecords }, click: \$root.selectRecordsToBeCreated"><span data-bind="text: patientId"></span></td>
                            <td data-bind="event: { mouseover: \$root.hoverRecords, mouseout: \$root.unHoverRecords }, click: \$root.selectRecordsToBeCreated"><span data-bind="text: sendToLocation"></span></td>
                            <td data-bind="event: { mouseover: \$root.hoverRecords, mouseout: \$root.unHoverRecords }, click: \$root.selectRecordsToBeCreated"><span data-bind="text: timeRequested"></span></td>
                            <td><i data-bind="click: \$root.cancelRequest" class="delete-item icon-remove" title="${ui.message("emr.archivesRoom.cancel")}"></i></td>
                        </tr>
                        </tbody>
                    </table>
                </div>
                <div class="box-align btn">
                    <button id="assign-to-create-button" class="arrow" data-bind="css: { disabled: !isValid() }, enable: isValid()">
                        <i class="icon-print"></i> <span>${ ui.message("emr.archivesRoom.printSelected") }</span>
                        <span class="arrow-border-button"></span>
                        <span class="arrow-button"></span>
                    </button>
                </div>
            </span>
            <div id="assignedcreaterequest" class="box-align">
                <div class="sending-records scan-input">
                    <div id="scan-create-records" class="container">
                        <form class="mark-as-pulled">
                            <input type="text" size="40" name="mark-as-pulled-identifier" class="mark-as-pulled-identifier" placeholder="${ ui.message("emr.archivesRoom.typeOrIdentifyBarCode.label") }"/>
                        </form>
                    </div>
                </div>
                <table id="assigned_create_requests_table">
                    <thead>
                    <tr>
                        <th>${ ui.message("emr.person.name") }</th>
                        <th>${ ui.message("emr.patient.paperRecordIdentifier") }</th>
                        <th>${ ui.message("emr.location") }</th>
                        <th>${ ui.message("emr.time") }</th>
                        <th>&nbsp;</th>
                        <th>&nbsp;</th>
                    </tr>
                    </thead>
                    <tbody data-bind="foreach: assignedRecordsToCreate">
                    <tr data-bind="css:{attr:{'id': dossierNumber}, selected: selected() }" >
                        <td><span data-bind="text: patientName"></span></td>
                        <td><span data-bind="text: dossierNumber"></span></td>
                        <td><span data-bind="text: sendToLocation"></span></td>
                        <td><span data-bind="text: timeRequested"></span></td>
                        <td><button data-bind="click: \$root.printLabel" class="print" title="${ui.message("emr.archivesRoom.reprint")}"><i class="icon-print"></i> </button></td>
                        <td><i data-bind="click: \$root.cancelRequest" class="delete-item icon-remove" title="${ui.message("emr.archivesRoom.cancel")}"></i></td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>

        <div id="tab-pull">
            <span id="pullrequest">
                <div class="instructions">
                    <span class="instruction">
                        <strong>1.</strong>
                        <span class="instruction-text">${ ui.message("emr.archivesRoom.selectRecordsPull.label") }</span>
                    </span>
                    <span class="instruction">
                        <strong>2.</strong>
                        <span class="instruction-text">${ ui.message("emr.archivesRoom.clickRecordsPull.label") }</span>
                    </span>
                    <span class="instruction">
                        <strong>3.</strong>
                        <span class="instruction-text">${ ui.message("emr.archivesRoom.sendRecords.label") }</span>
                    </span>
                </div>
                <div class="box-align">
                    <table id="pull_requests_table">
                        <thead>
                        <tr>
                            <th>${ ui.message("emr.person.name") }</th>
                            <th>${ ui.message("emr.patient.paperRecordIdentifier") }</th>
                            <th>${ ui.message("emr.archivesRoom.requestedBy.label") }</th>
                            <th>${ ui.message("emr.time") }</th>
                            <th>&nbsp;</th>
                        </tr>
                        </thead>
                        <tbody data-bind="foreach: recordsToPull">
                        <tr data-bind="css:{attr:{'id': dossierNumber}, selected: selected(), hover: hovered() }" >
                            <td data-bind="event: { mouseover: \$root.hoverRecords, mouseout: \$root.unHoverRecords }, click: \$root.selectRequestToBePulled"><span data-bind="text: patientName"></span></td>
                            <td data-bind="event: { mouseover: \$root.hoverRecords, mouseout: \$root.unHoverRecords }, click: \$root.selectRequestToBePulled"><span data-bind="text: dossierNumber"></span></td>
                            <td data-bind="event: { mouseover: \$root.hoverRecords, mouseout: \$root.unHoverRecords }, click: \$root.selectRequestToBePulled"><span data-bind="text: sendToLocation"></span></td>
                            <td data-bind="event: { mouseover: \$root.hoverRecords, mouseout: \$root.unHoverRecords }, click: \$root.selectRequestToBePulled"><span data-bind="text: timeRequested"></span></td>
                            <td><i data-bind="click: \$root.cancelRequest" class="delete-item icon-remove" title="${ui.message("emr.archivesRoom.cancel")}"></i></td>
                        </tr>
                        <tr data-bind="css:{attr:{'id': dossierNumber}, selected: selected(), hover: hovered() }">
                            <td colspan="5" data-bind="visible: locationLastSent" ><span data-bind="text: dossierNumber"></span> ${ ui.message("emr.archivesRoom.sentTo") } <span data-bind="text: locationLastSent"></span> ${ ui.message("emr.archivesRoom.at") } <span data-bind="text: dateLastSent"></span></td>
                        </tr>
                        </tbody>
                    </table>
                </div>
                <div class="box-align btn">
                    <button id="assign-to-pull-button" class="arrow" data-bind="css: { disabled: !isValid() }, enable: isValid()">
                        <i class="icon-folder-open"></i> <span>${ ui.message("emr.archivesRoom.pullSelected") }</span>
                        <span class="arrow-border-button"></span>
                        <span class="arrow-button"></span>
                    </button>
                </div>
            </span>
                <div id="assignedpullrequest" class="box-align">
                    <div class="sending-records scan-input">
                        <div id="scan-pull-records" class="container">
                            <form class="mark-as-pulled">
                                <input type="text" size="40" name="mark-as-pulled-identifier" class="mark-as-pulled-identifier" placeholder="${ ui.message("emr.archivesRoom.typeOrIdentifyBarCode.label") }"/>
                            </form>
                        </div>
                    </div>
                    <table id="assigned_pull_requests_table" >
                        <thead>
                        <tr>
                            <th>${ ui.message("emr.person.name") }</th>
                            <th>${ ui.message("emr.patient.paperRecordIdentifier") }</th>
                            <th>${ ui.message("emr.archivesRoom.requestedBy.label") }</th>
                            <th>${ ui.message("emr.time") }</th>
                            <th>&nbsp;</th>
                            <th>&nbsp;</th>
                        </tr>
                        </thead>
                        <tbody data-bind="foreach: assignedRecordsToPull">
                        <tr data-bind="css:{attr:{'id': dossierNumber}, selected: selected() }" >
                            <td><span data-bind="text: patientName"></span></td>
                            <td><span data-bind="text: dossierNumber"></span></td>
                            <td><span data-bind="text: sendToLocation"></span></td>
                            <td><span data-bind="text: timeRequested"></span></td>
                            <td><button data-bind="click: \$root.printLabel" class="print" title="${ui.message("emr.archivesRoom.reprint")}"><i class="icon-print"></i> </button></td>
                            <td><i data-bind="click: \$root.cancelRequest" class="delete-item icon-remove" title="${ui.message("emr.archivesRoom.cancel")}"></i></td>
                        </tr>
                        <tr>
                            <td colspan="6" data-bind="visible: locationLastSent" ><span data-bind="text: dossierNumber"></span> ${ ui.message("emr.archivesRoom.sentTo") } <span data-bind="text: locationLastSent"></span> ${ ui.message("emr.archivesRoom.at") } <span data-bind="text: dateLastSent"></span></td>
                        </tr>
                        </tbody>
                    </table>
                </div>
        </div>
        <div id="tab-merge">
            <div id="mergeRequests">
                <h2>${ ui.message("emr.archivesRoom.recordsToMerge.label") }</h2>
                <ul data-bind="foreach: requestsToMerge">
                    <li>
                        <h6><span data-bind="text: dateCreated"></span></h6>
                        <div class="patients-to-merge">
                            <div class="patient">
                                <div class="content"><i class="icon-folder-open medium"></i><span data-bind="text: notPreferredIdentifier"></span> <br/> <span data-bind="text: notPreferredName"></span> </div>
                            </div>
                            <div class="arrow">
                                <i class="icon-chevron-right medium"></i>
                            </div>
                            <div class="patient">
                                <div class="content"><i class="icon-folder-open medium"></i><span data-bind="text: preferredIdentifier"></span> <br/> <span data-bind="text: preferredName"></span> </div>
                            </div>
                            <div class="checkbox-done">
                                <input type="checkbox" name="mergeId" data-bind="value: mergeRequestId"/>
                                <label>${ ui.message("emr.archivesRoom.recordsToMerge.done.label") }</label>
                            </div>
                        </div>
                    </li>
                </ul>
            </div>
        </div>

        <div id="tab-return">

                <h2>${ ui.message("emr.archivesRoom.returningRecords.label") }</h2>

                <div id="scan-returned-records">
                    ${ ui.message("emr.archivesRoom.typeOrIdentifyBarCode.label") }
                    <br/>
                    <form class="mark-as-returned scan-input">
                        <input type="text" size="40" name="mark-as-returned-identifier" class="mark-as-returned-identifier" placeholder="${ ui.message("emr.archivesRoom.typeOrIdentifyBarCode.label") }"/>
                    </form>
                </div>

        </div>

</div>


<div id="cancel-paper-record-request-dialog" class="dialog" style="display: none">
    <div class="dialog-header">
        <h3>${ ui.message("emr.archivesRoom.cancelRequest.title") }</h3>
    </div>
    <div class="dialog-content">
        <p class="dialog-instructions">${ ui.message("emr.archivesRoom.pleaseConfirmCancel.message") }</p>

        <button class="confirm right">${ ui.message("emr.yes") }</button>
        <button class="cancel">${ ui.message("emr.no") }</button>
    </div>
</div>
