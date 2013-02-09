<%
    ui.decorateWith("emr", "standardEmrPage")
    ui.includeJavascript("emr", "navigator/validators.js", Integer.MAX_VALUE - 19)
    ui.includeJavascript("emr", "navigator/navigator.js", Integer.MAX_VALUE - 20)
    ui.includeJavascript("emr", "navigator/navigatorHandlers.js", Integer.MAX_VALUE - 21)
    ui.includeJavascript("emr", "navigator/navigatorModels.js", Integer.MAX_VALUE - 21)
    ui.includeCss("emr", "simpleFormUi.css")
%>

${ ui.includeFragment("emr", "validationMessages")}

${ ui.includeFragment("emr", "patientHeader", [ patient: patient ]) }

<script type="text/javascript">

    jQuery(function() {
        jq('input.submitButton').hide();
        jq('form#htmlform').append(jq('#confirmation-template').html());

        KeyboardController(jq('#htmlform').first());
    });
</script>

${ ui.includeFragment("emr", "htmlform/enterHtmlForm", [
        patient: patient,
        formUuid: formUuid,
        htmlFormId: htmlFormId,
        visit: visit,
        returnUrl: returnUrl,
        automaticValidation: false
]) }

<script type="text/template" id="confirmation-template">
    <div id="confirmation">
        <span class="title">${ ui.message("mirebalais.vitals.confirm.title") }</span>
        <div id="confirmationQuestion">
            ${ ui.message("emr.simpleFormUi.confirm.question") }
            <input type="submit" value="${ ui.message("emr.yes") }" class="confirm"/>
            ${ ui.message("emr.simpleFormUi.confirm.or")}
            <input type="button" value="${ ui.message("emr.no") }" class="cancel" />
        </div>
        <div class="before-dataCanvas"></div>
        <div id="dataCanvas"></div>
        <div class="after-data-canvas"></div>
    </div>
</script>