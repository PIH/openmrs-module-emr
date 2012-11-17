<%
    emrContext.requireAuthentication()

	ui.includeFragment("emr", "standardEmrIncludes")

	def title = config.title ?: ui.message("emr.title")
%>

<div id="body-wrapper">

    <div id="header">
        <div id="user-info">
            <% if (context.authenticated) { %>
                ${ context.authenticatedUser.username ?: context.authenticatedUser.systemId }:
                ${ ui.format(emrContext.sessionLocation) }
                <a href="/${ contextPath }/logout">Logout</a>
            <% } %>
        </div>

        <a href="${ ui.pageLink("mirebalais", "home") }">
            <img src="${ ui.resourceLink("mirebalais", "images/pih_grey_logo_small.png") }"/>
        </a>
    </div>

    ${ ui.includeFragment("emr", "infoAndErrorMessage") }

    <div id="content" class="container">
        <%= config.content %>
    </div>

</div>