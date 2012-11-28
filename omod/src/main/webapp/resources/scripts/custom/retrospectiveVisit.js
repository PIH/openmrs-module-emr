var FormModel = (function($) {

    return function(jqContainer, questions) {

        // private

        var templates = {};
        var loadTemplate = function(templateName) {
            $.ajax({
                url: emr.resourceLink("emr", "templates/" + templateName + ".template.html"),
                method: "GET",
                async: false,
                success: function(data) {
                    templates[templateName] = _.template(data);
                }
            });
        }
        loadTemplate("optionList");

        var currentQuestionIndex = 0;

        // public

        var api = { };

        api.questions = questions;

        api.getTemplate = function(templateName) {
            return templates[templateName];
        }

        api.getCurrentQuestionIndex = function() {
            return currentQuestionIndex;
        }

        api.getCurrentQuestionJquery = function () {
            return jqContainer.find(".current-question-widget");
        };

        api.getCurrentQuestion = function() {
            return api.questions[currentQuestionIndex];
        };

        api.previousQuestion = function() {
            if (currentQuestionIndex != null) {
                --currentQuestionIndex;
                if (currentQuestionIndex < 0) {
                    currentQuestionIndex = null;
                }
            }
            api.render();
        }

        api.nextQuestion = function() {
            if (currentQuestionIndex == null) {
                currentQuestionIndex = 0;
            } else {
                ++currentQuestionIndex;
                if (currentQuestionIndex >= questions.length) {
                    currentQuestionIndex = null;
                }
            }
            api.render();
        }

        api.render = function() {
            api.renderCurrentQuestion();
            api.renderSummary();
        };

        api.renderCurrentQuestion = function() {
            var currentQuestion = api.getCurrentQuestion();
            if (currentQuestion) {
                jqContainer.find(".current-question-label").html(currentQuestion.questionLabel);
                jqContainer.find(".current-question-widget").html(currentQuestion.widget.generateHtml(api, currentQuestion.currentValue));
            } else {
                jqContainer.find(".current-question-label").html("");
                jqContainer.find(".current-question-widget").html("");
            }
        };

        api.renderSummary = function() {
            var html = "";
            for (var i = 0; i < questions.length; ++i) {
                var question = questions[i];
                html += "<li>" + question.summaryLabel + " = " + (question.currentValue ? question.widget.formatValueForDisplay(question.currentValue) : "") + "</li>";
            }
            jqContainer.find(".summary-questions").html(html);
        };

        $('body').keydown(function(keyEvent) {
            var question = api.getCurrentQuestion();
            if (question) {
                if (question.widget && question.widget.keyDown) {
                    question.widget.keyDown(keyEvent, question, api);
                }
            } else {
                currentQuestionIndex = 0;
                api.render();
            }
        });

        return api;
    };

})(jQuery);

var OptionListWidget = (function($) {

    return function(options) {

        // private

        var options = options;
        var selectedIndex = null;

        var nextOption = function() {
            if (selectedIndex == null) {
                selectedIndex = 0;
            } else {
                selectedIndex += 1;
                if (selectedIndex >= options.length) {
                    selectedIndex = options.length - 1;
                }
            }
        };

        var previousOption = function() {
            if (selectedIndex == null) {
                selectedIndex = options.length - 1;
            } else {
                selectedIndex -= 1;
                if (selectedIndex < 0) {
                    selectedIndex = 0;
                }
            }
        };

        var refreshView = function(formModel) {
            var jqContainer = formModel.getCurrentQuestionJquery();
            jqContainer.find(".option-list > .option").removeClass("selected");
            if (selectedIndex != null) {
                jqContainer.find(".option-list > .option:nth-child(" + (selectedIndex + 1) + ")").addClass("selected");
            }
        };

        // public

        var api = {};

        api.generateHtml = function(formModel, currentValue) {
            return formModel.getTemplate("optionList")({ options: options, value: currentValue });
        };

        api.keyDown = function(keyEvent, question, formModel) {
            if (keyEvent.keyCode == 40) {
                nextOption();
                refreshView(formModel);
                keyEvent.preventDefault();
            } else if (keyEvent.keyCode == 38) {
                previousOption();
                refreshView(formModel);
                keyEvent.preventDefault();
            } else if (keyEvent.keyCode == 27) {
                selectedIndex = null;
                formModel.previousQuestion();
                refreshView(formModel);
            } else if (keyEvent.keyCode == 13) {
                question.currentValue = api.getValue();
                formModel.nextQuestion();
            }
        };

        api.getValue = function() {
            return selectedIndex == null ? null : options[selectedIndex].value;
        };

        api.formatValueForDisplay = function(value) {
            var found = _.find(options, function(item) { return item.value == value; });
            return found ? found.label : "";
        }

        return api;
    }

})(jQuery);