<%
    ui.decorateWith("emr", "standardEmrPage")
%>

<h3>${  ui.message("emr.printer.managePrinters") }</h3>

<table class="bordered">
    <tr>
        <th>${ ui.message("emr.printer.type") }</th>
        <th>${ ui.message("emr.printer.physicalLocation") }</th>
        <th>${ ui.message("emr.printer.name") }</th>
        <th>${ ui.message("emr.printer.ipAddress") }</th>
        <th>${ ui.message("emr.printer.port") }</th>
        <th>&nbsp;</th>
    </tr>

    <% printers.each{  %>
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
                <button>${ ui.message("general.edit") }</button>
            </a>
        </td>
    </tr>
    <% } %>
</table>

<a href="/${ contextPath }/emr/printer/printer.page"><button>${ ui.message("emr.printer.add") }</button></a>