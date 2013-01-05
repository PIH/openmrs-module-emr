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

<input id="cancel-form" type="button" value="${ ui.message("htmlformentry.discard") }"/>

<script type="text/javascript">
    jq(function() {
        jq('#cancel-form').click(function() {
            location.href = '${ returnUrl }';
        }).insertAfter(jq('input.submitButton'));
    });
</script>

${ ui.includeFragment("emr", "htmlform/enterHtmlForm", [
        patient: patient,
        formUuid: formUuid,
        htmlFormId: htmlFormId,
        visit: visit,
        returnUrl: returnUrl
]) }