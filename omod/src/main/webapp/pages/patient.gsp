<%
	ui.decorateWith("emr", "standardEmrPage")
%>

${ ui.includeFragment("emr", "patientHeader", [ patient: patient ]) }

Actions:
<ul>
    <% availableTasks.each { %>
        <li><a href="/${ contextPath }/${ it.getUrl(emrContext) }">${ it.getLabel(emrContext) }</a></li>
    <% } %>
</ul>

Orders:
<ul>
<% if (!orders) { %>
    <li>None</li>
<% } %>
<% orders.each { %>
    <li>
        <b>${ ui.format(it.concept) }</b>
        Ordered by ${ ui.format(it.creator) }
        on ${ ui.format(it.dateCreated) }
    </li>
<% } %>
</ul>