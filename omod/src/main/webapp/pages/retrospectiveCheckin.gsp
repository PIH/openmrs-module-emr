<%
    ui.decorateWith("emr", "standardEmrPage")
    ui.includeJavascript("emr", "knockout-2.2.0.js")
    ui.includeJavascript("emr", "navigator.js")
    ui.includeJavascript("emr", "custom/retrospectiveCheckin.js")
    ui.includeCss("emr", "retrospectiveCheckin.css")
%>

<script type="text/javascript">
    jQuery(function() {
        ko.applyBindings(RetrospectiveCheckinViewModel(), jq('#content').get(0));
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
    <span class="value" data-bind="text:patientIdentifier"></span> -
    <span class="value" data-bind="text:locationName"></span> -
    <span class="value" data-bind="text:checkinDate"></span>

    <ul>
        <li>
            <label for="patientIdentifier">Patient identifier</label>
            <input id="patientIdentifier" type="text" data-bind="value:patientIdentifier" />
        </li>
        <li data-bind="template: {name:'optionsList-template', foreach:locations}"></li>
        <li>
            <label for="checkinDate">Check-in date</label>
            <input id="checkinDate" type="date" data-bind="value:checkinDate" placeholder="  /  /  "/>
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
    <input id="submitButton" type="button" class="submit" value="Submit" />
</div>