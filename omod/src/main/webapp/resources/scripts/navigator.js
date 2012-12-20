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

    return model;
}

var Section = function(elem) {
    var previousSection, nextSection;
    var element = $(elem);
    var title = element.find("span.title").text();
    var questions = _.map(element.find("div.form_question"), function(q) {
       return Question(q);
    });
    _.each(questions, function(q, index) {
        if(index != 0) {
            q.definePreviousQuestion(questions[index-1]);
        }
        if(index != questions.len - 1) {
            q.defineNextQuestion(questions[index+1]);
        }
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
        if( isThereAQuestionSelected() ) return null;

        model.hide();
        nextSection.show();
        return nextSection;
    };
    model.moveToPreviousSection = function() {
        if( isThereAQuestionSelected() ) return null;

        model.hide();
        previousSection.show();
        return previousSection;
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
    var sections = _.map($('section'), function(s) {
        return Section(s);
    });
    _.each(sections, function(s, index) {
        if(index != 0) {
            s.hide();
            s.definePreviousSection(sections[index-1]);
        }
        if(index != sections.len - 1) {
            s.defineNextSection(sections[index+1]);
        }
    });
    var currentSection = sections[0];

    $('body').keydown(function(key) {
        var keycode = key.which;
        if(keycode == 39) {
            var s = currentSection.moveToNextSection();
            if( s ) {
                currentSection = s;
                key.preventDefault();
            }
        }
        if(keycode == 37) {
            var s = currentSection.moveToPreviousSection();
            if( s ) {
                currentSection = s;
                key.preventDefault();
            }
        }

        if(keycode == 38) {
            currentSection.moveToPreviousQuestion();
        }
        if(keycode == 40) {
            currentSection.moveToNextQuestion();
        }
    });
}
