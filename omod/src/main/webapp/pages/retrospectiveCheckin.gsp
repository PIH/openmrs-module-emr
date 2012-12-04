<%
    ui.decorateWith("emr", "standardEmrPage")
    ui.includeJavascript("emr", "navigator.js", Integer.MAX_VALUE - 20)
    ui.includeJavascript("emr", "custom/retrospectiveCheckin.js", Integer.MAX_VALUE - 25)
    ui.includeCss("emr", "retrospectiveCheckin.css")
%>

<script type="text/javascript">
    jQuery(function() {
        ko.applyBindings(RetrospectiveCheckinViewModel(${locations}, ${paymentReasons}, ${paymentAmounts}), jq('#content').get(0));
        FormNavigator(jQuery('#submitButton'), jQuery('#cancelButton'));
    });
</script>
<script type="text/html" id="optionsList-template">
    <label data-bind="text:label, attr: {for: widgetId}"></label>
    <div class="optionsList" data-bind="foreach: options, attr: {id:widgetId}">
        <span class="option"
              data-bind="text: name, click:\$parent.selectOption, css:{selectedOption: selected}"></span>
    </div>
</script>


<h1>Retrospective Check-in</h1>
<section id="checkinInformation">
    <img class="field_check" src=${ui.resourceLink("emr", "images/checked.png")}
        data-bind="style: {visibility: checkinInfoIsValid() ? 'visible':'hidden'}"/>
    <span class="label">Check-in information</span>
    <span class="value" data-bind="text:patientName"></span> -
    <span class="value" data-bind="text:locationName"></span> -
    <span class="value" data-bind="text:checkinDate"></span>

    <ul>
        <li>
            <label for="patientIdentifier">Patient identifier</label>
            <input id="patientIdentifier" type="text" data-bind="value:patientIdentifier" />
        </li>
        <li data-bind="template: {name:'optionsList-template', foreach:locations}"></li>
        <li>
            <label for="checkinDate_day">Check-in date</label>
            <input id="checkinDate_day" type="text" data-bind="value:checkinDay" maxlength="2" placeholder="Day"/>
            <span> / </span>
            <input id="checkinDate_month" type="text" data-bind="value:checkinMonth" maxlength="2" placeholder="Month"/>
            <span> / </span>
            <input id="checkinDate_year" type="text" data-bind="value:checkinYear" maxlength="4" placeholder="Year"/>
        </li>
        <li>
            <label for="checkinTime_hour">Check-in time</label>
            <input id="checkinTime_hour" data-bind="value:checkinHour" maxlength="2" placeholder="Hour"/>
            <span> : </span>
            <input id="checkinTime_minutes" data-bind="value:checkinMinutes" maxlength="2" placeholder="Minutes"/>
        </li>
    </ul>
</section>

<section id="paymentInformation">
    <img class="field_check" src=${ui.resourceLink("emr", "images/checked.png")}
        data-bind="style: {visibility: paymentInfoIsValid() ? 'visible':'hidden'}" />
    <span class="label">Payment</span>
    <span class="value" data-bind="text:paymentReason"></span>
    <span class="value" data-bind="text:amountPaid"></span>

    <ul>
        <li data-bind="template: {name:'optionsList-template', foreach:paymentReasons}"></li>
        <li data-bind="template: {name:'optionsList-template', foreach:paymentAmounts}"></li>
    </ul>
</section>

<div class="actions">
    <a id="cancelButton" href="#" class="cancel">Cancel</a>
    <input id="submitButton" type="button" class="submit" value="Submit" data-bind="click: registerCheckin"/>
</div>

<div id="dialogMessage"></div>