var QuestionItem = function(elem) {
    var model = {};
    var element = $(elem);

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
    var model = {};
    var element = $(elem);
    var items = _.map(element.find("input, select"), function(i) {
        return QuestionItem(i);
    })

    model.isSelected = false;

    model.focusIn = function() {
        model.isSelected = true;
    };
    model.focusOut = function() {
        model.isSelected = false;
    };

    return model;
}

var Section = function(elem) {
    var previousSection, nextSection;
    var element = $(elem);
    var title = element.find("span.title").text();
    var questions = _.map(element.find("div.form_question"), function(q) {
       return Question(q);
    });

    var isThereAQuestionSelected = function() {
        return _.some(questions, function(q) {
            return q.isSelected;
        })
    };
    var getSelectedQuestion = function() {
        return _.find(questions, function(q) {
            return q.isSelected;
        });
    }

    var model = {};
    model.isSelected = false;
    model.definePreviousSection = function(section) {
        previousSection = section;
    };
    model.defineNextSection = function(section) {
        nextSection = section;
    };
    model.moveToNextSection = function() {
        if( isThereAQuestionSelected() ) return false;

        model.hide();
        nextSection.show();
        return true;
    };
    model.moveToPreviousSection = function() {
        if( isThereAQuestionSelected() ) return false;

        model.hide();
        previousSection.show();
        return true;
    };

    model.moveToNextQuestion = function() {
        var currentQuestion = getSelectedQuestion();
        if( currentQuestion ) {
            currentQuestion.moveToNextQuestion();
        } else {
            questions[0].focusIn();
        }
    };
    model.moveToPreviousQuestion = function() {
        var currentQuestion = getSelectedQuestion();
        if( currentQuestion ) {
            currentQuestion.moveToPreviousQuestion();
        }
    }

    model.hide = function() {
        model.isSelected = false;
        element.hide();
    };
    model.show = function() {
        model.isSelected = true;
        element.show();
    }

    return model;
}

function KeyboardController() {
    $('body').keydown(function(ev) {

    });
}

function initFormNavigation(submitElement, cancelElement) {
    sections = _.map($('section'), function(s) {
        return Section(s);
    });
    var p, n;
    _.each(sections, function(s, index) {
        if(index != 0) {
            s.hide();
            s.definePreviousSection(sections[index-1]);
        }
        if(index != sections.len - 1) {
            s.defineNextSection(sections[index+1]);
        }
    });
}