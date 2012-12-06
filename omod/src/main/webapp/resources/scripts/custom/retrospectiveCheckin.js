function OptionItem(name, value) {
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
        return _.map(list, function(item) { return OptionItem(item.label, item.value); });
    };
    var checkinDateForSubmission = function() {
        return api.checkinYear() + "-" + api.checkinMonth() + "-" + api.checkinDay() +
            " " + api.checkinHour() + ":" + api.checkinMinutes() + ":00";
    }

    api.locations = ko.observable(SelectableOptions('Location', 'location', convertSimpleObjectsToOptions(locations)));
    api.paymentReasons = ko.observable(SelectableOptions('Reason', 'paymentReason', convertSimpleObjectsToOptions(paymentReasons)));
    api.paymentAmounts = ko.observable(SelectableOptions('Amount', 'paymentAmount', convertSimpleObjectsToOptions(paymentAmounts)));

    api.patientIdentifier = ko.observable();
    api.checkinDay = ko.observable(); api.checkinMonth = ko.observable(); api.checkinYear = ko.observable();
    api.checkinHour = ko.observable(); api.checkinMinutes = ko.observable();
    api.receiptNumber = ko.observable();

    api.patientName = ko.observable();
    api.patientIdentifier.subscribe(function(newValue) {
        $.getJSON('/mirebalais/emr/findPatient/search.action?successUrl=/mirebalais/mirebalais/home.page?&term=' + newValue, function(data) {
            api.patient = undefined;
            api.patientName(undefined);
            if(data.length > 0) {
                var patient = data[0];
                if(patient && patient.preferredName) {
                    api.patient = patient;
                    api.patientName(patient.preferredName.fullName);
                }
            } else {
                $().toastmessage('showErrorToast', "The given patient identifier is invalid.");
            }
        });
    });

    api.checkinDate = ko.computed(function() {
       if( api.checkinDay() && api.checkinMonth() && api.checkinYear() && api.checkinHour() && api.checkinMinutes() ) {
           return api.checkinDay() + "/" + api.checkinMonth() + "/" + api.checkinYear() + " " +
               api.checkinHour() + ":" + api.checkinMinutes();
       }
       return undefined;
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
        return Boolean(api.locationName() && api.checkinDate() && api.patient);
    }
    api.paymentInfoIsValid = function () {
        return Boolean(api.paymentReason() && api.amountPaid() && api.receiptNumber());
    }
    api.registerCheckin = function() {
        if( !api.checkinInfoIsValid() || !api.paymentInfoIsValid() ) {
            $("#dialogMessage").html("Please fill all the fields in the form!");
            $("#dialogMessage").dialog({
                modal: true,
                buttons: {
                    Ok: function() {
                        $(this).dialog("close");
                        $(this).html("");
                    }
                }
            });
            return;
        };
        $.ajax({
            type: 'POST',
            url: window.location.pathname,
            data: {
                patientId:api.patient.patientId,
                locationId: api.locations().selectedOption().value,
                checkinDate: checkinDateForSubmission(),
                paymentReasonId: api.paymentReasons().selectedOption().value,
                paidAmount: api.paymentAmounts().selectedOption().value,
                paymentReceipt: api.receiptNumber()
            },
            success: function(data) {
                $("#dialogMessage").html("Retrospective Check in added successfully!");
                $("#dialogMessage").dialog({
                    modal: true,
                    buttons: {
                        Ok: function() {
                            $(this).dialog("close");
                            window.location.href = emr.pageLink("emr", "retrospectiveCheckin");
                        }
                    }
                });
            },
            error: function(data) {
                $("#dialogMessage").html("There was an error on processing this retrospective check in");
                $("#dialogMessage").dialog({
                    modal: true,
                    buttons: {
                        Cancel: function() {
                            $(this).dialog("close");
                        }
                    }
                });
            }
        });
    }
    return api;
}
