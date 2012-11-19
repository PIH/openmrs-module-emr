<%
    ui.decorateWith("emr", "standardEmrPage", [ title: ui.message("emr.mergePatients") ])
    ui.includeCss("mirebalais", "mergePatients.css")

    def formatEncounter = {
        "${ ui.format(it.encounterType) }<br/>at ${ ui.format(it.location) }<br/>on ${ ui.format(it.encounterDatetime) }"
    }

    def displayList = { title, leftList, rightList, formatter ->
        def ret = """
            <tr>
            <td class="first-patient">
                <div class="section-heading">${ title }</div>
                <div class="section-content">
        """
        if (!leftList) {
            ret += ui.message("emr.none")
        }
        leftList.each {
            ret += (formatter != null ? formatter(it) : ui.format(it)) + "<br/>";
        }
        ret += """
                </div>
            </td>
            <td class="spacer"></td>
            <td class="second-patient">
                <div class="section-heading">${ title }</div>
                <div class="section-content">
                    """
        if (!rightList) {
            ret += ui.message("emr.none")
        }
        rightList.each {
            ret += (formatter != null ? formatter(it) : ui.format(it)) + "<br/>";
        }
        ret += """
                </div>
            </td>
        </tr>
                    """
    }
%>

<script type="text/javascript">
    jq(function() {
        jq('#cancel-button').click(function() {
            emr.navigateTo({ page: 'systemAdministration' });
        });
    });
</script>

<form method="get">
    <input type="hidden" name="patient1" value="${ patient1.patient.id }" />
    <input type="hidden" name="patient2" value="${ patient2.patient.id }" />
    <input type="hidden" name="confirmed" value="true"/>

    <h3>Are these duplicate records for the same person?</h3>
    <em>Please make sure--merging cannot be undone.</em>

    <table>
        <%= displayList("Name(s)", patient1.patient.names, patient2.patient.names, null) %>
        <%= displayList("Gender", [ patient1.patient.gender ], [ patient2.patient.gender ], null) %>
        <%= displayList("Identifier(s)", patient1.primaryIdentifiers.collect{ it.identifier }, patient2.primaryIdentifiers.collect{ it.identifier }, null) %>
        <%= displayList("Paper Record Identifier(s)", patient1.paperRecordIdentifiers.collect{ it.identifier }, patient2.paperRecordIdentifiers.collect{ it.identifier }, null) %>
        <%= displayList("Other ID(s)", ["TODO"], ["TODO"], null) %>
        <%= displayList("Address(es)", patient1.patient.addresses, patient2.patient.addresses, null) %>
        <%= displayList("Last Seen", [ patient1.lastEncounter ], [ patient2.lastEncounter ], formatEncounter) %>
        <%= displayList("Active Visit", [ patient1.getActiveVisit(emrContext.sessionLocation) ], [ patient2.getActiveVisit(emrContext.sessionLocation) ], null) %>
        <%= displayList("Total Data", [ patient1 ], [ patient2 ], { "${ it.countOfVisits} visit(s), ${ it.countOfEncounters} encounter(s)" }) %>
    </table>

    <br/>

    <input type="button" id="cancel-button" class="button secondary" value="${ ui.message("emr.cancel") }"/>

    <input type="submit" class="button primary" value="${ ui.message("emr.continue") }"/>
</form>