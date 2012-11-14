<%
    ui.decorateWith("emr", "standardEmrPage")
%>

<h3>${  ui.message("emr.printer.managePrinters") }</h3>

<ul>
    <% printers.each{  %>
    <li>
        <a href="/${ contextPath }/emr/printer.page?printerId=${ it.printerId }">
            ${ ui.format(it.name) }
        </a>
    </li>
    <% } %>
</ul>

<a href="/${ contextPath }/emr/printer.page"><button>${ ui.message("emr.printer.add") }</button></a>