<%
    ui.decorateWith("emr", "standardEmrPage")
%>

<h3>${  ui.message("emr.manageAccounts") }</h3>

<ul>
	<% accounts.each{  %>
 	<li>
		<a href="/${ contextPath }/emr/account.page?personId=${ it.person.personId }">
		 	${ ui.format(it.person.personName) }
			<% if(it.username && it.username != '') {%>
			(${ ui.format(it.username) })
			<% } %>
		 </a>
	</li>
	<% } %>
</ul>