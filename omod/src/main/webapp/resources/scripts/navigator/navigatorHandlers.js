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
            return switchActiveField(function(i) { return i+1; }, true);
        }
        return true;

    };
    api.handleShiftTabKey = function() {
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

        if(!question.isValid()) {
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
    _.each(sections, function(section) {
        section.title.click( function(event) {
            event.stopPropagation();
            clickedSection(section);
        });
    });

    var clickedSection = function(section) {
        var currentSection = selectedModel(sections);
        if(currentSection == section) {
            return;
        }

        var currentSectionIndex = _.indexOf(sections, currentSection);
        var clickedSectionIndex = _.indexOf(sections, section);
        if(clickedSectionIndex > currentSectionIndex && !currentSection.isValid()) {
            var selectedQuestion = selectedModel(currentSection.questions);
            var selectedField = selectedModel(selectedQuestion.fields);
            selectedField && selectedField.select();
            return
        }
        currentSection.toggleSelection();
        section.toggleSelection();
    }
};

var QuestionsMouseHandler = function(questionModels) {
    var questions = questionModels;
    _.each(questions, function(question) {
        if(question.questionLi) {
            question.questionLi.click(function(event) {
                event.stopPropagation();
                clickedQuestion(question);
            });
        }
    });

    var clickedQuestion = function(question) {
        var currentQuestion = selectedModel(questions);
        if(currentQuestion == question) {
            return;
        }

        var currentQuestionIndex = _.indexOf(questions, currentQuestion);
        var clickedQuestionIndex = _.indexOf(questions, question);
        if(clickedQuestionIndex > currentQuestionIndex && !currentQuestion.isValid()) {
            var selectedField = selectedModel(currentQuestion.fields);
            selectedField && selectedField.select();
            return;
        }

        currentQuestion.toggleSelection();
        question.toggleSelection();
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
        if(currentField == field) {
            return;
        }

        var currentFieldIndex = _.indexOf(fields, currentField);
        var clickedFieldIndex = _.indexOf(fields, field);
        if(clickedFieldIndex > currentFieldIndex && !currentField.isValid()) {
            return;
        }
        currentField.toggleSelection();
        field.toggleSelection();
    };
};