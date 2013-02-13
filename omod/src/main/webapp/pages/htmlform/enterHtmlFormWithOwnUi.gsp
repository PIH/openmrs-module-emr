<%
    ui.decorateWith("emr", "standardEmrPage")

    def breadcrumbMiddle = breadcrumbOverride ?: """
        [ { label: "${ ui.escapeJs(ui.format(patient.familyName)) }, ${ ui.escapeJs(ui.format(patient.givenName)) }" , link: '${ui.pageLink("emr", "patient", [patientId: patient.id])}'} ]
    """
%>

<script type="text/javascript">
    var breadcrumbs = _.flatten([
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        ${ breadcrumbMiddle },
        { label: "${ ui.escapeJs(ui.format(htmlForm)) }" }
    ]);
</script>

${ ui.includeFragment("emr", "patientHeader", [ patient: patient ]) }

${ ui.includeFragment("emr", "htmlform/enterHtmlForm", [
        patient: patient,
        htmlForm: htmlForm,
        visit: visit,
        returnUrl: returnUrl
]) }