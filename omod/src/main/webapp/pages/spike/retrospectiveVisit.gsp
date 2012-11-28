<%
    ui.decorateWith("emr", "standardEmrPage")
%>

<script type="text/template" id="optionList-template">
    <div class="option-list">
        {{ _.each(options, function(option) { }}
            <div class="option">
                {{- option.label }}
            </div>
        {{ }); }}
    </div>
</script>

<script type="text/javascript">

    var FormModel = function(jqContainer, questions) {

        var currentQuestionIndex = 0;

        var api = { };

        api.questions = questions;

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
                jqContainer.find(".current-question-widget").html(currentQuestion.widget.generateHtml());
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


        return api;
    }

    var optionListTemplate = _.template(jq('#optionList-template').html());

    var OptionListWidget = function(options) {
        var options = options;
        var selectedIndex = null;

        var api = {};

        api.generateHtml = function() {
            return optionListTemplate({ options: options });
        };

        api.nextOption = function() {
            if (selectedIndex == null) {
                selectedIndex = 0;
            } else {
                selectedIndex += 1;
                if (selectedIndex >= options.length) {
                    selectedIndex = options.length - 1;
                }
            }
        };

        api.previousOption = function() {
            if (selectedIndex == null) {
                selectedIndex = options.length - 1;
            } else {
                selectedIndex -= 1;
                if (selectedIndex < 0) {
                    selectedIndex = 0;
                }
            }
        };

        api.keyDown = function(keyEvent, question, formModel) {
            if (keyEvent.keyCode == 40) {
                api.nextOption();
                api.refreshView(formModel);
                keyEvent.preventDefault();
            } else if (keyEvent.keyCode == 38) {
                api.previousOption();
                api.refreshView(formModel);
                keyEvent.preventDefault();
            } else if (keyEvent.keyCode == 27) {
                selectedIndex = null;
                formModel.previousQuestion();
                api.refreshView(formModel);
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

        api.refreshView = function(formModel) {
            var jqContainer = formModel.getCurrentQuestionJquery();
            jqContainer.find(".option-list > .option").removeClass("selected");
            if (selectedIndex != null) {
                jqContainer.find(".option-list > .option:nth-child(" + (selectedIndex + 1) + ")").addClass("selected");
            }
        }

        return api;
    }

    jq(function () {
        var form = FormModel(jq("#form"), [
            {
                summaryLabel: "Location",
                questionLabel: "Choose a Location",
                currentValue: null,
                widget: OptionListWidget([
                    { label: 'Outpatient', value: 1 },
                    { label: 'ER', value: 2 }
                ])
            },
            {
                summaryLabel: "Reason",
                questionLabel: "Reason for Visit",
                currentValue: null,
                widget: OptionListWidget([
                    { label: 'Regular Visit', value: 1 },
                    { label: 'Marriage Certificate', value: 2 },
                    { label: 'Medical Certificate', value: 3 },
                    { label: 'Dental Visit', value: 4 }
                ])
            }
        ]);

        jq('body').keydown(function(keyEvent) {
            var question = form.getCurrentQuestion();
            if (question && question.widget && question.widget.keyDown) {
                question.widget.keyDown(keyEvent, question, form);
            }
        });

        form.render();
    });
</script>

<style type="text/css">
    .selected {
        background-color: blue;
    }
</style>

<div id="form">

    <h2>Summary</h2>
    <ul class="summary-questions"></ul>

    <h2 class="current-question-label"></h2>

    <div class="current-question-widget"></div>
</div>
