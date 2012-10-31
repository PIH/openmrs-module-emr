<%
    ui.decorateWith("emr", "standardEmrPage")
%>

<h3>${  ui.message("emr.manageUsers") }</h3>

<ul>
	<% users.each{  %>
 	<li><a href="/${ contextPath }/emr/user.page?userId=${ it.userId }">${ ui.format(it) }</a></li>
	<% } %>
	
	<% providers.each{  %>
 	<li><a href="/${ contextPath }/emr/user.page?providerId=${ it.providerId }">${ ui.format(it) }</a></li>
	<% } %>
</ul>