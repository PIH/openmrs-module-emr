<%
    ui.decorateWith("emr", "standardEmrPage")
%>

${ ui.includeFragment("emr", "patientHeader", [ patient: patient ]) }

<% if (displayWith == "htmlformentry") { %>

    ${ ui.includeFragment("emr", "htmlform/viewEncounterWithHtmlForm", [ encounter: encounter ]) }

<% } else { %>

    Type: ${ ui.format(encounter.encounterType) } <br/>
    Date: ${ ui.format(encounter.encounterDatetime) } <br/>
    Location: ${ ui.format(encounter.location) } <br/>

    <br/>

    <table>
        <thead>
            <tr>
                <th>Observations</th>
                <th>Orders</th>
                <th>Providers</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td>
                    <% encounter.getAllObs(true).each { %>
                        <% if (it.voided) { %><strike><% } %>
                            ${ ui.format(it.concept) } = ${ ui.format(it) } <br/>
                        <% if (it.voided) { %></strike><% } %>
                    <% } %>
                </td>
                <td>
                    <% encounter.orders.each { %>
                        ${ ui.format(it.concept) }
                    <% } %>
                </td>
                <td>
                    <% encounter.providersByRoles.each { %>
                        ${ ui.format(it.key) } =
                        <% it.value.each { %>
                            ${ ui.format(it) }
                        <% } %>
                    <% } %>
                </td>
            </tr>
        </tbody>
    </table>

<% } %>