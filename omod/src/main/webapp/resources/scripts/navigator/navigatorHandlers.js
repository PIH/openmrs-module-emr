function FieldsHandler(questionsHandler) {
    var fields = [];
    var questionsHandler = questionsHandler;
    var selectedField = function() {
        return _.find(fields, function(f) { return f.isSelected; });
    };

    model.isSelected = false;
    model.focusIn = function() {
        model.isSelected = true;
        element.focus();
    };
    model.focusOut = function() {
        model.isSelected = false;
        element.blur();
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
