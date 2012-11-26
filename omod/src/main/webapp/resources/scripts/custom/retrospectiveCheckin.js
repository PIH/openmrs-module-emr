var FormNavigator = function(submitElement, cancelElement) {
    var api = {};
    var submit=submitElement, cancel=cancelElement;
    var sections = $('section');
    var currentSection;
    var focusSectionsCounter = [];
    var currentField;

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
        if(!currentSection) {
            api.gotoSection(sections[0]);
            return;
        }

        var fields = $(currentSection).find('input,select');
        var fieldIndex = $.inArray(currentField, fields);

        if(fieldIndex == fields.length - 1) {
            var sectionIndex = $.inArray(currentSection, sections);
            if(sectionIndex == sections.length - 1) gotoSubmit();
            else api.gotoSection(sections[sectionIndex + 1]);
        } else {
            currentField = fields[fieldIndex + 1];
            if( $(currentField).is(':visible')) $(currentField).focus();
            else api.jumpToNextField();
        }
    };
    api.jumpToPreviousField = function() {
        var fields = $(currentSection).find('input,select');
        var fieldIndex = $.inArray(currentField, fields);

        if(!currentField || fieldIndex == 0) {
            var sectionIndex = $.inArray(currentSection, sections);
            if(sectionIndex != 0) api.gotoSection(sections[sectionIndex - 1]);
        } else {
            currentField = fields[fieldIndex - 1];
            if( $(currentField).is(':visible')) $(currentField).focus();
            else api.jumpToPreviousField();
        }
    };

    var keyFunctions = {
        /*TAB*/   9  : api.jumpToNextField,
        /*ENTER*/ 13 : api.jumpToNextField,
        /*ESC*/   27 : api.jumpToPreviousField
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
        {name:"Exemtpt", values:0}];

    api.location = ko.observable();
    api.patientIdentifier = ko.observable();
    api.checkinDate = ko.observable();
    api.paymentReason = ko.observable();
    api.amountPaid = ko.observable();
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
        return api.patientIdentifier() && api.location() && api.checkinDate();
    }
    api.paymentInfoIsValid = function () {
        return api.paymentReason() && api.amountPaid();
    }
    return api;
}
