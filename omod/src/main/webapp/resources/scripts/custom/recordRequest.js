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

    api.selectRequestToBePulled = function(request) {
        var indexOf = requests.indexOf(request);
        for(var i=0; i <= indexOf; i++) {
            requests[i].selected(true);
        }
        for(var i=indexOf+1; i<requests.length; i++) {
            requests[i].selected(false);
        }
    };

    api.selectedRequests = ko.computed(function() {
        var selected = [];
        for (var i = 0; i < api.requests.length; ++i) {
            if (this.requests[i].selected()) {
                selected.push(this.requests[i].requestId);
            }
        }
        return selected.join(",");
    });

    return api;
}
