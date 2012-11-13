<%
    ui.decorateWith("emr", "standardEmrPage")
%>

<h3>${ ui.message("emr.systemAdministration") }</h3>

<a href="/${ contextPath }/emr/manageAccounts.page">${ ui.message("emr.manageAccounts") }</a>

<a href="/${ contextPath }/emr/printer/managePrinters.page">${ ui.message("emr.printer.managePrinters") }</a>