<%
	ui.includeFragment("emr", "standardEmrIncludes")

	def title = config.title ?: ui.message("emr.title")
%>

<script type="text/javascript">
	var jq = jQuery;
    _.templateSettings = {
        interpolate : /\\{\\{=(.+?)\\}\\}/g ,
        escape : /\\{\\{-(.+?)\\}\\}/g ,
        evaluate : /\\{\\{(.+?)\\}\\}/g
    };
</script>

<div id="application-header" class="container">
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

<div id="content" class="container">
	<%= config.content %>
</div>