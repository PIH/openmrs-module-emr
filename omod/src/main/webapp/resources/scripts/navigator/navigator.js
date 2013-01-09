function initFormModels() {
    var formElement = $('div#content > form').first();
    formElement.prepend('<div id="spacer"></div>');
    formElement.prepend('<div class="formBreadcrumb"></div>');
    var spacer = formElement.find('div#spacer').first();
    var breadcrumb = formElement.find('div.formBreadcrumb').first();

    var sections = _.map(formElement.find('section'), function(s) {
        var section = SectionModel(s);
        section.moveTitleTo(breadcrumb);
        return section;
    });

    sections[0].toggleSelection();

    var confirmationSection = ConfirmationSectionModel($('#confirmation'), _.clone(sections));
    confirmationSection.moveTitleTo(breadcrumb);
    sections.push(confirmationSection);

    var questions = _.flatten( _.map(sections, function(s) { return s.questions; }), true);
    var fields = _.flatten(_.map(questions, function(q) { return q.fields; }), true);
    return [sections, questions, fields, spacer];
}

function initKeyboardHandlersChain(sections, questions, fields) {
    var sectionsHandler = SectionsKeyboardHandler();
    _.each(sections, function(s) { sectionsHandler.addSection(s); });

    var questionsHandler = QuestionsKeyboardHandler(sectionsHandler);
    _.each(questions, function(q) { questionsHandler.addQuestion(q); });

    var fieldsHandler = FieldsKeyboardHandler(questionsHandler);
    _.each(fields, function(f) { fieldsHandler.addField(f); });

    return fieldsHandler;
}

function initMouseHandlers(sections, questions, fields) {
    SectionMouseHandler(sections);
    QuestionsMouseHandler(questions);
    FieldsMouseHandler(fields);
}

function KeyboardController() {
    var modelsList = initFormModels();
    var sections=modelsList[0], questions=modelsList[1], fields=modelsList[2], spacer=modelsList[3];
    initMouseHandlers(sections, questions, fields);
    var handlerChainRoot = initKeyboardHandlersChain(sections, questions, fields);
    var spacerUpdater = SpacerUpdater(spacer, sections);

    handlerChainRoot.handleTabKey();
    spacerUpdater.update();

    $('body').keydown(function(key) {
        switch(key.which) {
            case 39:
                handlerChainRoot.handleRightKey() && key.preventDefault();
                break;
            case 37:
                handlerChainRoot.handleLeftKey() && key.preventDefault();
                break;
            case 38:
                handlerChainRoot.handleUpKey() && key.preventDefault();
                break;
            case 40:
                handlerChainRoot.handleDownKey() && key.preventDefault();
                break;
            case 27:
                handlerChainRoot.handleEscKey() && key.preventDefault();
                break;
            case 9:
                if(event.shiftKey) {
                    handlerChainRoot.handleShiftTabKey();
                } else {
                    handlerChainRoot.handleTabKey();
                }
                spacerUpdater.update();
                key.preventDefault()
                break;
            case 13:
                handlerChainRoot.handleTabKey();
            default:
                break;
        }
    });
}

function SpacerUpdater(spacer, sectionModels) {
    var spacerEl = spacer;
    var sections = sectionModels;
    var questionsBefore = 2;
    var questionHeight = 40;

    var api = {};
    api.update = function() {
        var currentSection = _.find(sections, function(s) { return s.isSelected; });
        var currentFieldIndex = 0;
        var currentField = _.find(currentSection.questions, function(q, i) { currentFieldIndex = i; return q.isSelected; });


        _.each(currentSection.questions, function(q,i) {
            if(i < (currentFieldIndex-questionsBefore) ) {
                q.hide();
            } else {
                q.show();
            }
        });

        spacerEl.css('height', questionHeight*(questionsBefore-currentFieldIndex));
        console.log(currentField.questionLegend().css('height'));
    }

    return api;
}