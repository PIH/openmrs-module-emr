<%
	ui.decorateWith("emr", "standardEmrPage")

    def dateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy")

    def timeFormat = new java.text.SimpleDateFormat("HH:mm")

    ui.includeCss("emr", "patientDashboard.css")
    ui.includeJavascript("emr", "patientDashboard.js")

%>

${ ui.includeFragment("emr", "patientHeader", [ patient: patient.patient ]) }

<div class="dashboard-container">
    <aside class="menu">
        <ul class="options">
            <li class="selected"> <a id="visitsDivLink" href="#visitsDiv" onclick="jq.setupVisitsDiv();">${ui.message("emr.patientDashBoard.visits")}</a> </li>
            <li> <a id="contactInfoDivLink" href="#contactInfoDiv" onclick="jq.setupContactInfoDiv();">${ui.message("emr.patientDashBoard.contactinfo")}</a> </li>
        </ul>
        <div class="actions">
            <strong>Actions</strong>
            <% availableTasks.each { %>
            <div><a class="button" href="/${ contextPath }/${ it.getUrl(emrContext) }">${ it.getLabel(emrContext) }</a></div>
            <% } %>
        </div>
    </aside>


    <div class="dashboard">
        <div id="visitsDiv">
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

        <div id="contactInfoDiv">
            <h3>${ui.message("emr.patientDashBoard.contactinfo")}</h3>
           <ul>
               <li><strong>Address:</strong> Buenos Aires street 580 apt 201</li>
               <li><strong>Telephone:</strong> (11)40583103</li>
           </ul>
        </div>

    </div>
</div>

