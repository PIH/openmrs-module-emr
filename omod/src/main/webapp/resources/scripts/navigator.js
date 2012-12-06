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
    var focusSelectedOption = function() {
        var optionItems = $(currentField).children('.option');
        if(optionItems.length > 0) {
            koContext = ko.contextFor(currentField);
            if(koContext && koContext.$data.selectedOption()) {
                currentOptionFromList = _.find(optionItems, function(op) {
                    return $(op).text() == koContext.$data.selectedOption().name;
                });
            } else {
                currentOptionFromList = optionItems[0];
            }
            $(currentOptionFromList).addClass("focusedOption");
        }
    };
    var focusCurrentField = function() {
        $(currentField).addClass('focusedQuestion');
        $(currentField).focus();
        focusSelectedOption();
    };
    var gotoSubmit = function() {
        submit.focus();
    };
    var moveToOption = function(option) {
        if( $(currentField).hasClass('optionsList') ) {
            if(option.length > 0) {
                $(currentOptionFromList).removeClass('focusedOption')
                currentOptionFromList = option[0];
                $(currentOptionFromList).addClass('focusedOption');
            }
        }
    }
    var selectCurrentOption = function() {
        koContext = ko.contextFor(currentOptionFromList);
        if(koContext) {
            koContext.$parent.selectOption(koContext.$data);
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
        if(currentOptionFromList) selectCurrentOption();

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
        api.gotoSection($(this)[0]);
    });

    var keyFunctions = {
        /*TAB*/   9  : api.jumpToNextField,
        /*ENTER*/ 13 : api.jumpToNextField,
        /*ESC*/   27 : api.jumpToPreviousField,
        /*UP*/    40 : api.moveDownIfOnOptionsList,
        /*DOWN*/  38 : api.moveUpIfOnOptionsList
    };
    $('input, .optionsList').click(function() {
        blurCurrentField();
        currentField = $(this)[0];
        focusCurrentField();
    });
    $('body').keydown(function(key) {
        if(keyFunctions[key.which]) {
            key.preventDefault();
            keyFunctions[key.which]();
        }
    });
    $('body').append('<div id="invisibleBotton" style="height: 1000px"></div>');

    return api;
}