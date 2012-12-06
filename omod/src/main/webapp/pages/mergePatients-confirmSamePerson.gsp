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
    var preferred = patient2Id;
    jq(function() {

        jq('.first-patient').live('click',function() {
            //this value come from the request (isUnknownPatient parameter)
            var isUnknownPatient = ${isUnknownPatient};

            if(!isUnknownPatient){
                jq('.first-patient').toggleClass('second-patient');
                jq('.second-patient').toggleClass('first-patient');
                jq('.first-patient').toggleClass('second-patient');

                jq('#separator-left').toggleClass('hidden');
                jq('#separator-right').toggleClass('hidden');

                if (preferred == patient2Id){
                    preferred = patient1Id;
                } else {
                    preferred = patient2Id;
                }

                jq('#preferred').val(preferred);

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
    <input type="hidden" name="preferred" id="preferred" value="${ patient2.patient.id }" />
    <input type="hidden" name="confirmed" value="true"/>


    <div class="messages-container">
        <%  if (isUnknownPatient){ %>
            <em class="message-title">${ ui.message("emr.mergePatients.unknownPatient.message") }</em>
        <% } else {%>
            <em class="message-title">${ ui.message("emr.mergePatients.confirmationQuestion") }</em>
        < } %>
        <em class="message">${ ui.message("emr.mergePatients.choosePreferred.description") }</em>
    </div>

    <% def arrows = "<span id=\"separator-right\"><img src=\"/mirebalais/ms/uiframework/resource/uilibrary/images/blue_arrow_right_32.png\"></span> "  %>
    <%  arrows += "<span id=\"separator-left\" class=\"hidden\"><img src=\"/mirebalais/ms/uiframework/resource/uilibrary/images/blue_arrow_left_32.png\" ></span>"  %>

    <table>
        <%= displayList(ui.message("emr.mergePatients.section.names"), patient1.patient.names, "", patient2.patient.names, highlightPreferred) %>
        <%= displayList(ui.message("emr.mergePatients.section.demographics"), [ patient1.patient ], "", [ patient2.patient ], formatDemographics) %>
        <%= displayList(ui.message("emr.mergePatients.section.primaryIdentifiers"), patient1.primaryIdentifiers, "",  patient2.primaryIdentifiers, formatIdentifiers) %>
        <%= displayList(ui.message("emr.mergePatients.section.paperRecordIdentifiers"), patient1.paperRecordIdentifiers, arrows, patient2.paperRecordIdentifiers, formatIdentifiers) %>
        <%= displayList(ui.message("emr.mergePatients.section.addresses"), patient1.patient.addresses, "", patient2.patient.addresses, formatAddress) %>
        <%= displayList(ui.message("emr.mergePatients.section.lastSeen"), [ patient1.lastEncounter ], "", [ patient2.lastEncounter ], formatEncounter) %>
        <%= displayList(ui.message("emr.mergePatients.section.activeVisit"), [ patient1 ], "", [ patient2 ], formatActiveVisit) %>
        <%= displayList(ui.message("emr.mergePatients.section.dataSummary"), [ patient1 ], "", [ patient2 ], formatDataSummary) %>
    </table>

    <div class="messages-container">
        <em class="message-bold">${ ui.message("emr.mergePatients.confirmationSubtext") }</em>
        <em class="message">${ ui.message("emr.mergePatients.allDataWillBeCombined") }</em>
    </div>

    <input type="button" id="cancel-button" class="button secondary" value="${ ui.message("emr.no") }"/>

    <input type="submit" id="confirm-button" class="button primary" value="${ ui.message("emr.yesContinue") }"/>
</form>