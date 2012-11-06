<%
    ui.decorateWith("emr", "standardEmrPage")
%>

<script type="text/template" id="options-question-template">
    <select id="current-question-widget">
        {{ jq.each(options, function(i, item) { }}
            <option value="{{- item.value }}">{{- item.labelCode }}</option>
        {{ }); }}
    </select>
</script>

<script type="text/javascript">
    var optionsQuestionTemplate = _.template(jq('#options-question-template').html());

    var wizardConfig = ${ wizardConfigString };

    function WizardModel() {
        return {
            currentStep: 0,
            data: { }
        };
    }

    var model = WizardModel();

    function showStep(stepNumber) {
        var step = wizardConfig.steps[stepNumber];

        var stepContent = "";
        if (step.stepType == 'pointOfCareChooseVisit') {
            stepContent = "Must have a current visit";
        }
        else if (step.stepType == 'obs') {
            if (step.questionType == 'options') {
                stepContent = optionsQuestionTemplate(step)
            }
            else {
                throw "Unknown question type for obs step: " + step.questionType;
            }
        }
        else {
            throw "Unknown step type: " + step.stepType;
        }

        if (!step.labelCode) {
            step.labelCode = "&nbsp;";
        }

        jq('#current-step-content').html(stepContent);
        jq('#current-step-title').html(step.labelCode);
        jq('#current-step').html(stepNumber + 1);
    }

    jq(function() {
        jq(document).on('keyup', function(event) {
            if (event.which === 13 || event.which === 40) {
                model.currentStep += 1;
            } else if (event.which === 38) {
                model.currentStep -= 1;
            }
            if (model.currentStep < 0) {
                model.currentStep = 0;
            } else if (model.currentStep >= wizardConfig.steps.length) {
                model.currentStep = wizardConfig.steps.length - 1;
            }
            showStep(model.currentStep);
        });
        jq('#current-step-content').focus();

        showStep(0);
    });
</script>

<style type="text/css">
    .step-title {
        font-weight: bold;
        font-size: 2em;
    }

    #previous-step-title, #next-step-title {
        font-size: 1.5em;
        color: #808080;
    }

    #current-step-title {
        color: #00008b;
    }

    #current-step-content {
        background-color: #add8e6;
        padding: 3em;
    }

</style>

<div id="wizard-title">
    Wizard: ${ ui.message(wizardConfig.titleCode) }
    Step: <span id="current-step"></span>
</div>

<div class="step-title" id="previous-step-title">
    Previous Question Title
</div>

<div class="step-title" id="current-step-title">
    This Question Title
</div>

<div id="current-step-content">

</div>

<div class="step-title" id="next-step-title">
    Next Question Title
</div>