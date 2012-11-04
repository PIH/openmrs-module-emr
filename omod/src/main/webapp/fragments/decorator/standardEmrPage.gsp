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
            ${ ui.message("emr.emrContext.sessionLocation", ui.format(emrContext.sessionLocation)) }
            |
			<a id="logout-button" href="/${ contextPath }/logout">Log Out</a>
		</span>
	<% } %>
</div>

${ ui.includeFragment("emr", "infoAndErrorMessage") }

<div id="content" class="container">
	<%= config.content %>
</div>