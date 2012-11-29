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
    api.locations = [{name:"Emergency", value:1}, {name:"Outpatient", value:2}];
    api.paymentReasons = [
        {name:"Medical certificate without diagnosis", value:1},
        {name:"Standard dental visit", value:2},
        {name:"Marriage certificate without diagnosis", value:3},
        {name:"Standard outpatient visit", value:4}];
    api.paymentAmounts = [
        {name:"50 Gourdes", value:50},
        {name:"100 Gourdes", values:100},
        {name:"Exempt", values:0}];

    api.location = ko.observable();
    api.patientIdentifier = ko.observable();
    api.checkinDate = ko.observable();
    api.paymentReason = ko.observable();
    api.amountPaid = ko.observable();
    api.receiptNumber = ko.observable();

    api.checkinInfoIsValid = function() {
        return api.patientIdentifier() && api.location() && api.checkinDate();
    }
    api.paymentInfoIsValid = function () {
        return api.paymentReason() && api.amountPaid();
    }
    return api;
}
