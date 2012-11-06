<%
    ui.decorateWith("emr", "standardEmrPage")
%>

<h3>${ ui.message("emr.systemAdministration") }</h3>

${ ui.includeFragment("emr", "availableTasks") }
