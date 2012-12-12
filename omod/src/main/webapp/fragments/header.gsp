<header>
    <div class="logo">
        <a href="${ ui.pageLink("mirebalais", "home") }">
            <img src="${ ui.resourceLink("mirebalais", "images/partners_in_health_logo.png") }"/>
        </a>
    </div>
    <% if (context.authenticated) { %>
    <ul class="user-options">
        <li class="identifier">
            <i class="icon-user small"></i>
            ${ context.authenticatedUser.username ?: context.authenticatedUser.systemId }
        </li>
        <li class="change-location">
            <a href="#">
                <i class="icon-map-marker small"></i>
                <span data-bind="text: text">${ ui.format(emrContext.sessionLocation) }</span>
                <i class="icon-caret-down link"></i>
            </a>
        </li>
        <li class="logout">
            <a href="/${ contextPath }/logout">
                ${ ui.message("emr.logout") }
                <i class="icon-signout small"></i>
            </a>
        </li>
    </ul>
    <div id="session-location">
        <!--div id="spinner">
                <img src="${ui.resourceLink("mirebalais", "images/spinner.gif")}">
            </div-->
        <ul class="select">
            <% emrProperties.allAvailableLocations.each {
                def selected = (it==emrContext.sessionLocation) ? "selected" : ""
            %>
            <li class="${selected}" value="${it.id}" onclick="updateBindings(${it.id}, '${ui.format(it)}');">${ui.format(it)}</li>
            <% } %>
        </ul>
    </div>
    <% } %>
</header>