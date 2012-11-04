<%
    ui.decorateWith("emr", "standardEmrPage")
%>

<h3>${  ui.message("emr.manageUsers") }</h3>

<ul>
	<% accounts.each{  %>
 	<li>
		<a href="/${ contextPath }/emr/user.page?personId=${ it.person.personId }">
		 	${ ui.format(it.person.personName) }
			<% if(it.username && it.username != '') {%>
			(${ ui.format(it.username) })
			<% } %>
		 </a>
	</li>
	<% } %>
</ul>