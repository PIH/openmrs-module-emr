<%
    ui.decorateWith("emr", "standardEmrPage", [ title: ui.message("emr.myAccount") ])

    ui.includeCss("mirebalais", "app.css")
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("emr.app.system.administration.myAccount.label")}" }
    ];
</script>

<div id="tasks">
    <a class="button big" href="${ ui.pageLink("emr", "account/changePassword") }">
        <div class="task">
            <i class="icon-book"></i>
            ${ ui.message("emr.task.myAccount.changePassword.label") }
        </div>
    </a>
</div>
