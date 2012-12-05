var FormNavigator = function(submitElement, cancelElement) {
    var api = {};
    var submit=submitElement, cancel=cancelElement;
    var sections = $('section');
    var currentSection;
    var focusSectionsCounter = [];
    var currentField;
    var currentOptionFromList;


    var blurCurrentField = function() {
        $(currentField).blur();
        $(currentField).removeClass('focusedQuestion');
        if(currentOptionFromList) {
            $(currentOptionFromList).removeClass("focusedOption");
        }
    };
    var focusCurrentField = function() {
        $(currentField).addClass('focusedQuestion');
        $(currentField).focus();
        /* $('body').scrollTop($(currentField).offset().top - sections.offset().top); */
        if($(currentField).children('.option').length > 0) {
            currentOptionFromList = $(currentField).children('.option')[0];
            $(currentOptionFromList).addClass("focusedOption");
        }
    };
    var gotoSubmit = function() {
        submit.focus();
    };
    var moveToOption = function(option) {
        if( $(currentField).hasClass('optionsList') ) {
            if(option.length > 0) {
                $(currentOptionFromList).removeClass('focusedOption')
                console.log(option[0]);
                currentOptionFromList = option[0];
                $(currentOptionFromList).addClass('focusedOption');
            }
        }
    }


    api.gotoSection = function(section) {
        if(!currentSection || $(section).attr('id') != $(currentSection).attr('id')) {
            blurCurrentField();
            currentField = undefined;
            if(currentSection) {
                $(currentSection).removeClass("focused");
            }
            currentSection = section;
            $(currentSection).addClass("focused");
            $('body').scrollTop($(currentSection).offset().top - sections.offset().top);
        }
    };
    api.jumpToNextField = function() {
        if(currentOptionFromList) {
            koContext = ko.contextFor(currentOptionFromList);
            if(koContext) {
                koContext.$parent.selectOption(koContext.$data);
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
            blurCurrentField();
            currentField = fields[fieldIndex + 1];
            if( $(currentField).is(':visible')) {
                focusCurrentField();
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
            blurCurrentField();
            currentField = fields[fieldIndex - 1];
            if( $(currentField).is(':visible')) {
                focusCurrentField();
            }
            else api.jumpToPreviousField();
        }
    };
    api.moveDownIfOnOptionsList = function() {
        moveToOption($(currentOptionFromList).next());
    };
    api.moveUpIfOnOptionsList = function() {
        moveToOption($(currentOptionFromList).prev());
    };

    sections.click( function() {
        api.gotoSection($(this));
    });
    var keyFunctions = {
        /*TAB*/   9  : api.jumpToNextField,
        /*ENTER*/ 13 : api.jumpToNextField,
        /*ESC*/   27 : api.jumpToPreviousField,
        /*UP*/    40 : api.moveDownIfOnOptionsList,
        /*DOWN*/  38 : api.moveUpIfOnOptionsList
    };
    $('body').keydown(function(key) {
        if(keyFunctions[key.which]) {
            key.preventDefault();
            keyFunctions[key.which]();
        }
    });
    $('body').append('<div id="invisibleBotton" style="height: 1000px"></div>');
    return api;
}