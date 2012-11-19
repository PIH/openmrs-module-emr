<style class="text/css">
    .patient-header {
        border-bottom: 1px gray solid;
    }

    .patient-header .icon , .patient-header .demographics , .patient-header .identifiers, .active-visit {
        float: left;
        margin-right: 2em;
    }

    .patient-header .demographics , .patient-header .identifiers, .active-visit {
        padding-top: 1em;
    }

    .patient-header .name {
        font-weight: bold;
        font-size: 1.4em;
    }

    .patient-header .close {
        clear: left;
    }
</style>

<div class="patient-header">
    <img class="icon" src="${ ui.resourceLink("emr", "images/patient_" + patient.gender + ".gif") }"/>

    <div class="demographics">
        <span class="name">${ ui.format(patient.patient) }</span>
        <br/>
        ${ ui.message("emr.gender." + patient.gender) }
        <% if (patient.age) { %>
            ${ ui.message("emr.ageYears", patient.age) }
        <% } else { %>
            ${ ui.message("emr.unknownAge") }
        <% } %>
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

    <% if (emrContext.activeVisitSummary) { %>
        <div class="active-visit">
            <% def visit = emrContext.activeVisitSummary.visit %>
            ${ ui.message("emr.activeVisit", ui.format(visit.startDatetime), ui.format(visit.location)) }
        </div>
    <% } %>

    <div class="close"></div>
</div>