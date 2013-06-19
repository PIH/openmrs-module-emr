<%
    ui.decorateWith("appui", "standardEmrPage")

    def breadcrumbMiddle = breadcrumbOverride ?: """
        [ { label: "${ ui.escapeJs(ui.format(patient.familyName)) }, ${ ui.escapeJs(ui.format(patient.givenName)) }" , link: '${ui.pageLink("coreapps", "patientdashboard/patientDashboard", [patientId: patient.id])}'} ]
    """
%>

<script type="text/javascript">
    var breadcrumbs = _.flatten([
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        ${ breadcrumbMiddle },
        { label: "${ ui.escapeJs(ui.message("emr.editHtmlForm.breadcrumb", ui.format(htmlForm.form))) }" }
    ]);
</script>

${ ui.includeFragment("coreapps", "patientHeader", [ patient: patient ]) }

${ ui.includeFragment("emr", "htmlform/enterHtmlForm", [
        encounter: encounter,
        patient: patient,
        returnUrl: returnUrl
]) }