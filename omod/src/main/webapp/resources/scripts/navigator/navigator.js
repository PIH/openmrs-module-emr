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
    return sections;
}

function initKeyboardHandlersChain() {
    var sections = initFormModels();
    var sectionsHandler = SectionsHandler();
    _.each(sections, function(s) { sectionsHandler.addSection(s); });

    var questions = _.flatten( _.map(sections, function(s) { return s.questions; }), true);
    var questionsHandler = QuestionsHandler(sectionsHandler);
    _.each(questions, function(q) { questionsHandler.addQuestion(q); });

    var fields = _.flatten(_.map(questions, function(q) { return q.fields; }), true);
    var fieldsHandler = FieldsHandler(questionsHandler);
    _.each(fields, function(f) { fieldsHandler.addField(f); });

    return fieldsHandler;
}

function KeyboardController() {
    var handlerChainRoot = initKeyboardHandlersChain();
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
            default:
                break;
        }
    });
}
