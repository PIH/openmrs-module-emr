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
        var selected = [];
        ko.utils.arrayForEach(api.requests(), function(item) {
           if (item.selected()) {
               selected.push(item);
           }
        });
        return selected;
    });

    return api;
}
