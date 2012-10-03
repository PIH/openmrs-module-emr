<div style="background-color: #e0e0e0">
    <h1>${ ui.format(patient) }</h1>

    <img src="${ ui.resourceLink("emr", "images/patient_" + patient.gender + ".gif") }"/>
    ${ ui.format("emr.gender." + patient.gender) }

    Identifiers:
    <ul>
        <%  patient.identifiers.each { %>
        <li>${ ui.format(it)  }</li>
        <% } %>
    </ul>
</div>