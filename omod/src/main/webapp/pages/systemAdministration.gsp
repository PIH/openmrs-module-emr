<%
    ui.decorateWith("emr", "standardEmrPage", [ title: ui.message("emr.systemAdministration") ])
%>

<ul>
    <li><a href="${ ui.pageLink("emr", "manageAccounts") }">${ ui.message("emr.manageAccounts") }</a></li>
    <li><a href="${ ui.pageLink("emr", "managePrinters") }">${ ui.message("emr.printer.managePrinters") }</a></li>
    <li><a href="${ ui.pageLink("emr", "mergePatients") }">${ ui.message("emr.mergePatients") }</a></li>
</ul>
