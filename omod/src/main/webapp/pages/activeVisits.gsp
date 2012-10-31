<%
    ui.decorateWith("emr", "standardEmrPage")
%>
<table>
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
				<td>${ ui.format(v.patient.identifier) }</td>
				<td>${ ui.format(v.patient) }</td>
				<td>
					${ ui.format(checkIn.location) }
					${ ui.format(checkIn.encounterDatetime) }
				</td>
				<td>
					${ ui.format(latest.location) }
					${ ui.format(latest.encounterDatetime)}
					${ ui.format(latest.encounterType)}
				</td>
			</tr>
		<% } %>
	</tbody>
</table>