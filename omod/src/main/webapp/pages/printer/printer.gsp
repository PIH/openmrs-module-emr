<%
    ui.decorateWith("emr", "standardEmrPage")

    def typeOptions = []

     printerTypeOptions.each {
         typeOptions.push([ label: ui.message("emr.printer." + it), value:it ])
     }

    def locationOptions = []

    locations.each {
        locationOptions.push([ label: ui.format(it), value: it.id ])
    }
%>

<form class="standard-vertical-form" method="post" action="printer.page">

    <h3>${ ui.message("emr.printer.edit") }</h3>

    <fieldset>
        ${ ui.includeFragment("emr", "field/radioButtons", [ label: ui.message("emr.printer.type"), formFieldName: "type", initialValue: (printer.type ?: ''), options: typeOptions ])} <br/>
        ${ ui.includeFragment("emr", "field/dropDown", [ label: ui.message("emr.printer.physicalLocation"), formFieldName: "physicalLocation", initialValue: (printer.physicalLocation?.id ?: ''), options: locationOptions ])} <br/>
        ${ ui.includeFragment("emr", "field/text", [ label: ui.message("emr.printer.name"), formFieldName: "name", initialValue: (printer.name ?: '') ])} <br/>
        ${ ui.includeFragment("emr", "field/text", [ label: ui.message("emr.printer.ipAddress"), formFieldName: "ipAddress", initialValue: (printer.ipAddress ?: ''), size: 10 ])} <br/>
        ${ ui.includeFragment("emr", "field/text", [ label: ui.message("emr.printer.port"), formFieldName: "port",initialValue: (printer.port ?: ''), size: 10 ])} <br/>
    </fieldset>

    <input type="submit" value="${ ui.message("emr.save") }" /> &nbsp;&nbsp;&nbsp;
    <input type="button" value="${ ui.message("emr.cancel") }" onclick="javascript:window.location='${ ui.pageLink("emr", "printer/managePrinters") }'" />

    <input type="hidden" name="printerId" value="${ printer.id ?: ''}" />
    <input type="hidden" name="uuid" value="${ printer.uuid ?: ''}" />

</form>