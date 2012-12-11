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

                if (jq('#first-patient').hasClass("non-preferred-patient")){
                    jq('#second-patient').removeClass('preferred-patient');
                    jq('#second-patient').addClass('non-preferred-patient');

                    jq('#first-patient').removeClass('non-preferred-patient');
                    jq('#first-patient').addClass('preferred-patient');

                    jq('#separator-left').removeClass('hidden');
                    jq('#separator-right').addClass('hidden');
                }

                jq('#preferred').val(patient1Id);

            }
        });

        jq('div').on('click','#second-patient', function() {

            if(!isUnknownPatient){

                if (jq('#second-patient').hasClass("non-preferred-patient")){
                    jq('#first-patient').removeClass('preferred-patient');
                    jq('#first-patient').addClass('non-preferred-patient');

                    jq('#second-patient').removeClass('non-preferred-patient');
                    jq('#second-patient').addClass('preferred-patient');

                    jq('#separator-left').addClass('hidden');
                    jq('#separator-right').removeClass('hidden');
                }

                 jq('#preferred').val(patient2Id);

            }
        });

        jq('#cancel-button').click(function() {
            window.history.back();
        });

    });
</script>

<form method="post">
    <input type="hidden" name="patient1" value="${ patient1.patient.id }" />
    <input type="hidden" name="patient2" value="${ patient2.patient.id }" />
    <input type="hidden" name="preferred" id="preferred"  />


    <div class="messages-container">
        <%  if (isUnknownPatient){ %>
            <!--em class="message-title">${ ui.message("emr.mergePatients.unknownPatient.message") }</em-->
            <h2>${ ui.message("emr.mergePatients.unknownPatient.message") }</h2>
        <% } else {%>
            <!--em class="message-title">${ ui.message("emr.mergePatients.confirmationQuestion") }</em-->
            <h2>${ ui.message("emr.mergePatients.confirmationQuestion") }
        <% } %>
            <em>${ ui.message("emr.mergePatients.choosePreferred.description") }</em>
        </h2>
    </div>

    <div id="patients">
        <div id="first-patient" class="non-preferred-patient">
            <div class="name">
                <h3>${ui.message("emr.mergePatients.section.names")}</h3>
                <% patient1.patient.names.each { %>
                   <div>${it}</div>
                <% } %>
            </div>
            <div>
                <h3>${ui.message("emr.mergePatients.section.demographics")}</h3>
                <div>${ ui.message("emr.gender." + patient1.patient.gender) }${ patient1.patient.age ? ", " + ui.message("emr.ageYears", patient1.patient.age) : "" }</div>
                <div> ${ ui.message("emr.birthdate") }:${ui.format(patient1.patient.birthdate)}</div>
            </div>
            <div class="identifiers">
                <h3>${ui.message("emr.mergePatients.section.primaryIdentifiers")}</h3>
                <% patient1.primaryIdentifiers.each { %>
                    <% def identifier = (it.preferred ? "<b>${it.identifier}</b>" : it.identifier) %>
                    <div>${identifier}</div>
                <% } %>
            </div>
            <div class="identifiers">
                <h3>${ui.message("emr.mergePatients.section.paperRecordIdentifiers")}</h3>
                <%= (!patient1.paperRecordIdentifiers ?ui.message("emr.none"): "")  %>
                <% patient1.paperRecordIdentifiers.each { %>
                    <% def identifier = (it.preferred ? "<b>${it.identifier}</b>" : it.identifier) %>
                    <div>${identifier}</div>
                <% } %>
            </div>
            <div class="address">
                <h3>${ui.message("emr.mergePatients.section.addresses")}</h3>
                <% patient1.patient.addresses.each { %>
                    <div><%= ui.includeFragment("emr", "formatAddress", [ address:  it ]) %></div>
                <% } %>
            </div>
            <div>
                <h3>${ui.message("emr.mergePatients.section.lastSeen")}</h3>
                <div> ${ ui.format(patient1.lastEncounter.encounterType) }</div>
                <div> ${ ui.message("emr.atLocation", ui.format(patient1.lastEncounter.location)) }</div>
                <div> ${ ui.message("emr.onDatetime", ui.format(patient1.lastEncounter.encounterDatetime)) }</div>
            </div>
            <div>
                <h3>${ui.message("emr.mergePatients.section.activeVisit")}</h3>
                <% def activeVisit = patient1.getActiveVisit(emrContext.sessionLocation)
                       activeVisit = (activeVisit? ui.format(activeVisit) : ui.message("emr.none"))%>
                <div> ${ activeVisit }</div>
            </div>
            <div>
                <h3>${ui.message("emr.mergePatients.section.dataSummary")}</h3>
                <div>${ ui.message("emr.mergePatients.section.dataSummary.numVisits", patient1.countOfVisits) }</div>
                <div>${ ui.message("emr.mergePatients.section.dataSummary.numEncounters", patient1.countOfEncounters) } </div>
            </div>
        </div>

        <div id="separator" class="separator">
            <% def separatorClass = (isUnknownPatient ? "" : "hidden")  %>
            <div id="separator-right" class="${separatorClass}"><img src="${ ui.resourceLink("uilibrary", "images/blue_arrow_right_32.png") }"></div>
            <div id="separator-left" class="hidden"><img src="${ ui.resourceLink("uilibrary", "images/blue_arrow_left_32.png") }" ></div>
        </div>

        <% def patientClass = (isUnknownPatient ? "preferred-patient" : "non-preferred-patient")  %>
        <div id="second-patient" class="${patientClass}">
            <div class="name">
                <h3>${ui.message("emr.mergePatients.section.names")}</h3>
                <% patient2.patient.names.each { %>
                <div>${it}</div>
                <% } %>
            </div>
            <div>
                <h3>${ui.message("emr.mergePatients.section.demographics")}</h3>
                <div>${ ui.message("emr.gender." + patient2.patient.gender) }${ patient2.patient.age ? ", " + ui.message("emr.ageYears", patient2.patient.age) : "" }</div>
                <div> ${ ui.message("emr.birthdate") }:${ui.format(patient2.patient.birthdate)}</div>
            </div>
            <div class="identifiers">
                <h3>${ui.message("emr.mergePatients.section.primaryIdentifiers")}</h3>
                <% patient2.primaryIdentifiers.each { %>
                    <% def identifier = (it.preferred ? "<b>${it.identifier}</b>" : it.identifier) %>
                    <div>${identifier}</div>
                <% } %>
            </div>
            <div class="identifiers">
                <h3>${ui.message("emr.mergePatients.section.paperRecordIdentifiers")}</h3>
                <%= (!patient1.paperRecordIdentifiers ?ui.message("emr.none"): "")  %>
                <% patient1.paperRecordIdentifiers.each { %>
                    <% def identifier = (it.preferred ? "<b>${it.identifier}</b>" : it.identifier) %>
                    <div>${identifier}</div>
                <% } %>
            </div>
            <div class="address">
                <h3>${ui.message("emr.mergePatients.section.addresses")}</h3>
                <% patient2.patient.addresses.each { %>
                    <div><%= ui.includeFragment("emr", "formatAddress", [ address:  it ]) %></div>
                <% } %>
            </div>
            <div>
                <h3>${ui.message("emr.mergePatients.section.lastSeen")}</h3>
                <div> ${ ui.format(patient2.lastEncounter.encounterType) }</div>
                <div> ${ ui.message("emr.atLocation", ui.format(patient2.lastEncounter.location)) }</div>
                <div> ${ ui.message("emr.onDatetime", ui.format(patient2.lastEncounter.encounterDatetime)) }</div>
            </div>
            <div>
                <h3>${ui.message("emr.mergePatients.section.activeVisit")}</h3>
                <%  activeVisit = patient2.getActiveVisit(emrContext.sessionLocation)
                    activeVisit = (activeVisit ? ui.format(activeVisit) : ui.message("emr.none")) %>
                <div> ${ activeVisit }</div>
            </div>
            <div>
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
    <% def buttonClass = (isUnknownPatient ? "button primary" : "button disabled") %>
    <div class= "buttons">
        <input type="button" id="cancel-button" class="button secondary" value="${ ui.message("emr.no") }"/>
        <input type="submit" id="confirm-button" class="${buttonClass}" value="${ ui.message("emr.yesContinue") }"/>
    </div>

</form>