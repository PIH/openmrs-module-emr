<%
	ui.decorateWith("emr", "standardEmrPage")

    ui.includeCss("emr", "patientDashboard.css")

    def tabs = [
        [ id: "visits", label: ui.message("emr.patientDashBoard.visits") ],
        [ id: "contactInfo", label: ui.message("emr.patientDashBoard.contactinfo") ]
    ]

%>

${ ui.includeFragment("emr", "patientHeader", [ patient: patient.patient ]) }

<div class="dashboard-container">
    <aside class="menu">
        <ul class="options">
            <% tabs.each { %>
                <li <% if (it.id == selectedTab) { %> class="selected" <% } %> >
                    <a href="${ ui.pageLink("emr", "patient", [ patientId: patient.patient.id, tab: it.id ]) }">
                        ${ it.label }
                    </a>
                </li>
            <% } %>
        </ul>
        <div class="actions">
            <strong>Actions</strong>
            <% availableTasks.each { %>
            <div><a class="button" href="/${ contextPath }/${ it.getUrl(emrContext) }">${ it.getLabel(emrContext) }</a></div>
            <% } %>
        </div>
    </aside>


    <div class="dashboard">

        ${ ui.includeFragment("emr", "patientdashboard/" + selectedTab) }

    </div>
</div>

