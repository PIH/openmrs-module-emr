<%
    def patient = config.patient

    ui.includeCss("emr", "patientHeader.css")
%>


<div class="patient-header">

    <div class="demographics">
        <div class="surname"><span class="name">${ ui.format(patient.patient.familyName) } ,</span> <br/> <span class="legend">surname</span> </div>
        <div class="givenName"><span class="name">${ ui.format(patient.patient.givenName) }</span> <br/> <span class="legend">name</span></div>
    </div>

    <div class="gender">
        <span>${ ui.message("emr.gender." + patient.gender) } |
        <% if (patient.age) { %>
        ${ ui.message("emr.ageYears", patient.age) }
        <% } else { %>
        ${ ui.message("emr.unknownAge") }
        <% } %>
        </span>
    </div>

    <div class="identifiers">
        ID:
        <%= patient.primaryIdentifiers.collect{ it.identifier }.join(", ") %>
        <% if (patient.paperRecordIdentifiers) { %>
            <br/>
            ${ ui.format(emrProperties.paperRecordIdentifierType) }:
            <%= patient.paperRecordIdentifiers.collect{ it.identifier }.join(", ") %>
        <% } %>
    </div>

    <div class="unknown-patient" style=<%= (!patient.unknownPatient) ? "display:none" : "" %>>
        ${ ui.message("emr.patient.temporaryRecord") } <br/>
        <form action="/${ contextPath }/emr/mergePatients.page" method="get">
            <input type="hidden" name="isUnknownPatient" value="true" />
            <input type="hidden" name="patient1" value="${patient.patient.id}" />
            <input type="submit" id="merge-button" value="${ ui.message("emr.mergePatients.mergeIntoAnotherPatientRecord.button") }" />
        </form>
    </div>

    <% if (emrContext.activeVisitSummary) { %>
        <div class="active-visit">
            <% def visit = emrContext.activeVisitSummary.visit %>
            ${ ui.message("emr.activeVisit", ui.format(visit.startDatetime), ui.format(visit.location)) }
        </div>
    <% } %>

    <div class="close"></div>
</div>