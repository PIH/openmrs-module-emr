function RecordRequestModel(requestId, patientName, patientId, dossierNumber, sendToLocation, timeRequested, timeRequestedSortable) {
    var model = {};
    model.requestId = requestId;
    model.patientName = patientName;
    model.patientId = patientId;
    model.dossierNumber = dossierNumber;
    model.sendToLocation = sendToLocation;
    model.timeRequested = timeRequested;
    model.timeRequestedSortable = timeRequestedSortable;
    model.selected = ko.observable(false);
    model.hovered = ko.observable(false);

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

    return api;
}

function AssignedPullRequestsViewModel(assignedRecordsToPull) {
    var api = {};
    api.assignedRecordsToPull = ko.observableArray(assignedRecordsToPull);

    api.load = function (url) {

        // reload via ajax
        jQuery.getJSON(emr.fragmentActionLink("emr", "paperrecord/archivesRoom", "getAssignedRecordsToPull"))
            .success(function(data) {

                // remove any existing entries
                api.assignedRecordsToPull.removeAll();

                // create the new list
                jQuery.each(data, function(index, request) {
                    api.assignedRecordsToPull.push(RecordRequestModel(request.requestId, request.patient,
                        request.patientIdentifier, request.identifier, request.requestLocation, request.dateCreated,
                        request.dateCreatedSortable));
                });

            })
            .error(emr.handleError(xhr));

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
            .error(function(xhr, status, err) {
                emr.errorAlert(jq.parseJSON(xhr.responseText).globalErrors[0]);
            })
    }

    return api;
}

function AssignedCreateRequestsViewModel(assignedRecordsToCreate) {
    var api = {};
    api.assignedRecordsToCreate = ko.observableArray(assignedRecordsToCreate);

    return api;
}