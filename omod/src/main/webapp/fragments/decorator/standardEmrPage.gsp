<%
    emrContext.requireAuthentication()

	ui.includeFragment("emr", "standardEmrIncludes")

	def title = config.title ?: ui.message("emr.title")
%>

${ ui.includeFragment("emr", "header") }


<div id="body-wrapper">

    <script type="text/javascript">

        function Location(id, text){
            this.id = id;
            this.text = text;
        }

        function updateBindings(id, text) {
            var data = "locationId=" + id;

            jq("#spinner").show();

            jq.post("/${ contextPath }/mirebalais/standard.page", data, function(returnedData) {
                ko.applyBindings(new Location(id, text), jq('.change-location').get(0));
                jq('#session-location li').removeClass('selected');
                jq('#session-location li[value|=' + id + ']').addClass('selected');
                jq("#spinner").hide();
            })
        }

        jq(function() {
            jq(".change-location a").click(function() {
                jq('#session-location').show();
                jq(this).addClass('focus');
                jq(".change-location a i:nth-child(3)").removeClass("icon-caret-down");
                jq(".change-location a i:nth-child(3)").addClass("icon-caret-up");
            });

            jq('#session-location').mouseleave(function() {
                jq('#session-location').hide();
                jq(".change-location a").removeClass('focus');
                jq(".change-location a i:nth-child(3)").addClass("icon-caret-down");
                jq(".change-location a i:nth-child(3)").removeClass("icon-caret-up");
            });
        });

    </script>

    ${ ui.includeFragment("emr", "infoAndErrorMessage") }

    <div id="content" class="container">
        <%= config.content %>
    </div>

</div>