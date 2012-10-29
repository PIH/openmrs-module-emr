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

    return model;
}


function RecordRequestsViewModel(recordsToPull) {
    var api = {};
    api.recordsToPull = ko.observableArray(recordsToPull);

    api.selectNumber = function(number) {
        for (var i = 0; i < recordsToPull.length; ++i) {
            recordsToPull[i].selected(i < number);
        }
    };

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


function RecordsCreationViewModel(recordsToCreate) {
    var api = {};
    api.recordsToCreate = ko.observableArray(recordsToCreate);

    api.selectRecordsToBeCreated = function(record) {
        var indexOf = api.recordsToCreate().indexOf(record);
        for( var i=0; i<api.recordsToCreate().length; i++) {
            api.recordsToCreate()[i].selected(i <= indexOf);
        }
    };

    api.selectedRequests = ko.computed(function() {
        return jQuery.grep(api.recordsToCreate(), function(item) {
            return item.selected();
        });
    });

    return api;
}