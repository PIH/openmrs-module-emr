
var pullRequestsViewModel;
var createRequestsViewModel;
var assignedPullRequestsViewModel;
var assignedCreateRequestsViewModel;
var mergeRequestsViewModel;

var cancelPaperRecordRequestDialog = null;

function RecordRequestModel(requestId, patientName, patientId, dossierNumber, sendToLocation, timeRequested, timeRequestedSortable, dateLastSent, locationLastSent) {
    var model = {};
    model.requestId = requestId;
    model.patientName = patientName;
    model.patientId = patientId;
    model.dossierNumber = dossierNumber;
    model.sendToLocation = sendToLocation;
    model.timeRequested = timeRequested;
    model.timeRequestedSortable = timeRequestedSortable;
    model.locationLastSent = locationLastSent;
    model.dateLastSent = dateLastSent;
    model.selected = ko.observable(false);
    model.hovered = ko.observable(false);

    return model;
}

function MergeRequestsModel(mergeRequestId, preferredName, preferredIdentifier, notPreferredIdentifier, notPreferredName, dateCreated, dateCreatedSortable){
    var model = {};
    model.mergeRequestId = mergeRequestId;
    model.preferredName = preferredName;
    model.preferredIdentifier = preferredIdentifier;
    model.notPreferredIdentifier = notPreferredIdentifier;
    model.notPreferredName = notPreferredName;
    model.dateCreated = dateCreated;
    model.dateCreatedSortable = dateCreatedSortable;

    return model;
}


function PullRequestsViewModel(recordsToPull) {
    var api = {};
    api.recordsToPull = ko.observableArray(recordsToPull);

    api.selectNumber = function(number) {
        for (var i = 0; i < recordsToPull.length; ++i) {
            recordsToPull[i].selected(i < number);
        }
    };

    api.hoverNumber = function(number){
        for (var i = 0; i < recordsToPull.length; ++i) {
            recordsToPull[i].hovered(i < number);
        }
    }

    api.isValid = function(){
        return api.selectedRequests().length > 0;
    }

    api.selectRequestToBePulled = function(request) {
        var indexOf = recordsToPull.indexOf(request);
        api.selectNumber(indexOf + 1);
    };

    api.hoverRecords = function(request) {
        var indexOf = recordsToPull.indexOf(request);
        api.hoverNumber(indexOf + 1);
    };

    api.unHoverRecords = function(){
        var indexOf = api.recordsToPull();
        for( var i=0; i < api.recordsToPull().length; i++) {
            api.recordsToPull()[i].hovered(false);
        }
    }

    api.selectedRequests = ko.computed(function() {
        return jQuery.grep(api.recordsToPull(), function(item) {
           return item.selected();
        });
    });

    api.hoveredRequests = ko.computed(function() {
        return jQuery.grep(api.recordsToPull(), function(item) {
            return item.hovered();
        });
    });

    api.cancelRequest = function (request) {
        openCancelPaperRecordRequestDialog(request.requestId);
    }

    api.load = function() {

        // reload via ajax
        jQuery.getJSON(emr.fragmentActionLink("emr", "paperrecord/archivesRoom", "getOpenRecordsToPull"))
            .success(function(data) {

                // remove any existing entries
                api.recordsToPull.removeAll();

                // create the new list
                jQuery.each(data, function(index, request) {
                    api.recordsToPull.push(RecordRequestModel(request.requestId, request.patient,
                        request.patientIdentifier, request.identifier, request.requestLocation, request.dateCreated,
                        request.dateCreatedSortable, request.dateLastSent, request.locationLastSent));
                });

            })
            .error(function(xhr) {
                emr.handleError(xhr);
            });

    }

    return api;
}


function CreateRequestsViewModel(recordsToCreate) {
    var api = {};
    api.recordsToCreate = ko.observableArray(recordsToCreate);

    api.selectRecordsToBeCreated = function(record) {
        var indexOf = api.recordsToCreate().indexOf(record);
        for( var i=0; i<api.recordsToCreate().length; i++) {
            api.recordsToCreate()[i].selected(i <= indexOf);
        }
    };

    api.isValid = function(){
        return api.selectedRequests().length > 0;
    }

    api.hoverRecords = function(record){
        var indexOf = api.recordsToCreate().indexOf(record);
        for( var i=0; i < api.recordsToCreate().length; i++) {
            api.recordsToCreate()[i].hovered(i <= indexOf);
        }
    }

    api.unHoverRecords = function(){
        var indexOf = api.recordsToCreate();
        for( var i=0; i < api.recordsToCreate().length; i++) {
            api.recordsToCreate()[i].hovered(false);
        }
    }

    api.selectedRequests = ko.computed(function() {
        return jQuery.grep(api.recordsToCreate(), function(item) {
            return item.selected();
        });
    });

    api.cancelRequest = function (request) {
        openCancelPaperRecordRequestDialog(request.requestId);
    }

    api.load = function() {

        // reload via ajax
        jQuery.getJSON(emr.fragmentActionLink("emr", "paperrecord/archivesRoom", "getOpenRecordsToCreate"))
            .success(function(data) {

                // remove any existing entries
                api.recordsToCreate.removeAll();

                // create the new list
                jQuery.each(data, function(index, request) {
                    api.recordsToCreate.push(RecordRequestModel(request.requestId, request.patient,
                        request.patientIdentifier, request.identifier, request.requestLocation, request.dateCreated,
                        request.dateCreatedSortable));
                });

            })
            .error(function(xhr) {
                emr.handleError(xhr);
            });

    }

    return api;
}

