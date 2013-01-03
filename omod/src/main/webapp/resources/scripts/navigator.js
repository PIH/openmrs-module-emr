/* ================================================== */
/* ================= EVENT HANDLERS ================= */
/* ================================================== */
function FieldsHandler(questionsHandler) {
    var fields = [];
    var questionsHandler = questionsHandler;
    var selectedField = function() {
        return _.find(fields, function(f) { return f.isSelected; });
    };

    var api = {};
    api.addField = function(field) {
        fields.push(field);
    };
    api.handleLeftKey = function() {
        if(!selectedField()) {
            return questionsHandler.handleLeftKey();
        }
        return false;
    };
    api.handleRightKey = function() {
        if(!selectedField()) {
            return questionsHandler.handleRightKey();
        }
        return false;
    };
    api.handleUpKey = function() {
        if(!selectedField()) {
            return questionsHandler.handleUpKey();
        }
        return false;
    };
    api.handleDownKey = function() {
        if(!selectedField()) {
            return questionsHandler.handleDownKey();
        }
        return false;
    };
    api.handleTabKey = function() {
        var field = selectedField();
        if(field) {
            var idx = _.indexOf(fields, field);
            if(idx < fields.length-1) {
                field.toggleSelection();
                if(field.parentQuestion != fields[idx+1].parentQuestion) {
                    field.parentQuestion.toggleSelection();
                    if(field.parentQuestion.parentSection != fields[idx+1].parentQuestion.parentSection) {
                        field.parentQuestion.parentSection.toggleSelection();
                        fields[idx+1].parentQuestion.parentSection.toggleSelection();
                    }
                    fields[idx+1].parentQuestion.toggleSelection();
                }
                fields[idx+1].toggleSelection();
                return true;
            }
        } else {
            var question = questionsHandler.selectedQuestion();
            if(question) {
                question.fields[0].toggleSelection();
                return true;
            }
        }
        return false;
    };
    api.handleShiftTabKey = function() {
        var field = selectedField();
        if(field) {
            var idx = _.indexOf(fields, field);
            if(idx > 0) {
                field.toggleSelection();
                if(field.parentQuestion != fields[idx-1].parentQuestion) {
                    field.parentQuestion.toggleSelection();
                    if(field.parentQuestion.parentSection != fields[idx-1].parentQuestion.parentSection) {
                        field.parentQuestion.parentSection.toggleSelection();
                        fields[idx-1].parentQuestion.parentSection.toggleSelection();
                    }
                    fields[idx-1].parentQuestion.toggleSelection();
                }
                fields[idx-1].toggleSelection();
                return true;
            }
        }
        return false;
    };
    api.handleEscKey = function() {
        var field = selectedField();
        if(field) {
            field.toggleSelection();
            return true;
        } else {
            return questionsHandler.handleEscKey();
        }
    };
    return api;
}

function QuestionsHandler(sectionsHandler) {
    var questions = [];
    var sectionsHandler = sectionsHandler;

    var api = {};
    api.addQuestion = function(question) {
        questions.push(question);
    };
    api.selectedQuestion = function() {
        return _.find(questions, function(q) { return q.isSelected; });
    };
    api.handleLeftKey = function() {
        if(!api.selectedQuestion()) {
            return sectionsHandler.handleLeftKey();
        }
        return false;
    };
    api.handleRightKey = function() {
        if(!api.selectedQuestion()) {
            return sectionsHandler.handleRightKey();
        }
        return false;
    };
    api.handleUpKey = function() {
        var question = api.selectedQuestion();
        if(question) {
            var idx = _.indexOf(questions, question);
            if(idx > 0) {
                question.toggleSelection();
                questions[idx-1].toggleSelection();
                if(question.parentSection != questions[idx-1].parentSection) {
                    question.parentSection.toggleSelection();
                    questions[idx-1].parentSection.toggleSelection();
                }
                return true;
            }
        }
        return false;
    };
    api.handleDownKey = function() {
        var question = api.selectedQuestion();
        if(question) {
            var idx = _.indexOf(questions, question);
            if(idx < questions.length-1) {
                question.toggleSelection();
                questions[idx+1].toggleSelection();
                if(question.parentSection != questions[idx+1].parentSection) {
                    question.parentSection.toggleSelection();
                    questions[idx+1].parentSection.toggleSelection();
                }
                return true;
            }
        } else {
            var section = sectionsHandler.selectedSection();
            section.questions[0].toggleSelection();
            return true;
        }
        return false;
    };
    api.handleEscKey = function() {
        var question = api.selectedQuestion();
        if(question) {
            question.toggleSelection();
            return true;
        }
        return false;
    };
    return api;
}

function SectionsHandler() {
    var sections = [];

    var api = {};
    api.addSection = function(section) {
        sections.push(section);
    };
    api.selectedSection = function() {
        return _.find(sections, function(s) { return s.isSelected; });
    };
    api.currentSection = function() {
        return api.selectedSection();
    }
    api.handleLeftKey = function() {
        var section = api.selectedSection();
        if(section) {
            var idx = _.indexOf(sections, section);
            if(idx > 0) {
                section.toggleSelection();
                sections[idx-1].toggleSelection();
                return true;
            }
        };
        return false;
    };
    api.handleRightKey = function() {
        var section = api.selectedSection();
        if(section) {
            var idx = _.indexOf(sections, section);
            if(idx < sections.length-1) {
                section.toggleSelection();
                sections[idx+1].toggleSelection();
                return true;
            }
        };
        return false;
    };

    return api;
}

/* ======================================================= */
/* ================= FORM ELEMENT MODELS ================= */
/* ======================================================= */
function FieldModel(question, elem) {
    var element = $(elem);

    var model = {};
    model.parentQuestion = question;
    model.isSelected = false;
    model.toggleSelection = function() {
        model.isSelected = !model.isSelected;
        if(model.isSelected) {
            element.focus();
        } else {
            element.blur();
        }
    };

    return model;
}

var QuestionModel = function(section, elem) {
    var element = $(elem);
    var model = {};

    model.parentSection = section;
    model.fields = _.map(element.find("input, select"), function(i) {
        return FieldModel(model, i);
    });
    model.isSelected = false;
    model.toggleSelection = function() {
        model.isSelected = !model.isSelected;
        if(model.isSelected) {
            element.addClass("focused");
        } else {
            element.removeClass("focused");
        }
    }

    return model;
}

var SectionModel = function(elem) {
    var element = $(elem);
    var title = element.find("span.title").text();
    element.hide();

    var model = {};
    model.isSelected = false;
    model.questions = _.map(element.find("div.form_question"), function(q) {
        return QuestionModel(model, q);
    });
    model.toggleSelection = function() {
        model.isSelected = !model.isSelected;
        if(model.isSelected) {
            element.show();
        } else {
            element.hide();
        }
    }

    return model;
}


function initFormModels() {
    var sections = _.map($('section'), function(s) {
        return SectionModel(s);
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
                    handlerChainRoot.handleShiftTabKey() && key.preventDefault();
                } else {
                    handlerChainRoot.handleTabKey() && key.preventDefault();
                }
                break;
            default:
                break;
        }
    });
}
