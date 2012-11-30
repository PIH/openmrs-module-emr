_.templateSettings = original_templateSettings;

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

        var currentQuestionIndex = 0;

        // public

        var api = { };

        api.questions = questions;

        api.getTemplate = function(templateName) {
            if (!templates[templateName]) {
                loadTemplate(templateName);
            }
            return templates[templateName];
        }

        api.getCurrentQuestionIndex = function() {
            return currentQuestionIndex;
        }

        api.getCurrentWidgetJquery = function () {
            return jqContainer.find(".current-question .widget");
        };

        api.getContext = function() {
            return {
                jQueryContainer: api.getCurrentWidgetJquery(),
                getTemplate: api.getTemplate,
                nextField: api.nextQuestion,
                previousField: api.previousQuestion
            };
        }

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
            api.renderPreviousQuestions();
            api.renderUpcomingQuestions();
        };

        api.renderCurrentQuestion = function() {
            var currentQuestion = api.getCurrentQuestion();
            if (currentQuestion) {
                jqContainer.find(".current-question .label").html(currentQuestion.summaryLabel);
                jqContainer.find(".current-question .title").html(currentQuestion.questionLabel);
                jqContainer.find(".current-question .widget").html(currentQuestion.widget.generateHtml(api.getContext(), currentQuestion.currentValue));
                if (currentQuestion.widget.focus) {
                    currentQuestion.widget.focus(api.getContext(), currentQuestion);
                }
            } else {
                jqContainer.find(".current-question .label").html("");
                jqContainer.find(".current-question .title").html("");
                jqContainer.find(".current-question .widget").html("");
            }
        };

        api.renderPreviousQuestions = function() {
            var toDisplay = questions; // if no question is selected, we display all
            if (currentQuestionIndex != null) {
                toDisplay = questions.slice(0, currentQuestionIndex);
            }
            jqContainer.find(".previous-questions").html(api.getTemplate("previousQuestions")({ questions: toDisplay }));
        }

        api.renderUpcomingQuestions = function() {
            var toDisplay = []; // if no question is selected, we display none
            if (currentQuestionIndex != null) {
                toDisplay = questions.slice(currentQuestionIndex + 1, questions.length);
            }
            jqContainer.find(".upcoming-questions").html(api.getTemplate("upcomingQuestions")({ questions: toDisplay }));
        }

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
                    question.widget.keyDown(keyEvent, question, api.getContext());
                }
            } else {
                if (keyEvent.keyCode == 38 || keyEvent.keyCode == 27) { // up or escape
                    currentQuestionIndex = questions.length - 1;
                } else if (keyEvent.keyCode == 40 || keyEvent.keyCode == 13) { // down or enter
                    currentQuestionIndex = 0;
                }
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

        var refreshView = function(context) {
            var jqContainer = context.jQueryContainer;
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

        api.keyDown = function(keyEvent, question, context) {
            if (keyEvent.keyCode == 40) {
                nextOption();
                refreshView(context);
                keyEvent.preventDefault();
            } else if (keyEvent.keyCode == 38) {
                previousOption();
                refreshView(context);
                keyEvent.preventDefault();
            } else if (keyEvent.keyCode == 27) {
                selectedIndex = null;
                context.previousField();
                refreshView(context);
            } else if (keyEvent.keyCode == 13) {
                question.currentValue = api.getValue();
                context.nextField();
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


var FreeTextWidget = (function($) {

    return function() {

        // private

        var summarize = function(string) {
            return string.length > 100 ? (string.slice(0, 100) + "...") : string;
        };

        // public

        var api = {};

        api.generateHtml = function(context, currentValue) {
            return context.getTemplate("freeText")({ value: currentValue });
        };

        api.focus = function(context) {
            context.jQueryContainer.find("input[type=text]").focus();
        };

        api.keyDown = function(keyEvent, question, context) {
            if (keyEvent.keyCode == 13) {
                question.currentValue = api.getValue(context.jQueryContainer);
                context.nextField();
                keyEvent.preventDefault();
            }
        };

        api.getValue = function(jqContainer) {
            return jqContainer.find("input[type=text]").val();
        };

        api.formatValueForDisplay = function(value) {
            return value;
        };

        return api;
    }

})(jQuery);


var CompoundWidget = (function($) {

    // config should be a list of objects with label and widget properties
    return function(config) {

        // private
        var currentWidgetIndex = 0;

        var getChildContext = function(parentContext, index) {
            return {
                jQueryContainer: parentContext.jQueryContainer.find(".compound-" + index),
                getTemplate: parentContext.getTemplate,
                nextField: function() { return api.nextWidget(parentContext) },
                previousField: function() { return api.previousWidget(parentContext) }
            };
        };

        var getChildWidget = function(index) {
            return config[index].widget;
        };

        var refreshView = function(parentContext) {
            parentContext.jQueryContainer.find(".compound-widget-container").removeClass("selected");
            if (currentWidgetIndex != null) {
                parentContext.jQueryContainer.find(".compound-" + currentWidgetIndex).addClass("selected");
                var widget = getChildWidget(currentWidgetIndex);
                if (widget && widget.focus) {
                    widget.focus(getChildContext(parentContext, currentWidgetIndex));
                }
            }
        }


        // public
        var api = {};

        api.previousWidget = function(context) {
            if (currentWidgetIndex != null) {
                --currentWidgetIndex;
                if (currentWidgetIndex < 0) {
                    currentWidgetIndex = null;
                    context.previousField();
                }
            }
            refreshView(context);
        }

        api.nextWidget = function(context) {
            if (currentWidgetIndex == null) {
                currentWidgetIndex = 0;
            } else {
                ++currentWidgetIndex;
                if (currentWidgetIndex >= config.length) {
                    currentWidgetIndex = null;
                    context.nextField();
                }
            }
            refreshView(context);
        }

        api.generateHtml = function(context, currentValue) {
            var html = "<div><table><tr>";
            _.each(config, function(item, index) {
                html += "<td>" + item.label + "</td>"
            });
            html += "</tr><tr>";
            _.each(config, function(item, index) {
                html += '<td class="compound-widget-container compound-' + index + '">' + item.widget.generateHtml(getChildContext(context, index), null) + '</td>';
            });
            html += "</tr></table></div>"
            return html;
        };

        api.focus = function(context) {
            currentWidgetIndex = 0;

            if (config[0].widget.focus) {
                config[0].widget.focus(getChildContext(context, 0));
            }
        };

        api.keyDown = function(keyEvent, question, context) {
            var widget = getChildWidget(currentWidgetIndex);
            if (widget && widget.keyDown) {
                widget.keyDown(keyEvent, question, getChildContext(context, currentWidgetIndex));
            } else {
                if (keyEvent.keyCode == 13) {
                    question.currentValue = api.getValue(context.jQueryContainer);
                    context.nextField();
                    keyEvent.preventDefault();
                }
            }
        };

        api.getValue = function(jQueryContainer) {
            // TODO
        };

        api.formatValueForDisplay = function(value) {
            // TODO
        }

        return api;

    }

})(jQuery);


var DateAndTimeWidget = (function($) {

    return function() {

        // private

        var asComponents = function(date) {
            if (date) {
                return {
                    year: date.getFullYear(),
                    month: date.getMonth() + 1,
                    day: date.getDate(),
                    hour: date.getHours(),
                    minute: date.getMinutes(),
                    seconds: date.getSeconds()
                };
            } else {
                return {
                    year: null,
                    month: null,
                    day: null,
                    hour: null,
                    minute: null,
                    seconds: null
                };
            }
        };

        // public

        var api = {};

        api.generateHtml = function(context, currentValue) {
            return context.getTemplate("dateAndTime")({ value: asComponents(currentValue) });
        };

        api.keyDown = function(keyEvent, question, context) {
            if (keyEvent.keyCode == 13) {
                question.currentValue = api.getValue();
                context.nextField();
                keyEvent.preventDefault();
            }
        };

        api.getValue = function() {
            return null;
        };

        api.formatValueForDisplay = function(value) {
            return "TODO";
        }

        return api;
    }

})(jQuery);