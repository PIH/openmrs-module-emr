<h3>${ui.message("emr.patientDashBoard.contactinfo")}</h3>

<div class="contact-info">
    <ul>
        <li><strong>${ ui.message("emr.person.address")}: </strong> ${patient.personAddress['address2']},
        <% addressHierarchyLevels.each { addressLevel -> %>
            <% if (addressLevel != 'address2'){ %>
                ${patient.personAddress[addressLevel]}
                <% if(addressLevel != addressHierarchyLevels.last()){%>,
                <% }%>

            <% }%>
        <% } %>
        </li>
        <li><strong>${ ui.message("emr.person.telephoneNumber")}:</strong> ${patient.telephoneNumber}</li>
    </ul>
</div>