<%
	ui.includeFragment("emr", "standardEmrIncludes")
	ui.includeCss("emr", "emr.css")
	
	def title = config.title ?: ui.message("emr.title")
%>

<script type="text/javascript">
	var jq = jQuery;
</script>

<div id="application-header">
	<a href="/${ contextPath }/index.htm">
		${ title }
	</a>
	<% if (context.authenticated) { %>
		<span style="float: right">
			${ ui.includeFragment("emr", "loginInfo") }
			|
			<a href="/${ contextPath }/logout">Log Out</a>
		</span>
	<% } %>
</div>

<div id="content">
	<%= config.content %>
</div>