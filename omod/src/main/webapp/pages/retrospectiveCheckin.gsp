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
            ui.includeCss("emr", "retrospectiveCheckin_alt1.css")
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
<script type="text/html" id="optionsList-template">
    <label data-bind="text:label, attr: {for: widgetId}"></label>
    <div class="optionsList" data-bind="foreach: options, attr: {id:widgetId}">
        <span class="option"
              data-bind="text: name, click:\$parent.selectOption, css:{selectedOption: selected}"></span>
    </div>
</script>


${ ui.includeFragment("emr", "patientHeader", [ patient: patient ]) }

<div id="content" class="container">
    <h3 class="title">Retrospective Check-in</h3>
    <form method="POST">
        <input type="hidden" name="patientId" value="${patient.id}" />

        <section id="checkinInformation">
            <span class="title">Check-in info</span>

            <fieldset>
                <legend>Check-in location</legend>
                ${ ui.includeFragment("emr", "field/dropdown", [
                        label: ui.message("emr.retrospectiveCheckin.location.label"),
                        formFieldName:"locationId",
                        options:locations,
                        showEmptyLabel: false,
                        maximumSize: 8
                ])}
            </fieldset>

            <fieldset>
                <legend>Check-in provider</legend>
                ${ ui.includeFragment("emr", "field/dropdown", [
                        label: "Provider",
                        formFieldName:"locationId",
                        options:locations,
                        showEmptyLabel: false,
                        maximumSize: 8
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
        </section>

        <section id="paymentInformation">
            <span class="title">Payment info</span>

            <fieldset>
                <legend>Payment Reason</legend>
                ${ ui.includeFragment("emr", "field/dropdown", [
                        label: ui.message("emr.retrospectiveCheckin.paymentReason.label"),
                        formFieldName:"paymentReasonId",
                        options:paymentReasons,
                        showEmptyLabel: false,
                        maximumSize: 10])}
            </fieldset>
            <fieldset>
                <legend>Payment Amount</legend>
                ${ ui.includeFragment("emr", "field/dropdown", [
                        label: ui.message("emr.retrospectiveCheckin.paymentAmount.label"),
                        formFieldName:"paidAmountId",
                        options:paymentAmounts,
                        showEmptyLabel: false,
                        maximumSize: 10])}
            </fieldset>
            <fieldset>
                <legend>Receipt Number</legend>
                ${ ui.includeFragment("emr", "field/text", [
                        label: ui.message("emr.retrospectiveCheckin.receiptNumber.label"),
                        formFieldName: "receiptNumber"
                ])}
            </fieldset>

            <fieldset>
                <legend>Payment Reason</legend>
                ${ ui.includeFragment("emr", "field/dropdown", [
                        label: ui.message("emr.retrospectiveCheckin.paymentReason.label"),
                        formFieldName:"paymentReasonId",
                        options:paymentReasons,
                        showEmptyLabel: false,
                        maximumSize: 10])}
            </fieldset>
            <fieldset>
                <legend>Payment Amount</legend>
                ${ ui.includeFragment("emr", "field/dropdown", [
                        label: ui.message("emr.retrospectiveCheckin.paymentAmount.label"),
                        formFieldName:"paidAmountId",
                        options:paymentAmounts,
                        showEmptyLabel: false,
                        maximumSize: 10])}
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