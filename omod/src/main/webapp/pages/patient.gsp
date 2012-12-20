<%
	ui.decorateWith("emr", "standardEmrPage")
%>

<style type="text/css">
    #visits ul {
        margin-left: 2.5em;
    }
</style>

${ ui.includeFragment("emr", "patientHeader", [ patient: patient.patient ]) }

Actions:
<ul>
    <% availableTasks.each {
        def url = it.getUrl(emrContext)
        if (!url.startsWith("/")) {
            url = "/" + contextPath + "/" + url
        }
    %>
        <li><a href="${ url }">${ it.getLabel(emrContext) }</a></li>
    <% } %>
</ul>

<br/>

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

<br/>

Visits and Encounters:
<ul id="visits">
    <% patient.allVisits.each { v -> %>
        <li>
            ${ ui.format(v.visitType) } at ${ ui.format(v.location) } from ${ ui.format(v.startDatetime) } to ${ ui.format(v.stopDatetime) }
            <ul>
                <% v.encounters.findAll { !it.voided } .each { %>
                    <li><a href="${ ui.pageLink("emr", "encounter", [ patientId: patient.patient.id, encounterId: it.id ]) }">${ ui.format(it.encounterType) } at ${ ui.format(it.encounterDatetime) }</a></li>
                <% } %>
            </ul>
        </li>
    <% } %>
</ul>