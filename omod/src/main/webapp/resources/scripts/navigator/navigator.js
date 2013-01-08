function initFormModels() {
    var formElement = $('div#content > form').first();
    formElement.prepend('<div class="formBreadcrumb"></div>');
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
    return [sections, questions, fields];
}

function initKeyboardHandlersChain(sections, questions, fields) {
    var sectionsHandler = SectionsHandler();
    _.each(sections, function(s) { sectionsHandler.addSection(s); });

    var questionsHandler = QuestionsHandler(sectionsHandler);
    _.each(questions, function(q) { questionsHandler.addQuestion(q); });

    var fieldsHandler = FieldsHandler(questionsHandler);
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
    var sections=modelsList[0], questions=modelsList[1], fields=modelsList[2];
    initMouseHandlers(sections, questions, fields);
    var handlerChainRoot = initKeyboardHandlersChain(sections, questions, fields);
    handlerChainRoot.handleTabKey();

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
                key.preventDefault()
                break;
            case 13:
                handlerChainRoot.handleTabKey();
            default:
                break;
        }
    });
}

var SectionMouseHandler = function(sectionModels) {
    var sections = sectionModels;
    _.each(sections, function(s) {
        s.title().click( function() {
            clickedSection(s);
        });
    });

    var clickedSection = function(section) {
        _.each(sections, function(s) {
           if(section == s) {
               s.select();
           } else {
               s.unselect();
           }
        });
    }
};

var QuestionsMouseHandler = function(questionModels) {
    var questions = questionModels;
    _.each(questions, function(q) {
        q.questionLegend().click(function() {
            clickedQuestion(q);
        });
    });

    var clickedQuestion = function(question) {
        _.each(questions, function(q) {
           if(question == q) {
               q.select();
           } else {
               q.unselect();
           }
        });
    };
};

var FieldsMouseHandler = function(fieldsModels) {
    var fields = fieldsModels;
    _.each(fields, function(f) {
        f.element().click(function() {
            clickedField(f);
        });
    });

    var clickedField = function(field) {
        _.each(fields, function(f) {
            if( field == f ) {
                f.select();
            } else {
                f.unselect();
            }
        })
    };
};