function AssignedPullRequestsViewModel(assignedRecordsToPull) {
    var api = {};
    api.assignedRecordsToPull = ko.observableArray(assignedRecordsToPull);

    api.cancelRequest = function (request) {
        openCancelPaperRecordRequestDialog(request.requestId);
    }

    api.load = function() {

        // reload via ajax
        jQuery.getJSON(emr.fragmentActionLink("emr", "paperrecord/archivesRoom", "getAssignedRecordsToPull"))
            .success(function(data) {

                // remove any existing entries
                api.assignedRecordsToPull.removeAll();

                // create the new list
                jQuery.each(data, function(index, request) {
                    api.assignedRecordsToPull.push(RecordRequestModel(request.requestId, request.patient,
                        request.patientIdentifier, request.identifier, request.requestLocation, request.dateCreated,
                        request.dateCreatedSortable, request.dateLastSent, request.locationLastSent));
                });

            })
            .error(function(xhr) {
                emr.handleError(xhr);
            });

    }

    api.printLabel = function (request) {

        jQuery.ajax({
            url: emr.fragmentActionLink("emr", "paperrecord/archivesRoom", "printLabel", { requestId: request.requestId }),
            dataType: 'json',
            type: 'POST'
        })
            .success(function(data) {
                emr.successMessage(data.message);
            })
            .error(function(xhr) {
                emr.handleError(xhr);
            });
    }

    return api;
}

function AssignedCreateRequestsViewModel(assignedRecordsToCreate) {
    var api = {};
    api.assignedRecordsToCreate = ko.observableArray(assignedRecordsToCreate);

    api.cancelRequest = function (request) {
        openCancelPaperRecordRequestDialog(request.requestId);
    }

    api.load = function() {

        // reload via ajax
        jQuery.getJSON(emr.fragmentActionLink("emr", "paperrecord/archivesRoom", "getAssignedRecordsToCreate"))
            .success(function(data) {

                // remove any existing entries
                api.assignedRecordsToCreate.removeAll();

                // create the new list
                jQuery.each(data, function(index, request) {
                    api.assignedRecordsToCreate.push(RecordRequestModel(request.requestId, request.patient,
                        request.patientIdentifier, request.identifier, request.requestLocation, request.dateCreated,
                        request.dateCreatedSortable));
                });

            })
            .error(function(xhr) {
                emr.handleError(xhr);
            });

    }

    api.printLabel = function (request) {

        jQuery.ajax({
            url: emr.fragmentActionLink("emr", "paperrecord/archivesRoom", "printLabel", { requestId: request.requestId }),
            dataType: 'json',
            type: 'POST'
        })
            .success(function(data) {
                emr.successMessage(data.message);
            })
            .error(function(xhr) {
                emr.handleError(xhr);
            });
    }

    return api;
}

function MergeRequestsViewModel(requestsToMerge){
    var api = {};
    api.requestsToMerge = ko.observableArray(requestsToMerge);

    api.load = function() {

        jQuery.getJSON(emr.fragmentActionLink("emr", "paperrecord/archivesRoom", "getOpenRecordsToMerge"))
            .success(function(data) {

                api.requestsToMerge.removeAll();

                // create the new list
                jQuery.each(data, function(index, request) {
                    api.requestsToMerge.push(MergeRequestsModel(request.mergeRequestId, request.preferredName,
                        request.preferredIdentifier, request.notPreferredIdentifier, request.notPreferredName,
                        request.dateCreated, request.dateCreatedSortable));

                });

            })
            .error(function(xhr) {
                emr.handleError(xhr);
            });

    };

    api.remove = function(id){
        for( var i=0; i < api.requestsToMerge().length; i++) {
            if(api.requestsToMerge()[i].mergeRequestId==id){
                api.requestsToMerge().splice(i,1);
            }
        }
    }

    return api;

}

function refreshAllQueues() {
    pullRequestsViewModel.load();
    createRequestsViewModel.load()
    assignedCreateRequestsViewModel.load();
    assignedPullRequestsViewModel.load();
    mergeRequestsViewModel.load();
}

function openCancelPaperRecordRequestDialog(requestId) {

    cancelPaperRecordRequestDialog = emr.setupConfirmationDialog({
        selector: '#cancel-paper-record-request-dialog',
        actions: {
            confirm: function() {
                emr.getFragmentActionWithCallback('emr', 'paperrecord/archivesRoom', 'markPaperRecordRequestAsCancelled', { requestId: requestId }, function(data) {
                    cancelPaperRecordRequestDialog .close();
                    refreshAllQueues();
                });
            },
            cancel: function() {
                cancelPaperRecordRequestDialog.close();
            }
        }
    });

    cancelPaperRecordRequestDialog.show();
    return false;
}

