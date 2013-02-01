var selectedModel = function(items) {
    return _.find(items, function(i) { return i.isSelected; });
}

function FieldsKeyboardHandler(fieldModels, questionsHandler) {
    var fields = fieldModels;
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
        currentField.select();
        return true;

    };
    api.handleShiftTabKey = function() {
        return switchActiveField(function(i) { return i-1; }, false);
    };
    api.handleEnterKey = function() {
        var currentField = selectedModel(fields);
        var fieldType = currentField.element.attr("type");
        if(fieldType && fieldType.match(/submit|button/)) {
            currentField.element.click();
        } else {
            api.handleTabKey();
        }
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

function QuestionsKeyboardHandler(questionModels) {
    var questions = questionModels;

    var api = {};
    api.selectedQuestion = function() {
        return selectedModel(questions);
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
        var shouldSelectClickedSection = true;
        if(clickedSectionIndex > currentSectionIndex) {
            for(var i=currentSectionIndex; i<clickedSectionIndex; i++) {
                shouldSelectClickedSection = sections[i].isValid() && shouldSelectClickedSection;
            }
        }

        if(!shouldSelectClickedSection) {
            var selectedQuestion = selectedModel(currentSection.questions);
            var selectedField = selectedModel(selectedQuestion.fields);
            selectedField && selectedField.select();
        } else {
            currentSection.toggleSelection();
            section.toggleSelection();
            section.questions[0].toggleSelection();
            section.questions[0].fields[0].toggleSelection();
        }
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
        var shouldSelectClickedQuestion = true;
        if(clickedQuestionIndex > currentQuestionIndex) {
            for(var i=currentQuestionIndex; i<clickedQuestionIndex; i++) {
                shouldSelectClickedQuestion = questions[i].isValid() && shouldSelectClickedQuestion;
            }
        }

        if(!shouldSelectClickedQuestion) {
            var selectedField = selectedModel(currentQuestion.fields);
            selectedField && selectedField.select();
        } else {
            currentQuestion.toggleSelection();
            question.toggleSelection();
            question.fields[0].toggleSelection();
        }
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
            currentField.select();
            return;
        }

        var currentFieldIndex = _.indexOf(fields, currentField);
        var clickedFieldIndex = _.indexOf(fields, field);
        var shouldSelectClickedField = true;
        if(clickedFieldIndex > currentFieldIndex) {
            for(var i=currentFieldIndex; i<clickedFieldIndex; i++) {
                shouldSelectClickedField = fields[i].isValid() && shouldSelectClickedField;
            }
        }

        if(!shouldSelectClickedField) {
            currentField.select();
        } else {
            currentField.toggleSelection();
            field.toggleSelection();
        }
    };
};