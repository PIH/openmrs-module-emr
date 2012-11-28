<%
    ui.decorateWith("emr", "standardEmrPage")

    ui.includeJavascript("emr", "custom/retrospectiveVisit.js")
%>

${ ui.includeFragment("emr", "patientHeader", [ patient: patient ]) }

<script type="text/javascript">
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

        form.render();
    });
</script>

<style type="text/css">
    .selected {
        background-color: lightblue;
    }
</style>

<div id="form">

    <h1>Retrospective Visit</h1>

    <h2>Summary</h2>
    <ul class="summary-questions"></ul>

    <h2 class="current-question-label"></h2>

    <div class="current-question-widget"></div>
</div>
