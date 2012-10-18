function RecordRequestModel(requestId, patientName, dossierNumber, sendToLocation, timeRequested) {
    var model = {};
    model.requestId = requestId;
    model.patientName = patientName;
    model.dossierNumber = dossierNumber;
    model.sendToLocation = sendToLocation;
    model.timeRequested = timeRequested;
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

    return api;
}
