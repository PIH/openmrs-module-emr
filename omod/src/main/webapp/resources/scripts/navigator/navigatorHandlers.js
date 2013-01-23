var selectedModel = function(items) {
    return _.find(items, function(i) { return i.isSelected; });
}

function FieldsKeyboardHandler(questionsHandler) {
    var fields = [];
    var questionsHandler = questionsHandler;

    var delegateIfNoSelectedFieldTo = function(delegatedFunction) {
        if(!selectedModel(fields)) {
            return delegatedFunction();
        }
        return false;
    }
    var switchActiveQuestions = function(previousFieldParentQuestion, currentFieldParentQuestion) {
        if(previousFieldParentQuestion != currentFieldParentQuestion) {
            previousFieldParentQuestion.toggleSelection();
            if(previousFieldParentQuestion.parentSection != currentFieldParentQuestion.parentSection) {
                previousFieldParentQuestion.parentSection.toggleSelection();
                currentFieldParentQuestion.parentSection.toggleSelection();
            }
            currentFieldParentQuestion.toggleSelection();
        }
    };
    var switchActiveField = function(fieldIndexUpdater, showFirstFieldIfNoneIsActive) {
        var field = selectedModel(fields);
        if(field) {
            var currentIndex = _.indexOf(fields, field);
            var nextIndex = fieldIndexUpdater(currentIndex);
            var newField = fields[nextIndex];
            if(newField) {
                field.toggleSelection();
                switchActiveQuestions(field.parentQuestion, newField.parentQuestion);
                newField.toggleSelection();
                return true;
            }
        } else {
            if(showFirstFieldIfNoneIsActive) {
                questionsHandler.selectedQuestion() || questionsHandler.handleDownKey();
                questionsHandler.selectedQuestion().fields[0].toggleSelection();
                return true;
            }
        }
        return false;
    };


    var api = {};
    api.addField = function(field) {
        fields.push(field);
    };
    api.handleUpKey = function() {
        return delegateIfNoSelectedFieldTo(questionsHandler.handleUpKey);
    };
    api.handleDownKey = function() {
        return delegateIfNoSelectedFieldTo(questionsHandler.handleDownKey);
    };
    api.handleTabKey = function() {
        var currentField = selectedModel(fields);
        var isValid = (currentField ? currentField.isValid() : true);
        if(isValid) {
            $("#error-message").css("display", "none");
            return switchActiveField(function(i) { return i+1; }, true);
        } else {
            $("#error-message").css("display", "inline-block");
            return true;
        }

    };
    api.handleShiftTabKey = function() {
        $("#error-message").css("display", "none");
        return switchActiveField(function(i) { return i-1; }, false);
    };
    api.handleEscKey = function() {
        var field = selectedModel(fields);
        if(field) {
            field.toggleSelection();
            return true;
        }
        return false;
    };
    return api;
}

function QuestionsKeyboardHandler() {
    var questions = [];

    var api = {};
    api.selectedQuestion = function() {
        return selectedModel(questions);
    };
    api.addQuestion = function(question) {
        questions.push(question);
    };
    api.handleUpKey = function() {
        var question = selectedModel(questions);
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
        var question = selectedModel(questions);
        if(!question) {
            questions[0].toggleSelection();
            questions[0].parentSection.toggleSelection();
            return true;
        }

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
        return false;
    };
    return api;
}

var SectionMouseHandler = function(sectionModels) {
    var sections = sectionModels;
    _.each(sections, function(s) {
        s.title.click( function() {
            clickedSection(s);
        });
    });

    var clickedSection = function(section) {
        _.each(sections, function(s) {
            if(section == s) {
                if(!s.isSelected) {
                    s.select();
                    s.questions[0].select();
                }
            } else {
                s.unselect();
            }
        });
    }
};

var QuestionsMouseHandler = function(questionModels) {
    var questions = questionModels;
    _.each(questions, function(q) {
        if(q.questionLi) {
            q.questionLi.click(function(event) {
                clickedQuestion(q);
                event.stopPropagation();
            });
        }
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
        f.element.mousedown(function(event) {
            clickedField(f);
            event.preventDefault();
        });
    });
    var selectedField = function() {
        return _.find(fields, function(f) { return f.isSelected; });
    };

    var clickedField = function(field) {
        var currentField = selectedField();
        if(field != currentField && currentField.toggleSelection()) {
            field.toggleSelection();
        }
    };
};