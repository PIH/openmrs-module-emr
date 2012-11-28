<%
    emrContext.requireAuthentication()

	ui.includeFragment("emr", "standardEmrIncludes")

	def title = config.title ?: ui.message("emr.title")
%>

<div id="body-wrapper">

    <div id="header">
        <div id="user-info">
            <% if (context.authenticated) { %>
                <span class="header-menu"> ${ context.authenticatedUser.username ?: context.authenticatedUser.systemId }:<strong data-bind="text: text"> ${ ui.format(emrContext.sessionLocation) } </strong></span>
                <span class="header-menu location" onmouseover="javascript:showLocationDiv();">Change Location</span>
                <span class="header-menu"><a href="/${ contextPath }/logout">Logout</a></span>
            <% } %>
        </div>

        <a href="${ ui.pageLink("mirebalais", "home") }">
            <img src="${ ui.resourceLink("mirebalais", "images/pih_grey_logo_small.png") }"/>
        </a>
    </div>


    <div id="sessionLocation" style="display: none;">
        <% emrProperties.allAvailableLocations.each {
            def selected = (it==emrContext.sessionLocation) ? "selected" : ""
            %>
            <span class="locationOption ${selected}" value="${it.id}" onclick="updateBindings(${it.id}, '${ui.format(it)}');">${ui.format(it)}</span>
        <% } %>
    </div>


    <script type="text/javascript">

        function Location(id, text){
            this.id = id;
            this.text = text;
        }

        function updateBindings(id, text) {
            ko.applyBindings(new Location(id, text));

            var data = "locationId=" + id;
            jq.post("/mirebalais/mirebalais/standard.page", data, function(returnedData) {
                console.log("success");
            })
        }

        jq('#sessionLocation').mouseleave(function() {
            jq('#sessionLocation').attr('style','display: none');
        });

        function showLocationDiv() {
            jq('#sessionLocation').attr('style', '')
        }

    </script>

    ${ ui.includeFragment("emr", "infoAndErrorMessage") }

    <div id="content" class="container">
        <%= config.content %>
    </div>

</div>