<%
    def patient = config.patient

    ui.includeCss("emr", "patientHeader.css")
%>


<script type="text/javascript">
    jq(document).ready(function(){
        createEditPatientIdentifierDialog(${patient.id});

        jq(".editPatientIdentifier").click(function(event) {
            var identifierTypeId = jq(event.target).attr("data-identifier-type-id");
            var identifierTypeName = jq(event.target).attr("data-identifier-type-name");
            var patientIdentifierValue = jq(event.target).attr("data-patient-identifier-value");

            jq("#hiddenIdentifierTypeId").val(identifierTypeId);
            jq("#identifierTypeNameSpan").text(identifierTypeName);
            jq("#patientIdentifierValue").val(patientIdentifierValue);

            showEditPatientIdentifierDialog();
        });

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
            ${ ui.message("emr.activeVisit.time", ui.format(visit.startDatetime)) }
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
        <br />
        <% if (emrProperties.extraPatientIdentifierTypes) { %>
            <% emrProperties.extraPatientIdentifierTypes.each{ %>
                <em>${ ui.format(it) }</em>
                <% def extraPatientIdentifier = patient.patient.getPatientIdentifier(it)
                   if (extraPatientIdentifier) { %>
                        <span><a class="editPatientIdentifier" data-identifier-type-id="${ it.id }" data-identifier-type-name="${ it.name }" data-patient-identifier-value="${ extraPatientIdentifier }" href="#${ it.id }">${ extraPatientIdentifier }</a></span>
                <% }else{ %>
                        <span><a class="editPatientIdentifier" data-identifier-type-id="${ it.id }" data-identifier-type-name="${ it.name }" data-patient-identifier-value="" href="#${ it.id }">${ ui.message("emr.patient.identifier.add") }</a></span>
                <% } %>
            <% } %>
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
<div id="edit-patient-identifier-dialog" class="dialog" style="display: none">
    <div class="dialog-header">
        <h3>${ ui.message("emr.patientDashBoard.editPatientIdentifier.title") }</h3>
    </div>
    <div class="dialog-content">
        <input type="hidden" id="hiddenIdentifierTypeId" value=""/>
        <ul>
            <li class="info">
                <span>${ ui.message("emr.patient") }</span>
                <h5>${ ui.format(patient.patient) }</h5>
            </li>
            <li class="info">
                <span id="identifierTypeNameSpan"></span>
            </li>
            <li class="info">
                <input id="patientIdentifierValue" value=""/>
            </li>
        </ul>

        <button class="confirm right">${ ui.message("emr.confirm") }</button>
        <button class="cancel">${ ui.message("emr.cancel") }</button>
    </div>
</div>
