var FormNavigator = function(submitElement, cancelElement) {
    var api = {};
    var submit=submitElement, cancel=cancelElement;
    var sections = $('section');
    var currentSection;
    var focusSectionsCounter = [];
    var currentField;
    var currentOptionFromList;

    sections.click( function() {
        api.gotoSection($(this));
    });

    var gotoSubmit = function() { submit.focus(); };

    api.gotoSection = function(section) {
        if(!currentSection || $(section).attr('id') != $(currentSection).attr('id')) {
            if(currentSection) $(currentSection).removeClass("focused");
            currentSection = section;
            $(currentSection).addClass("focused");
            currentField = undefined;
        }
    };

    api.jumpToNextField = function() {
        if(currentOptionFromList) {
            koContext = ko.contextFor(currentOptionFromList);
            if(koContext) {
                koContext.$parent.selectOption(koContext.$data);
                console.log(koContext.$parent);
            }
        }

        if(!currentSection) {
            api.gotoSection(sections[0]);
            return;
        }

        var fields = $(currentSection).find('input,select,.optionsList');
        var fieldIndex = $.inArray(currentField, fields);

        if(fieldIndex == fields.length - 1) {
            var sectionIndex = $.inArray(currentSection, sections);
            if(sectionIndex == sections.length - 1) gotoSubmit();
            else api.gotoSection(sections[sectionIndex + 1]);
        } else {
            $(currentField).blur();
            $(currentField).removeClass('focusedQuestion');
            if(currentOptionFromList) {
                $(currentOptionFromList).removeClass("focusedOption");
            }
            currentField = fields[fieldIndex + 1];
            if( $(currentField).is(':visible')) {
                $(currentField).addClass('focusedQuestion');
                $(currentField).focus();
                if($(currentField).children('.option').length > 0) {
                    currentOptionFromList = $(currentField).children('.option')[0];
                    $(currentOptionFromList).addClass("focusedOption");
                }
            }
            else api.jumpToNextField();
        }
    };
    api.jumpToPreviousField = function() {
        var fields = $(currentSection).find('input,select,.optionsList');
        var fieldIndex = $.inArray(currentField, fields);

        if(!currentField || fieldIndex == 0) {
            var sectionIndex = $.inArray(currentSection, sections);
            if(sectionIndex != 0) api.gotoSection(sections[sectionIndex - 1]);
        } else {
            $(currentField).blur();
            $(currentField).removeClass('focusedQuestion');
            if(currentOptionFromList) {
                $(currentOptionFromList).removeClass("focusedOption");
            }
            currentField = fields[fieldIndex - 1];
            if( $(currentField).is(':visible')) {
                $(currentField).addClass('focusedQuestion');
                $(currentField).focus();
                if($(currentField).children('.option').length > 0) {
                    currentOptionFromList = $(currentField).children('.option')[0];
                    $(currentOptionFromList).addClass("focusedOption");
                }
            }
            else api.jumpToPreviousField();
        }
    };
    api.moveDownIfOnOptionsList = function() {
        if( $(currentField).hasClass('optionsList') ) {
            if($(currentOptionFromList).next()) {
                $(currentOptionFromList).removeClass('focusedOption')
                currentOptionFromList = $(currentOptionFromList).next()[0];
                $(currentOptionFromList).addClass('focusedOption');
            }
        }
    };
    api.moveUpIfOnOptionsList = function() {
        if( $(currentField).hasClass('optionsList') ) {
            if($(currentOptionFromList).prev()) {
                $(currentOptionFromList).removeClass('focusedOption')
                currentOptionFromList = $(currentOptionFromList).prev()[0];
                $(currentOptionFromList).addClass('focusedOption');
            }
        }
    };

    var keyFunctions = {
        /*TAB*/   9  : api.jumpToNextField,
        /*ENTER*/ 13 : api.jumpToNextField,
        /*ESC*/   27 : api.jumpToPreviousField,
        /*UP*/    40 : api.moveDownIfOnOptionsList,
        /*DOWN*/  38 : api.moveUpIfOnOptionsList
    };

    $('body').keydown(function(key) {
        console.log(key.which);
        if(keyFunctions[key.which]) {
            key.preventDefault();
            keyFunctions[key.which]();
        }
    });

    return api;
}

/*
 api.questions = [
    { label: "Payment Reason", type: "optionsList", options: [ ... ], value: ko.observable() },
    OptionListQuestion("Payment Reason", [ ... ])
 ];
 */



function Option(name, value) {
    var model = {};
    model.name = name;
    model.value = value;
    model.selected = ko.observable(false);

    return model;
}
function SelectableOptions(options) {
    var api = {};
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
    api.locations = ko.observable(SelectableOptions([
        Option("Emergency", 1),
        Option("Outpatient", 2),
        Option("Inpatient", 3)]));
    api.paymentReasons = ko.observable(SelectableOptions([
        Option("Medical certificate without diagnosis", 1),
        Option("Standard dental visit", 2),
        Option("Marriage certificate without diagnosis", 3),
        Option("Standard outpatient visit", 4)]));
    api.paymentAmounts = ko.observable(SelectableOptions([
        Option("50 Gourdes", 50),
        Option("100 Gourdes", 100),
        Option("Exempt", 0)]));

    api.patientIdentifier = ko.observable();
    api.checkinDate = ko.observable();
    api.receiptNumber = ko.observable();

    api.radiologyEncounter = ko.observable(false);

    api.diagnosticsEncounter = ko.observable(false);
    api.diagnostic = ko.observable();
    api.diagnosticPhisician = ko.observable();

    api.medicationEncounter = ko.observable(false);

    api.admissionEncounter = ko.observable(false);
    api.admissionDate = ko.observable();

    api.dischargeEncounter = ko.observable(false);
    api.dischargeDate = ko.observable();


    api.checkinInfoIsValid = function() {
        return api.patientIdentifier() && api.locations().selectedOption() && api.checkinDate();
    }
    api.paymentInfoIsValid = function () {
        return api.paymentReasons().selectedOption() && api.paymentAmounts().selectedOption();
    }
    return api;
};