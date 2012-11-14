<%
    ui.decorateWith("emr", "standardEmrPage")
%>

<h3>${ ui.message("emr.systemAdministration") }</h3>

<ul>
    <li><a href="/${ contextPath }/emr/manageAccounts.page">${ ui.message("emr.manageAccounts") }</a></li>
    <li><a href="/${ contextPath }/emr/managePrinters.page">${ ui.message("emr.printer.managePrinters") }</a></li>
</ul>


