<%
	ui.decorateWith("emr", "standardEmrPage")

    def dateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy")

    def timeFormat = new java.text.SimpleDateFormat("HH:mm")

    ui.includeCss("emr", "patientDashboard.css")

%>

${ ui.includeFragment("emr", "patientHeader", [ patient: patient.patient ]) }

<div class="dashboard-container">
    <aside class="menu">
        <ul class="options">
            <li> <a href="/${ contextPath }/emr/patient.page?patientId=${ patient.patient.patientId }">${ui.message("emr.patientDashBoard.visits")}</a> </li>
        </ul>
        <div class="actions">
            <strong>Actions</strong>
            <% availableTasks.each { %>
            <div><a class="button" href="/${ contextPath }/${ it.getUrl(emrContext) }">${ it.getLabel(emrContext) }</a></div>
            <% } %>
        </div>
    </aside>


    <div class="dashboard">
    <h3>${ui.message("emr.patientDashBoard.visits")}</h3>

        <table>
            <thead>
                <tr>
                    <th>${ui.message("emr.patientDashBoard.date")}</th>
                    <th>${ui.message("emr.patientDashBoard.startTime")}</th>
                    <th>${ui.message("emr.patientDashBoard.location")}</th>
                </tr>
            </thead>
            <% patient.allVisitsUsingWrappers.each { wrapper -> %>
                <tr>
                    <td>${dateFormat.format(wrapper.visit.startDatetime)} <br>(${wrapper.differenceInDaysBetweenCurrentDateAndStartDate} days ago) </td>
                    <td>${timeFormat.format(wrapper.visit.startDatetime)}</td>
                    <td>${ ui.format(wrapper.visit.location) }</td>
                </tr>
            <% } %>
        </table>



    </div>
</div>