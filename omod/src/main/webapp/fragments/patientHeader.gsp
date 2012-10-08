<style class="text/css">
    .patient-header {
        border-bottom: 1px gray solid;
    }

    .patient-header .icon , .patient-header .demographics , .patient-header .identifiers {
        float: left;
        margin-right: 2em;
    }

    .patient-header .demographics , .patient-header .identifiers {
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
        <span class="name">${ ui.format(patient) }</span>
        <br/>
        ${ ui.message("emr.gender." + patient.gender) }
        ${ patient.age } year(s)
    </div>

    <div class="identifiers">
        <% patient.activeIdentifiers.each { %>
            ${ ui.format(it) } <br/>
        <% } %>
    </div>

    <div class="close"></div>
</div>