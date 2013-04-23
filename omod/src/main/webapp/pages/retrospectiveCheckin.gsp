<%
    ui.decorateWith("emr", "standardEmrPage")
    ui.includeJavascript("emr", "navigator/validators.js", Integer.MAX_VALUE - 19)
    ui.includeJavascript("emr", "navigator/navigator.js", Integer.MAX_VALUE - 20)
    ui.includeJavascript("emr", "navigator/navigatorHandlers.js", Integer.MAX_VALUE - 21)
    ui.includeJavascript("emr", "navigator/navigatorModels.js", Integer.MAX_VALUE - 21)
    ui.includeJavascript("emr", "navigator/exitHandlers.js", Integer.MAX_VALUE - 22);
    ui.includeCss("mirebalais", "simpleFormUi.css")

%>

${ ui.includeFragment("emr", "validationMessages")}

<script type="text/javascript">
    jQuery(function() {
        KeyboardController();
    });
</script>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.format(patient.familyName) }, ${ ui.format(patient.givenName) }", link:'${ui.pageLink("emr", "patient", [patientId: patient.id])}' },
        { label: "${ui.message("emr.app.retrospectiveCheckin.label")}" }
    ];
</script>

${ ui.includeFragment("emr", "patientHeader", [ patient: patient ]) }

<div id="content" class="container">
    <h2>Retrospective Check-in</h2>
    <form id="retrospectiveCheckin" method="POST">
        <input type="hidden" name="patientId" value="${patient.id}" />

        <section id="checkinInformation">
            <span class="title">Check-in info</span>

            <fieldset>
                <legend>Check-in location</legend>
                ${ ui.includeFragment("emr", "field/dropDown", [
                        label: ui.message("emr.retrospectiveCheckin.location.label"),
                        formFieldName:"locationId",
                        options:locations,
                        classes: ['required'],
                        hideEmptyLabel: true,
                        maximumSize: 5
                ])}
            </fieldset>

            <fieldset>
                <legend>Check-in provider</legend>
                ${ ui.includeFragment("emr", "field/dropDown", [
                        label: "Provider",
                        formFieldName:"providerId",
                        options:providers,
                        classes: ['required'],
                        hideEmptyLabel: true,
                        maximumSize: 5
                ])}
            </fieldset>

            <fieldset>
                <legend>Check-in date</legend>
                ${ ui.includeFragment("emr", "field/text", [
                        label: ui.message("emr.retrospectiveCheckin.checkinDate.day.label"),
                        formFieldName: "checkinDate_day",
                        classes: ['required'],
                        left: true])}
                ${ ui.includeFragment("emr", "field/text", [
                        label: ui.message("emr.retrospectiveCheckin.checkinDate.month.label"),
                        formFieldName: "checkinDate_month",
                        classes: ['required'],
                        left: true])}
                ${ ui.includeFragment("emr", "field/text", [
                        label: ui.message("emr.retrospectiveCheckin.checkinDate.year.label"),
                        formFieldName: "checkinDate_year",
                        classes: ['required'],
                        left: true])}
            </fieldset>

            <fieldset>
                <legend>Check-in time</legend>
                ${ ui.includeFragment("emr", "field/text", [
                        label: ui.message("emr.retrospectiveCheckin.checkinDate.hour.label"),
                        formFieldName: "checkinDate_hour",
                        left: true])}
                ${ ui.includeFragment("emr", "field/text", [
                        label: ui.message("emr.retrospectiveCheckin.checkinDate.minutes.label"),
                        formFieldName: "checkinDate_minutes",
                        left: true])}
            </fieldset>
        </section>

        <section id="paymentInformation">
            <span class="title">Payment info</span>

            <fieldset>
                <legend>Payment Reason</legend>
                ${ ui.includeFragment("emr", "field/dropDown", [
                        label: ui.message("emr.retrospectiveCheckin.paymentReason.label"),
                        formFieldName:"paymentReasonId",
                        options:paymentReasons,
                        hideEmptyLabel: true,
                        classes: ['required'],
                        maximumSize: 5])}
            </fieldset>
            <fieldset>
                <legend>Payment Amount</legend>
                ${ ui.includeFragment("emr", "field/dropDown", [
                        label: ui.message("emr.retrospectiveCheckin.paymentAmount.label"),
                        formFieldName:"paidAmountId",
                        options:paymentAmounts,
                        classes: ['required'],
                        hideEmptyLabel: true,
                        maximumSize: 5])}
            </fieldset>
            <fieldset>
                <legend>Receipt Number</legend>
                ${ ui.includeFragment("emr", "field/text", [
                        label: ui.message("emr.retrospectiveCheckin.receiptNumber.label"),
                        formFieldName: "receiptNumber",
                        classes: ['required']
                ])}
            </fieldset>
        </section>

        <div id="confirmation">
            <span class="title">Confirm</span>
            <div id="confirmationQuestion">
                Confirm submission? <p style="display: inline"><input type="submit" class="confirm" value="Yes" /></p> or <p style="display: inline"><input id="cancelSubmission" class="cancel" type="button" value="No" /></p>
            </div>
            <div class="before-dataCanvas"></div>
            <div id="dataCanvas"></div>
            <div class="after-data-canvas"></div>
        </div>
    </form>
</div>