function FieldsHandler(questionsHandler) {
    var fields = [];
    var questionsHandler = questionsHandler;
    var selectedField = function() {
        return _.find(fields, function(f) { return f.isSelected; });
    };

    var delegateIfNoSelectedFieldTo = function(delegatedFunction) {
        if(!selectedField()) {
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
        var field = selectedField();
        if(field) {
            var idx = _.indexOf(fields, field);
            var newField = fields[fieldIndexUpdater(idx)];
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
    api.handleLeftKey = function() {
        return delegateIfNoSelectedFieldTo(questionsHandler.handleLeftKey);
    };
    api.handleRightKey = function() {
        return delegateIfNoSelectedFieldTo(questionsHandler.handleRightKey);
    };
    api.handleUpKey = function() {
        return delegateIfNoSelectedFieldTo(questionsHandler.handleUpKey);
    };
    api.handleDownKey = function() {
        return delegateIfNoSelectedFieldTo(questionsHandler.handleDownKey);
    };
    api.handleTabKey = function() {
        return switchActiveField(function(i) { return i+1; }, true);
    };
    api.handleShiftTabKey = function() {
        return switchActiveField(function(i) { return i-1; }, false);
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

    return model;
}

var Question = function(elem) {
    var previousQuestion, nextQuestion;
    var element = $(elem);
    var items = _.map(element.find("input, select"), function(i) {
        return QuestionItem(i);
    });


    var model = {};
    model.isSelected = false;

    model.definePreviousQuestion = function(question) {
        previousQuestion = question;
    };
    model.defineNextQuestion = function(question) {
        nextQuestion = question;
    };
    model.focusIn = function() {
        model.isSelected = true;
        element.addClass("focused");
    };
    model.focusOut = function() {
        model.isSelected = false;
        element.removeClass("focused");
    };
    model.moveToPreviousQuestion = function() {
        if(previousQuestion) {
            model.focusOut();
            previousQuestion.focusIn();
        }
    };
    model.moveToNextQuestion = function() {
        if(nextQuestion) {
            model.focusOut();
            nextQuestion.focusIn();
        }
    };
    model.escape = function() {
        model.focusOut();
    }


    return api;
}
