<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.format(patient.patient.familyName) }, ${ ui.format(patient.patient.givenName) }" },
        { label: "${ui.message("emr.patientDashBoard.visits")}" , link:'${ui.pageLink("emr", "patient", [patientId: patient.id])}'}
    ];
</script>

<h3>${ui.message("emr.patientDashBoard.contactinfo")}</h3>

<div class="contact-info">
    <ul>
        <li><strong>${ ui.message("emr.person.address")}: </strong>
        <% addressHierarchyLevels.each { addressLevel -> %>
           <% if(patient.personAddress!=null) { %>
                 ${patient.personAddress[addressLevel]}
                <% if(addressLevel != addressHierarchyLevels.last()){%>,
                <% }%>
            <% }%>
        <% } %>
        </li>
        <li><strong>${ ui.message("emr.person.telephoneNumber")}:</strong> ${patient.telephoneNumber ?: ''}</li>
    </ul>
</div>