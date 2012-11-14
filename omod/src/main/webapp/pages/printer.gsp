<%
    ui.decorateWith("emr", "standardEmrPage")
%>

<h3>${ ui.message("emr.printer.edit") }</h3>

<form method="post">

    <input type="hidden" name="printerId" value="${ printer.id ?: ''}"

    <fieldset>
        ${ ui.includeFragment("emr", "field/text", [ label: ui.message("emr.printer.name"), formFieldName: "name", initialValue: (printer.name ?: '') ])} <br/>
        ${ ui.includeFragment("emr", "field/text", [ label: ui.message("emr.printer.ipAddress"), formFieldName: "ipAddress", initialValue: (printer.ipAddress ?: ''), size: 10 ])} <br/>
        ${ ui.includeFragment("emr", "field/text", [ label: ui.message("emr.printer.port"), formFieldName: "port",initialValue: (printer.port ?: ''), size: 10 ])} <br/>
    </fieldset>

    <input type="submit" value="${ ui.message("general.save") }" /> &nbsp;&nbsp;&nbsp;
    <input type="button" value="${ ui.message("general.cancel") }" onclick="javascript:window.location='/${ contextPath }/emr/managePrinters.page'" />

</form>