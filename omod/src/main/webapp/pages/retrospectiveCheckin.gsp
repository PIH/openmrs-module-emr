<%
    ui.decorateWith("emr", "standardEmrPage")

    switch(uiOption) {
        case 0:
            ui.includeJavascript("emr", "navigator/navigator.js", Integer.MAX_VALUE - 20)
            ui.includeJavascript("emr", "navigator/navigatorHandlers.js", Integer.MAX_VALUE - 21)
            ui.includeJavascript("emr", "navigator/navigatorModels.js", Integer.MAX_VALUE - 21)
            ui.includeJavascript("emr", "custom/retrospectiveCheckin.js", Integer.MAX_VALUE - 25)
            ui.includeCss("emr", "retrospectiveCheckin.css")
            break
        case 1:
            ui.includeJavascript("emr", "navigator/navigator.js", Integer.MAX_VALUE - 20)
            ui.includeJavascript("emr", "navigator/navigatorHandlers.js", Integer.MAX_VALUE - 21)
            ui.includeJavascript("emr", "navigator/navigatorModels.js", Integer.MAX_VALUE - 21)
            ui.includeJavascript("emr", "custom/retrospectiveCheckin.js", Integer.MAX_VALUE - 25)
            ui.includeCss("emr", "retrospectiveCheckin_alt1.css", Integer.MAX_VALUE)
            break
        case 2:
            ui.includeJavascript("emr", "navigator/navigator.js", Integer.MAX_VALUE - 20)
            ui.includeJavascript("emr", "navigator/navigatorHandlers.js", Integer.MAX_VALUE - 21)
            ui.includeJavascript("emr", "navigator/navigatorModels.js", Integer.MAX_VALUE - 21)
            ui.includeJavascript("emr", "custom/retrospectiveCheckin.js", Integer.MAX_VALUE - 25)
            ui.includeCss("emr", "retrospectiveCheckin_alt2.css")
            break
    }

%>

<script type="text/javascript">
    jQuery(function() {
        KeyboardController();
    });
</script>

${ ui.includeFragment("emr", "patientHeader", [ patient: patient ]) }

<div id="content" class="container">
    <h1>Retrospective Check-in</h1>
    <form method="POST">
        <input type="hidden" name="patientId" value="${patient.id}" />

        <section id="checkinInformation">
            <span class="title">Check-in info</span>

            <fieldset>
                <legend>Check-in location</legend>
                ${ ui.includeFragment("emr", "field/dropDown", [
                        label: ui.message("emr.retrospectiveCheckin.location.label"),
                        formFieldName:"locationId",
                        options:locations,
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
                        hideEmptyLabel: true,
                        maximumSize: 5
                ])}
            </fieldset>

            <fieldset>
                <legend>Check-in date</legend>
                ${ ui.includeFragment("emr", "field/text", [
                        label: ui.message("emr.retrospectiveCheckin.checkinDate.day.label"),
                        formFieldName: "checkinDate_day",
                        left: true])}
                ${ ui.includeFragment("emr", "field/text", [
                        label: ui.message("emr.retrospectiveCheckin.checkinDate.month.label"),
                        formFieldName: "checkinDate_month",
                        left: true])}
                ${ ui.includeFragment("emr", "field/text", [
                        label: ui.message("emr.retrospectiveCheckin.checkinDate.year.label"),
                        formFieldName: "checkinDate_year",
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
                        maximumSize: 5])}
            </fieldset>
            <fieldset>
                <legend>Payment Amount</legend>
                ${ ui.includeFragment("emr", "field/dropDown", [
                        label: ui.message("emr.retrospectiveCheckin.paymentAmount.label"),
                        formFieldName:"paidAmountId",
                        options:paymentAmounts,
                        hideEmptyLabel: true,
                        maximumSize: 5])}
            </fieldset>
            <fieldset>
                <legend>Receipt Number</legend>
                ${ ui.includeFragment("emr", "field/text", [
                        label: ui.message("emr.retrospectiveCheckin.receiptNumber.label"),
                        formFieldName: "receiptNumber"
                ])}
            </fieldset>
        </section>

        <div id="confirmation">
            <span class="title">Confirm</span>
            <div id="confirmationQuestion">
                Confirm submission? <input type="submit" value="Yes" /> or <input type="button" value="No" />
            </div>
        </div>
    </form>
</div>