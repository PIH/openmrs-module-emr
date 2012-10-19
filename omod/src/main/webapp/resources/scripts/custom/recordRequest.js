function RecordRequestModel(requestId, patientName, dossierNumber, sendToLocation, timeRequested, timeRequestedSortable) {
    var model = {};
    model.requestId = requestId;
    model.patientName = patientName;
    model.dossierNumber = dossierNumber;
    model.sendToLocation = sendToLocation;
    model.timeRequested = timeRequested;
    model.timeRequestedSortable = timeRequestedSortable;
    model.selected = ko.observable(false);

    return model;
}


function RecordRequestsViewModel(requests) {
    var api = {};
    api.requests = ko.observableArray(requests);

    api.selectNumber = function(number) {
        for (var i = 0; i < requests.length; ++i) {
            requests[i].selected(i < number);
        }
    };

    api.selectRequestToBePulled = function(request) {
        var indexOf = requests.indexOf(request);
        api.selectNumber(indexOf + 1);
    };

    api.selectedRequests = ko.computed(function() {
        return jQuery.grep(api.requests(), function(item) {
           return item.selected();
        });
    });

    return api;
}
