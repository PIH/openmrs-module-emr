<%
	ui.decorateWith("emr", "standardEmrPage")
%>

${ ui.includeFragment("emr", "patientHeader", [ patient: patient ]) }

Actions:
<ul>
    <li>
        <a href="${ ui.pageLink("emr", "orderXray", [ patientId: patient.id ]) }">Order XRay</a>
    </li>
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