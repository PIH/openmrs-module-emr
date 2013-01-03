<%
	ui.decorateWith("emr", "standardEmrPage")

    def dateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy")

    def timeFormat = new java.text.SimpleDateFormat("HH:mm")

%>

<style type="text/css">
    #visits ul {
        margin-left: 2.5em;
    }
</style>

${ ui.includeFragment("emr", "patientHeader", [ patient: patient.patient ]) }

Actions:
<ul>
    <% availableTasks.each { %>
        <li><a href="/${ contextPath }/${ it.getUrl(emrContext) }">${ it.getLabel(emrContext) }</a></li>
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

${ui.message("emr.patientDashBoard.visits")}

<table>
    <thead>
        <tr>
            <th>${ui.message("emr.patientDashBoard.date")}</th>
            <th>${ui.message("emr.patientDashBoard.startTime")}</th>
            <th>${ui.message("emr.patientDashBoard.location")}</th>
        </tr>
    </thead>
    <% patient.allVisitsUsingWrappers.each { wrapper -> %>
        <tr>
            <td>${dateFormat.format(wrapper.visit.startDatetime)} <br>(${wrapper.differenceInDaysBetweenCurrentDateAndStartDate} days ago) </td>
            <td>${timeFormat.format(wrapper.visit.startDatetime)}</td>
            <td>${ ui.format(wrapper.visit.location) }</td>
        </tr>
    <% } %>
</table>

