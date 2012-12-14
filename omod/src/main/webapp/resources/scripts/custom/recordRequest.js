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

    api.isValid = function(){
        return api.selectedRequests().length > 0;
    }

    api.selectRequestToBePulled = function(request) {
        var indexOf = recordsToPull.indexOf(request);
        api.selectNumber(indexOf + 1);
    };

    api.selectedRequests = ko.computed(function() {
        return jQuery.grep(api.recordsToPull(), function(item) {
           return item.selected();
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

    return api;
}

function AssignedCreateRequestsViewModel(assignedRecordsToCreate) {
    var api = {};
    api.assignedRecordsToCreate = ko.observableArray(assignedRecordsToCreate);

    return api;
}