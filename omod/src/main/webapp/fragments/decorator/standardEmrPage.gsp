<%
    emrContext.requireAuthentication()

	ui.includeFragment("emr", "standardEmrIncludes")

	def title = config.title ?: ui.message("emr.title")
%>

<div id="body-wrapper">

    <div id="header">
        <div id="user-info">
            <% if (context.authenticated) { %>
                <span class="header-menu"> ${ context.authenticatedUser.username ?: context.authenticatedUser.systemId }: ${ ui.format(emrContext.sessionLocation) } </span>
                <span class="header-menu location">Change Location</span>
                <span class="header-menu"><a href="/${ contextPath }/logout">Logout</a></span>
            <% } %>
        </div>

        <a href="${ ui.pageLink("mirebalais", "home") }">
            <img src="${ ui.resourceLink("mirebalais", "images/pih_grey_logo_small.png") }"/>
        </a>
    </div>
    <div id="sessionLocation" style="display: none;">
        <span class="locationOption" value="24">Antepartum ward</span>

        <span class="locationOption" value="8">Community Health</span>

        <span class="locationOption" value="18">Dental</span>

        <span class="locationOption" value="9">Emergency</span>

        <span class="locationOption" value="17">ICU</span>

        <span class="locationOption selected" value="16">Isolation</span>

        <span class="locationOption" value="22">Labor and Delivery</span>

        <span class="locationOption" value="3">Main laboratory</span>

        <span class="locationOption" value="19">Men’s Internal Medicine A</span>

        <span class="locationOption" value="20">Men’s Internal Medicine B</span>

        <span class="locationOption" value="12">NICU</span>

        <span class="locationOption" value="14">Operating Rooms</span>

        <span class="locationOption" value="13">Outpatient clinic</span>

        <span class="locationOption" value="10">Pediatrics A</span>

        <span class="locationOption" value="21">Pediatrics B</span>

        <span class="locationOption" value="5">Post-op GYN</span>

        <span class="locationOption" value="4">Postpartum ward</span>

        <span class="locationOption" value="11">Pre-op/PACU</span>

        <span class="locationOption" value="7">Radiology</span>

        <span class="locationOption" value="25">Surgical Ward</span>

        <span class="locationOption" value="6">Womens clinic</span>

        <span class="locationOption" value="26">Women’s Internal Medicine A</span>

        <span class="locationOption" value="15">Women’s Internal Medicine B</span>

        <span class="locationOption" value="23">Women’s Outpatient Laboratory</span>

        <span class="locationOption" value="27">Women’s Triage</span>


    </div>
    ${ ui.includeFragment("emr", "infoAndErrorMessage") }

    <div id="content" class="container">
        <%= config.content %>
    </div>

</div>