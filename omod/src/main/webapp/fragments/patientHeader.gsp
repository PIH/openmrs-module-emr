<%
    def patient = config.patient

    ui.includeCss("emr", "patientHeader.css")
%>


<script type="text/javascript">
    jq(document).ready(function(){
        jq(".demographics .name").click(function(){
            emr.navigateTo({
                provider: 'emr',
                page: 'patient',
                query: { patientId: ${patient.patient.id} }
            });
        })
    })
</script>

<div class="patient-header">

    <div class="demographics">
        <h1 class="name">
            <span>${ ui.format(patient.patient.familyName) },<em>${ui.message("emr.patientHeader.surname")}</em></span>
            <span>${ ui.format(patient.patient.givenName) }<em>${ui.message("emr.patientHeader.name")}</em></span>
        </h1>
        <div class="gender-age">
            <span>${ ui.message("emr.gender." + patient.gender) }</span>

            <% if (patient.age) { %>
            <span>${ ui.message("emr.ageYears", patient.age) }</span>
            <% } else { %>
            <span>${ ui.message("emr.unknownAge") }</span>
            <% } %>
        </div>
        <% if (emrContext.activeVisitSummary) { %>
        <div class="visit-status">
            <% def visit = emrContext.activeVisitSummary.visit %>
            <span class="status active"></span>
            ${ ui.message("emr.activeVisit", ui.format(visit.startDatetime), ui.format(visit.location)) }
        </div>
        <% } %>
    </div>


    <div class="identifiers">
        <em>${ui.message("emr.patientHeader.patientId")}</em>
        <% patient.primaryIdentifiers.each{ %>
            <span>${it.identifier}</span>
        <% } %>
        <% if (patient.paperRecordIdentifier) { %>
            <br />
            <em>${ ui.format(emrProperties.paperRecordIdentifierType) }</em>
            <span>${patient.paperRecordIdentifier.identifier }</span>
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

    <div class="close"></div>
</div>