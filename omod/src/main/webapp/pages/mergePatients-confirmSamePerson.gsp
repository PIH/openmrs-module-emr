<%
    ui.decorateWith("emr", "standardEmrPage", [ title: ui.message("emr.mergePatients") ])
    ui.includeCss("mirebalais", "mergePatients.css")

    def highlightPreferred = {
        it.preferred ? "<b>${ ui.format(it) }</b>" : ui.format(it)
    }

    def formatIdentifiers = {
        it.preferred ? "<b>${ it.identifier }</b>" : it.identifier
    }

    def formatEncounter = {
        """"${ ui.format(it.encounterType) }<br/>
            ${ ui.message("emr.atLocation", ui.format(it.location)) }<br/>
            ${ ui.message("emr.onDatetime", ui.format(it.encounterDatetime)) }"""
    }

    def formatAddress = {
        ui.includeFragment("emr", "formatAddress", [ address: it ])
    }

    def formatDemographics = {
        def birthdate = it.birthdate ? (it.birthdateEstimated ? "~" : "" + ui.format(it.birthdate)) : "?"
        def age = it.age ?: "?"

        """${ ui.message("emr.gender." + it.gender) }${ it.age ? ", " + ui.message("emr.ageYears", it.age) : "" } <br/>
        ${ ui.message("emr.birthdate") }: ${ birthdate } """
    }

    def formatActiveVisit = {
        def activeVisit = it.getActiveVisit(emrContext.sessionLocation)
        activeVisit ? ui.format(activeVisit) : ui.message("emr.none")
    }

    def formatDataSummary = {
        """${ ui.message("emr.mergePatients.section.dataSummary.numVisits", it.countOfVisits) },
            ${ ui.message("emr.mergePatients.section.dataSummary.numEncounters", it.countOfEncounters) } """
    }

    def displayList = { title, leftList, spacerContent, rightList, formatter ->
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
            <td class="spacer">${spacerContent}</span></td>
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
    var patient1Id = ${patient1.patient.id};
    var patient2Id = ${patient2.patient.id};

    //this value come from the request (isUnknownPatient parameter)
    var isUnknownPatient = ${isUnknownPatient};

    jq(function() {
        jq('div').on('click','#first-patient', function() {

            if(!isUnknownPatient){

                if (!jq('#first-patient').hasClass("selected")){

                    jq('#second-patient').removeClass('selected');
                    jq('#first-patient').addClass('selected');

                    jq('#separator-left').removeClass('hidden');
                    jq('#separator-right').addClass('hidden');

                    jq('#confirm-button').removeAttr("disabled");
                    jq('#confirm-button').removeClass("disabled");
                    jq('#confirm-button').addClass("confirm");



                    jq('#preferred').val(patient1Id);
                }

            }
        });

        jq('div').on('click','#second-patient', function() {

            if(!isUnknownPatient){

                if (!jq('#second-patient').hasClass("selected")){
                    jq('#first-patient').removeClass('selected');

                    jq('#second-patient').addClass('selected');

                    jq('#separator-left').addClass('hidden');
                    jq('#separator-right').removeClass('hidden');

                    jq('#confirm-button').removeAttr("disabled");
                    jq('#confirm-button').removeClass("disabled");
                    jq('#confirm-button').addClass("confirm");

                    jq('#preferred').val(patient2Id);
                }

            }
        });

        jq('#cancel-button').click(function() {
            window.history.back();
        });

    });
</script>

<form method="post">
    <% def preferred = (isUnknownPatient ? patient2.patient.id : "") %>
    <input type="hidden" name="patient1" value="${ patient1.patient.id }" />
    <input type="hidden" name="patient2" value="${ patient2.patient.id }" />
    <input type="hidden" name="preferred" id="preferred"  value="${preferred}" />


    <div class="messages-container">
        <%  if (isUnknownPatient){ %>
            <h2>${ ui.message("emr.mergePatients.unknownPatient.message") }</h2>
        <% } else {%>
            <h2>${ ui.message("emr.mergePatients.confirmationQuestion") }
        <% } %>
            <em>${ ui.message("emr.mergePatients.choosePreferred.description") }</em>
        </h2>
    </div>

    <div id="patients">
        <div id="first-patient" class="patient">
            <div class="row name">
                <h3>${ui.message("emr.mergePatients.section.names")}</h3>
                <% patient1.patient.names.each { %>
                   <div>${it}</div>
                <% } %>
            </div>
            <div class="row">
                <h3>${ui.message("emr.mergePatients.section.demographics")}</h3>
                <div>${ ui.message("emr.gender." + patient1.patient.gender) }${ patient1.patient.age ? ", " + ui.message("emr.ageYears", patient1.patient.age) : "" }</div>
                <div> ${ ui.message("emr.birthdate") }:${ui.format(patient1.patient.birthdate)}</div>
            </div>
            <div class="row identifiers">
                <h3>${ui.message("emr.mergePatients.section.primaryIdentifiers")}</h3>
                <% patient1.primaryIdentifiers.each { %>
                    <% def identifier = (it.preferred ? "<b>${it.identifier}</b>" : it.identifier) %>
                    <div>${identifier}</div>
                <% } %>
            </div>
            <div class="row identifiers">
                <h3>${ui.message("emr.mergePatients.section.paperRecordIdentifiers")}</h3>
                <%= (!patient1.paperRecordIdentifiers ?ui.message("emr.none"): "")  %>
                <% patient1.paperRecordIdentifiers.each { %>
                    <% def identifier = (it.preferred ? "<b>${it.identifier}</b>" : it.identifier) %>
                    <div>${identifier}</div>
                <% } %>
            </div>
            <div class="row address">
                <h3>${ui.message("emr.mergePatients.section.addresses")}</h3>
                <% patient1.patient.addresses.each { %>
                    <div><%= ui.includeFragment("emr", "formatAddress", [ address:  it ]) %></div>
                <% } %>
            </div>
            <div class="row">
                <h3>${ui.message("emr.mergePatients.section.lastSeen")}</h3>
                <div> ${ ui.format(patient1.lastEncounter.encounterType) }</div>
                <div> ${ ui.message("emr.atLocation", ui.format(patient1.lastEncounter.location)) }</div>
                <div> ${ ui.message("emr.onDatetime", ui.format(patient1.lastEncounter.encounterDatetime)) }</div>
            </div>
            <div class="row">
                <h3>${ui.message("emr.mergePatients.section.activeVisit")}</h3>
                <% def activeVisit = patient1.getActiveVisit(emrContext.sessionLocation)
                       activeVisit = (activeVisit? ui.format(activeVisit) : ui.message("emr.none"))%>
                <div> ${ activeVisit }</div>
            </div>
            <div class="row">
                <h3>${ui.message("emr.mergePatients.section.dataSummary")}</h3>
                <div>${ ui.message("emr.mergePatients.section.dataSummary.numVisits", patient1.countOfVisits) }</div>
                <div>${ ui.message("emr.mergePatients.section.dataSummary.numEncounters", patient1.countOfEncounters) } </div>
            </div>
        </div>

        <div id="separator" class="separator">
            <% def separatorClass = (isUnknownPatient ? "" : "hidden")  %>
            <div id="separator-right" class="${separatorClass}"><i class="icon-arrow-right"></i></div>
            <div id="separator-left" class="hidden"><i class="icon-arrow-left"></i></div>
        </div>

        <% def patientClass = (isUnknownPatient ? "patient selected" : "patient")  %>
        <div id="second-patient" class="${patientClass}">
            <div class="row name">
                <h3>${ui.message("emr.mergePatients.section.names")}</h3>
                <% patient2.patient.names.each { %>
                <div>${it}</div>
                <% } %>
            </div>
            <div class="row">
                <h3>${ui.message("emr.mergePatients.section.demographics")}</h3>
                <div>${ ui.message("emr.gender." + patient2.patient.gender) }${ patient2.patient.age ? ", " + ui.message("emr.ageYears", patient2.patient.age) : "" }</div>
                <div> ${ ui.message("emr.birthdate") }:${ui.format(patient2.patient.birthdate)}</div>
            </div>
            <div class="row identifiers">
                <h3>${ui.message("emr.mergePatients.section.primaryIdentifiers")}</h3>
                <% patient2.primaryIdentifiers.each { %>
                    <% def identifier = (it.preferred ? "<b>${it.identifier}</b>" : it.identifier) %>
                    <div>${identifier}</div>
                <% } %>
            </div>
            <div class="row identifiers">
                <h3>${ui.message("emr.mergePatients.section.paperRecordIdentifiers")}</h3>
                <%= (!patient1.paperRecordIdentifiers ?ui.message("emr.none"): "")  %>
                <% patient1.paperRecordIdentifiers.each { %>
                    <% def identifier = (it.preferred ? "<b>${it.identifier}</b>" : it.identifier) %>
                    <div>${identifier}</div>
                <% } %>
            </div>
            <div class="row address">
                <h3>${ui.message("emr.mergePatients.section.addresses")}</h3>
                <% patient2.patient.addresses.each { %>
                    <div><%= ui.includeFragment("emr", "formatAddress", [ address:  it ]) %></div>
                <% } %>
            </div>
            <div class="row">
                <h3>${ui.message("emr.mergePatients.section.lastSeen")}</h3>
                <div> ${ ui.format(patient2.lastEncounter.encounterType) }</div>
                <div> ${ ui.message("emr.atLocation", ui.format(patient2.lastEncounter.location)) }</div>
                <div> ${ ui.message("emr.onDatetime", ui.format(patient2.lastEncounter.encounterDatetime)) }</div>
            </div>
            <div class="row">
                <h3>${ui.message("emr.mergePatients.section.activeVisit")}</h3>
                <%  activeVisit = patient2.getActiveVisit(emrContext.sessionLocation)
                    activeVisit = (activeVisit ? ui.format(activeVisit) : ui.message("emr.none")) %>
                <div> ${ activeVisit }</div>
            </div>
            <div class="row">
                <h3>${ui.message("emr.mergePatients.section.dataSummary")}</h3>
                <div>${ ui.message("emr.mergePatients.section.dataSummary.numVisits", patient2.countOfVisits) }</div>
                <div>${ ui.message("emr.mergePatients.section.dataSummary.numEncounters", patient2.countOfEncounters) } </div>
            </div>
        </div>


    </div>


    <div class="messages-container">
        <h2>
            ${ ui.message("emr.mergePatients.confirmationSubtext") }
            <em>${ ui.message("emr.mergePatients.allDataWillBeCombined") }</em>
            <% if (overlappingVisits) { %>
            <em>${ ui.message("emr.mergePatients.overlappingVisitsWillBeJoined") }</em>
            <% } %>
        </h2>
    </div>

    <% def buttonClass = (isUnknownPatient ? "button confirm" : "button disabled") %>
    <% def disabledOption = (isUnknownPatient ? "" : "disabled=\"disabled\" ") %>
    <div class= "buttons">
        <input type="button" id="cancel-button" class="button cancel" value="${ ui.message("emr.no") }"/>
        <input type="submit" id="confirm-button" class="${buttonClass}" ${disabledOption} value="${ ui.message("emr.yesContinue") }"/>
    </div>

</form>