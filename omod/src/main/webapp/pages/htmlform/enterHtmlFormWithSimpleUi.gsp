<%
    ui.decorateWith("emr", "standardEmrPage")

    ui.includeJavascript("emr", "navigator/navigator.js", Integer.MAX_VALUE - 20)
    ui.includeJavascript("emr", "navigator/navigatorHandlers.js", Integer.MAX_VALUE - 21)
    ui.includeJavascript("emr", "navigator/navigatorModels.js", Integer.MAX_VALUE - 21)
    ui.includeCss("emr", "retrospectiveCheckin.css")
%>

<script type="text/javascript">
    jQuery(function() {
        KeyboardController(jq('#htmlform').first());
    });
</script>

<script type="text/javascript">
    var getValueIfLegal = function(idAndProperty) {
        var jqField = getField(idAndProperty);
        if (jqField && jqField.hasClass('illegalValue')) {
            return null;
        }
        return getValue(idAndProperty);
    }

    jq(function() {
        jq('input.submitButton').hide();

        var originalCheckNumberFunction = checkNumber;
        checkNumber = function(el, errorDivId, floatOkay, absoluteMin, absoluteMax) {
            originalCheckNumberFunction(el, errorDivId, floatOkay, absoluteMin, absoluteMax);
            if (el.className == 'illegalValue') {
                // figure out how to cancel the blur() event
            }
        }

        jq('form#htmlform').append(jq('#confirmation-template').html());

    });
</script>

${ ui.includeFragment("emr", "htmlform/enterHtmlForm", [
        patient: patient,
        formUuid: formUuid,
        htmlFormId: htmlFormId,
        visit: visit,
        returnUrl: returnUrl
]) }

<script type="text/template" id="confirmation-template">
    <div id="confirmation">
        <span class="title"><uimessage code="mirebalais.vitals.confirm.title"/></span>
        <div id="confirmationQuestion">
            (TODO translate this)
            Confirm submission? <input type="submit" value="Yes" /> or <input type="button" value="No" />
        </div>
    </div>
</script>