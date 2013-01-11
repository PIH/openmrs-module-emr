<%
    def dateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy")
    def timeFormat = new java.text.SimpleDateFormat("HH:mm")
%>

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