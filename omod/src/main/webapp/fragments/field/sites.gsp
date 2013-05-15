<%
    config.require("label")
    config.require("formFieldName")


    def options = []

    options[0]=  [ label: ui.message("disposition.emrapi.transferOutOfHospital.sites.zlSite"), value: ui.message("disposition.emrapi.transferOutOfHospital.sites.zlSite"), selected: false ]
    options[1]=  [ label: ui.message("disposition.emrapi.transferOutOfHospital.sites.nonZlSite"), value: ui.message("disposition.emrapi.transferOutOfHospital.sites.nonZlSite"), selected: false ]

%>

${ ui.includeFragment("emr", "field/dropDown", [
        id: config.id,
        label: ui.message(config.label),
        formFieldName: config.formFieldName,
        options: options
]) }