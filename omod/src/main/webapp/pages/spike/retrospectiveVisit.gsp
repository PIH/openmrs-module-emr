<%
    ui.decorateWith("emr", "standardEmrPage")

    ui.includeJavascript("emr", "custom/retrospectiveVisit.js", Integer.MIN_VALUE)
%>

${ ui.includeFragment("emr", "patientHeader", [ patient: patient ]) }

<script type="text/javascript">
    jq(function () {
        var dateTimeQuestionConfig = [
            { label: "Day", widget: FreeTextWidget() },
            { label: "Month", widget: OptionListWidget([
                { label: "January", value: 1},
                { label: "February", value: 2},
                { label: "March", value: 3},
                { label: "April", value: 4},
                { label: "May", value: 5},
                { label: "June", value: 6},
                { label: "July", value: 7},
                { label: "August", value: 8},
                { label: "September", value: 9},
                { label: "October", value: 10},
                { label: "November", value: 11},
                { label: "December", value: 12}
            ]) },
            { label: "Year", widget: FreeTextWidget() }
        ];

        var form = FormModel(jq("#form"), [
            {
                summaryLabel: "Date & Time",
                questionLabel: "Check-In Date and Time",
                currentValue: new Date(),
                widget: CompoundWidget(dateTimeQuestionConfig)
            },
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
            },
            {
                summaryLabel: "Payment",
                questionLabel: "Payment Amount",
                currentValue: null,
                widget: OptionListWidget([
                    { label: '100', value: 100 },
                    { label: '50', value: 50 },
                    { label: 'Exempt', value: 0 }
                ])
            },
            {
                summaryLabel: "Receipt",
                questionLabel: "Receipt Number",
                currentValue: null,
                widget: FreeTextWidget()
            }
        ]);

        form.render();
    });
</script>

<style type="text/css">
    .selected {
        background-color: lightblue;
    }

    .previous-questions {
        border-bottom: 1px black dashed;
        padding-bottom: 1em;
        margin-bottom: 1em;
        background-color: #d3d3d3;
        color: darkgray;
    }

    .upcoming-questions {
        border-top: 1px black dashed;
        padding-top: 1em;
        margin-top: 1em;
        background-color: #d3d3d3;
        color: darkgray;
    }

    .previous-question .label {
        display: inline-block;
        width: 200px;
    }

    .upcoming-question .label {
        display: inline-block;
        width: 200px;
    }

    .current-question .title {
        font-size: 2em;
        color: blue;
        padding-bottom: 10px;
    }

    .current-question .widget {
        margin-left: 200px;
    }

</style>

<div id="form">

    <h1>Retrospective Check-In</h1>

    <div class="previous-questions"></div>

    <div class="current-question">
        <div class="title"></div>

        <div class="widget"></div>
    </div>

    <div class="upcoming-questions"></div>

</div>
