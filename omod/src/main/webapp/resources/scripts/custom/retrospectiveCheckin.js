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

function RetrospectiveCheckinViewModel() {
    var api = {};
    api.locations = ko.observable(SelectableOptions('Location', 'location', [
        Option("Emergency", 1),
        Option("Outpatient", 2),
        Option("Inpatient", 3)]));
    api.paymentReasons = ko.observable(SelectableOptions('Reason', 'paymentReason', [
        Option("Medical certificate without diagnosis", 1),
        Option("Standard dental visit", 2),
        Option("Marriage certificate without diagnosis", 3),
        Option("Standard outpatient visit", 4)]));
    api.paymentAmounts = ko.observable(SelectableOptions('Amount', 'paymentAmount', [
        Option("50 Gourdes", 50),
        Option("100 Gourdes", 100),
        Option("Exempt", 0)]));

    api.patientIdentifier = ko.observable();
    api.checkinDate = ko.observable();
    api.paymentReason = ko.observable();
    api.amountPaid = ko.observable();
    api.receiptNumber = ko.observable();

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
