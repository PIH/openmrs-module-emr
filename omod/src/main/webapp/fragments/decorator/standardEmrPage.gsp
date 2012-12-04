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
                <a href="#" class="header-menu location">Change Location</a>
                <span class="header-menu"><a href="/${ contextPath }/logout">Logout</a></span>
            <% } %>
        </div>

        <a href="${ ui.pageLink("mirebalais", "home") }">
            <img src="${ ui.resourceLink("mirebalais", "images/pih_grey_logo_small.png") }"/>
        </a>
        <div id="sessionLocation" style="display: none;">
            <div id="spinner">
                <img src="${ui.resourceLink("mirebalais", "images/spinner.gif")}">
            </div>
            <% emrProperties.allAvailableLocations.each {
                def selected = (it==emrContext.sessionLocation) ? "selected" : ""
                %>
                <span class="locationOption ${selected}" value="${it.id}" onclick="updateBindings(${it.id}, '${ui.format(it)}');">${ui.format(it)}</span>
            <% } %>
        </div>
    </div>


    <script type="text/javascript">

        function Location(id, text){
            this.id = id;
            this.text = text;
        }

        function updateBindings(id, text) {
            var data = "locationId=" + id;

            jq("#spinner").show();

            jq.post("/${ contextPath }/mirebalais/standard.page", data, function(returnedData) {
                ko.applyBindings(new Location(id, text), jq('#user-info').get(0));
                jq('#sessionLocation .locationOption').removeClass('selected');
                jq('#sessionLocation .locationOption[value|=' + id + ']').addClass('selected');
                jq("#spinner").hide();
            })
        }

        jq(function() {
            jq("#user-info .location").click(function() {
                jq('#sessionLocation').show();
                jq(this).addClass('focus');
            });

            jq('#sessionLocation').mouseleave(function() {
                jq('#sessionLocation').hide();
                jq("#user-info .location").removeClass('focus');
            });
        });

    </script>

    ${ ui.includeFragment("emr", "infoAndErrorMessage") }

    <div id="content" class="container">
        <%= config.content %>
    </div>

</div>