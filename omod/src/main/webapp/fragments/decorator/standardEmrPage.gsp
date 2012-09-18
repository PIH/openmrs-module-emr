<%
	ui.includeFragment("emr", "standardEmrIncludes")
	ui.includeCss("emr", "emr.css")
	
	def title = config.title ?: "OpenMRS Electronic Medical Record"
%>

<div id="application-header">
	${ title }
	<% if (context.authenticated) { %>
		<span style="float: right">
			${ context.authenticatedUser.personName }
			|
			<a href="/${ contextPath }/logout">Log Out</a>
		</span>
	<% } %>
</div>

<div id="content">
	<%= config.content %>
</div>