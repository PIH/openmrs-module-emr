<%
    ui.decorateWith("emr", "standardEmrPage")
%>

${ ui.includeFragment("emr", "patientHeader", [ patient: patient ]) }

${ ui.includeFragment("emr", "htmlform/enterHtmlForm", [
        patient: patient,
        formUuid: formUuid,
        htmlFormId: htmlFormId,
        visit: visit,
        returnUrl: returnUrl
]) }