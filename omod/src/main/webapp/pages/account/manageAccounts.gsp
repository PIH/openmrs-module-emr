<%
    ui.decorateWith("emr", "standardEmrPage")
    ui.includeCss("emr", "account.css")
%>

<h3>${  ui.message("emr.task.accountManagement.label") }</h3>

<a href="${ ui.pageLink("emr", "account/account") }">
    <button id="create-account-button">${ ui.message("emr.createAccount") }</button>
</a>

<table id="list-accounts" width="50%" border="1" cellspacing="0" cellpadding="2">
	<thead>
		<tr>
			<th>Name</th>
			<th>Login</th>
		</tr>
	</thead>
	<tbody>
		<% accounts.each{  %>
	 	<tr>
	 		<td>
	 			<a href="/${ contextPath }/emr/account/account.page?personId=${ it.person.personId }">
					${ ui.format(it.person.personName)}
				</a>
			</td>
			<td>
				<% if(it.username && it.username != '') {%>
					${ ui.format(it.username) }
				<% } %>
			</td>
		</tr>
		<% } %>
	</tbody>
</table>