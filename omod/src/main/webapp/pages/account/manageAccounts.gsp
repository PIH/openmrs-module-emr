<%
    ui.decorateWith("emr", "standardEmrPage")
%>

<h3>${  ui.message("emr.task.accountManagement.label") }</h3>

<a href="${ ui.pageLink("emr", "account/account") }">
    <button id="create-account-button">${ ui.message("emr.createAccount") }</button>
</a>

<ul>
	<% accounts.each{  %>
 	<li>
		<a href="/${ contextPath }/emr/account/account.page?personId=${ it.person.personId }">
		 	${ ui.format(it.person.personName) }
			<% if(it.username && it.username != '') {%>
			(${ ui.format(it.username) })
			<% } %>
		 </a>
	</li>
	<% } %>
</ul>