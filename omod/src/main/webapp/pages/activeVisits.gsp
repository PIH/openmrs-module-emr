<%
    ui.decorateWith("emr", "standardEmrPage")
%>

<h3>Active Visits</h3>

<table id="active-visits" width="100%" border="1" cellspacing="0" cellpadding="2">
	<thead>
		<tr>
			<th>ID</th>
			<th>Name</th>
			<th>Check-In</td>
			<th>Last Seen</th>
		</tr>
	</thead>
	<tbody>
		<% visitSummaries.each { v ->
			def checkIn = v.checkInEncounter
			def latest = v.lastEncounter
		%>
			<tr>
				<td>${ ui.format(v.visit.patient.patientIdentifier) }</td>
				<td>
                    <a href="${ ui.pageLink("emr", "patient", [ patientId: v.visit.patient.id ]) }">
                        ${ ui.format(v.visit.patient) }
                    </a>
                </td>
				<td>
                    <% if (checkIn) { %>
                        <small>
                            ${ ui.format(checkIn.location) } @ ${ ui.format(checkIn.encounterDatetime) }
                        </small>
                    <% } %>
				</td>
				<td>
                    <% if (latest) { %>
                        ${ ui.format(latest.encounterType) }
                        <br/>
                        <small>
                            ${ ui.format(latest.location) } @ ${ ui.format(latest.encounterDatetime) }
                        </small>

                    <% } %>
				</td>
			</tr>
		<% } %>
	</tbody>
</table>