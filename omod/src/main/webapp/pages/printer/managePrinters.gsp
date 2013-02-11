<%
    ui.decorateWith("emr", "standardEmrPage")
%>
<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("emr.app.systemAdministration.label")}", link: '${ui.pageLink("emr", "systemAdministration")}' },
        { label: "${ ui.message("emr.printer.managePrinters")}" }
    ];
</script>
<h3>${  ui.message("emr.printer.managePrinters") }</h3>
<div>
    <a href="/${ contextPath }/emr/printer/printer.page" class="button">${ ui.message("emr.printer.add") }</a>
</div>
<br/>
<table>
    <tr>
        <th>${ ui.message("emr.printer.type") }</th>
        <th>${ ui.message("emr.printer.physicalLocation") }</th>
        <th>${ ui.message("emr.printer.name") }</th>
        <th>${ ui.message("emr.printer.ipAddress") }</th>
        <th>${ ui.message("emr.printer.port") }</th>
        <th>&nbsp;</th>
    </tr>

    <% if (!printers) { %>
        <tr><td colspan="6">${ ui.message("emr.none") }</td></tr>
    <% } %>
    <% printers.sort { it.name }.each {   %>
    <tr>
        <td>
            ${ ui.message("emr.printer." + it.type) }
        </td>
        <td>
            ${ ui.format(it.physicalLocation) ?: '&nbsp;'}
        </td>
        <td>
            ${ ui.format(it.name) }
        </td>
        <td>
            ${ ui.format(it.ipAddress) }
        </td>
        <td>
            ${ ui.format(it.port) }
        </td>
        <td>
            <a href="/${ contextPath }/emr/printer/printer.page?printerId=${ it.printerId }">
                <button>${ ui.message("emr.edit") }</button>
            </a>
        </td>
    </tr>
    <% } %>
</table>