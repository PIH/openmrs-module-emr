function Option(name, value) {
    var model = {};
    model.name = name;
    model.value = value;
    model.selected = ko.observable(false);

    return model;
}
function SelectableOptions(label, widgetId, options) {
    var api = {};
    api.label = ko.observable(label);
    api.widgetId = ko.observable(widgetId);
    api.options = ko.observableArray(options);
    api.selectedOption = ko.computed(function() {
        return _.find(api.options(), function(o) {return o.selected()});
    });

    api.selectOption = function(option) {
        _.each(api.options(), function(o) {o.selected(false)});
        option.selected(true);
    };
    return api;
}

function RetrospectiveCheckinViewModel(locations, paymentReasons, paymentAmounts) {
    var api = {};
    var convertSimpleObjectsToOptions = function(list) {
        return _.map(list, function(item) { return Option(item.label, item.value); });
    };

    api.locations = ko.observable(SelectableOptions('Location', 'location', convertSimpleObjectsToOptions(locations)));
    api.paymentReasons = ko.observable(SelectableOptions('Reason', 'paymentReason', convertSimpleObjectsToOptions(paymentReasons)));
    api.paymentAmounts = ko.observable(SelectableOptions('Amount', 'paymentAmount', convertSimpleObjectsToOptions(paymentAmounts)));

    api.patientIdentifier = ko.observable();
    api.checkinDate = ko.observable();
    api.paymentReason = ko.observable();
    api.amountPaid = ko.observable();
    api.receiptNumber = ko.observable();

    api.patientName = ko.observable();
    api.patientIdentifier.subscribe(function(newValue) {
        $.getJSON('/mirebalais/emr/findPatient/search.action?successUrl=/mirebalais/mirebalais/home.page?&term=' + newValue, function(data) {
                var patient = data[0];
                if(patient && patient.preferredName) api.patientName(patient.preferredName.fullName);
        });
    });

    api.locationName = ko.computed(function() {
        var selectedOption = api.locations().selectedOption();
        return selectedOption ? selectedOption.name : '';
    });
    api.paymentReason = ko.computed(function() {
        var selectedOption = api.paymentReasons().selectedOption();
        return selectedOption ? selectedOption.name : '';
    });
    api.amountPaid = ko.computed(function() {
        var selectedOption = api.paymentAmounts().selectedOption();
        return selectedOption ? selectedOption.name : '';
    });

    api.checkinInfoIsValid = function() {
        return api.patientIdentifier() && api.locationName() && api.checkinDate();
    }
    api.paymentInfoIsValid = function () {
        return api.paymentReason() && api.amountPaid();
    }
    return api;
}
