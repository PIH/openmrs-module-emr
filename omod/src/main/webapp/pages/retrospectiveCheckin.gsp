<%
    ui.decorateWith("emr", "standardEmrPage")
    ui.includeJavascript("emr", "navigator/navigator.js", Integer.MAX_VALUE - 20)
    ui.includeJavascript("emr", "navigator/navigatorHandlers.js", Integer.MAX_VALUE - 21)
    ui.includeJavascript("emr", "navigator/navigatorModels.js", Integer.MAX_VALUE - 21)
    ui.includeJavascript("emr", "custom/retrospectiveCheckin.js", Integer.MAX_VALUE - 25)
    ui.includeCss("emr", "retrospectiveCheckin.css")
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
    <form>
        <section id="checkinInformation">
            <span class="title">Check-in information</span>

            <div class="form_question">
                ${ ui.includeFragment("emr", "field/dropdown", [
                        label: ui.message("emr.retrospectiveCheckin.location.label"),
                        formFieldName:"locations",
                        options:locations])}
            </div>

            <div class="form_question">
                ${ ui.includeFragment("emr", "field/text", [
                        label: ui.message("emr.retrospectiveCheckin.checkinDate.day.label"),
                        formFieldName: "checkinDate_day",
                        left: true
                ])}
                ${ ui.includeFragment("emr", "field/text", [
                        label: ui.message("emr.retrospectiveCheckin.checkinDate.month.label"),
                        formFieldName: "checkinDate_month",
                        left: true
                ])}
                ${ ui.includeFragment("emr", "field/text", [
                        label: ui.message("emr.retrospectiveCheckin.checkinDate.year.label"),
                        formFieldName: "checkinDate_year",
                        left: true
                ])}
            </div>
        </section>

        <section id="paymentInformation">
            <span class="title">Payment information</span>

            <div class="form_question">
                ${ ui.includeFragment("emr", "field/dropdown", [
                        label: ui.message("emr.retrospectiveCheckin.paymentReason.label"),
                        formFieldName:"paymentReason",
                        options:paymentReasons])}
            </div>
            <div class="form_question">
                ${ ui.includeFragment("emr", "field/dropdown", [
                        label: ui.message("emr.retrospectiveCheckin.paymentAmount.label"),
                        formFieldName:"paymentAmount",
                        options:paymentAmounts])}
            </div>
            <div class="form_question">
                ${ ui.includeFragment("emr", "field/text", [
                        label: ui.message("emr.retrospectiveCheckin.receiptNumber.label"),
                        formFieldName: "receiptNumber"
                ])}
            </div>
        </section>
    </form>

    <div class="actions">
        <a id="cancelButton" href="#" class="cancel">Cancel</a>
        <input id="submitButton" type="button" class="submit" value="Submit"
               data-bind="style: {visibility:paymentInfoIsValid() && checkinInfoIsValid() ? 'visible':'hidden'}, click: registerCheckin" />
    </div>

    <div id="dialogMessage"></div>
</div>