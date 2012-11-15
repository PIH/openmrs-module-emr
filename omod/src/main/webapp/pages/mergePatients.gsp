<%
    ui.decorateWith("emr", "standardEmrPage", [ title: ui.message("emr.mergePatients") ])

    def displayList = { title, leftList, rightList ->
        def ret = """
        <tr>
            <th>${ title }</th>
            <td class="always-merge">
        """
        leftList.each {
            ret += ui.format(it) + "\n";
        }
        ret += """
            </td>
            <td class="always-merge">
        """
        rightList.each {
            ret += ui.format(it) + "\n";
        }
        ret += """
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

<% if (patient1 != null && patient2 != null) { %>
    <%
        def leftId = patient1.patient.id
        def rightId = patient2.patient.id
    %>

    <style type="text/css">
        .preferred-${ leftId }, .preferred-${ rightId } {
            background-color: #d3d3d3;
        }
        .always-merge, .preferred-merge {
            background-color: #add8e6;
            font-weight: bold;
        }
    </style>
    <script type="text/javascript">
        jq(function() {
            jq('#prefer-form').on('change', 'input[name=preferred]', function(eventObject) {
                jq('.preferred-merge').removeClass('preferred-merge');
                var preferId = jq(eventObject.target).val();
                jq('.preferred-' + preferId).addClass('preferred-merge');
                jq('#perform-merge').removeAttr('disabled');
            });
        });
    </script>

    About two merge these two patient records. <em>Make sure they are the same person</em>.

    <form id="prefer-form" method="post">
        <fieldset>
            <legend>
                Choose which set of demographic details you want to keep
            </legend>
            <table>
                <tr>
                    <th></th>
                    <td class="preferred-${ leftId }">
                        <input type="radio" id="prefer-left" name="preferred" value="${ patient1.patient.id }"/>
                        <label for="prefer-left">Choose left</label>
                    </td>
                    <td class="preferred-${ rightId }">
                        <input type="radio" id="prefer-right" name="preferred" value="${ patient2.patient.id }"/>
                        <label for="prefer-right">Choose right</label>
                    </td>
                </tr>
                <tr>
                    <th>Gender</th>
                    <td class="preferred-${ leftId }">${ patient1.patient.gender }</td>
                    <td class="preferred-${ rightId }">${ patient2.patient.gender }</td>
                </tr>
                <tr>
                    <th>Birthdate</th>
                    <td class="preferred-${ leftId }">
                        <% if (patient1.patient.birthdateEstimated) { %>~<% } %>
                        ${ ui.format(patient1.patient.birthdate) }
                    </td>
                    <td class="preferred-${ rightId }">
                        <% if (patient2.patient.birthdateEstimated) { %>~<% } %>
                        ${ ui.format(patient2.patient.birthdate) }
                    </td>
                </tr>
                <tr>
                    <th>Age</th>
                    <td class="preferred-${ leftId }">${ patient1.patient.age }</td>
                    <td class="preferred-${ rightId }">${ patient2.patient.age }</td>
                </tr>
            </table>
        </fieldset>

        <fieldset>
            <legend>Other data will be combined</legend>

            <table>
                <%= displayList("Name(s)", patient1.patient.names, patient2.patient.names) %>
                <%= displayList("Identifier(s)", patient1.primaryIdentifiers.collect{ it.identifier }, patient2.primaryIdentifiers.collect{ it.identifier }) %>
                <%= displayList("Paper Record Identifier(s)", patient1.paperRecordIdentifiers.collect{ it.identifier }, patient2.paperRecordIdentifiers.collect{ it.identifier }) %>
                <%= displayList("Other ID(s)", ["TODO"], ["TODO"]) %>
                <%= displayList("Address(es)", patient1.patient.addresses, patient2.patient.addresses) %>
                <tr>
                    <th>Last Seen</th>
                    <td class="always-merge">${ ui.format(patient1.lastEncounter) }</td>
                    <td class="always-merge">${ ui.format(patient2.lastEncounter) }</td>
                </tr>
                <tr>
                    <th>Active Visit</th>
                    <td class="always-merge">
                        ${ ui.format(patient1.getActiveVisit(emrContext.sessionLocation)) }
                    </td>
                    <td class="always-merge">
                        ${ ui.format(patient2.getActiveVisit(emrContext.sessionLocation)) }
                    </td>
                </tr>
            </table>
        </fieldset>

        <input id="perform-merge" disabled="disabled" type="submit" value="Perform Merge"/>

        <input type="button" id="cancel-button" value="${ ui.message("emr.cancel") }"/>
    </form>

<% } else { %>

    <script type="text/javascript">
        jq(function() {
            jq('input[type=text]').first().focus();
        });

        function labelFunction(item) {
            var id = item.patientId;
            if (item.primaryIdentifiers[0]) {
                id = item.primaryIdentifiers[0].identifier;
            }
            return id + ' - ' + item.preferredName.fullName;
        }
    </script>

    <form method="get">

        ${ ui.includeFragment("emr", "field/autocomplete", [
                label: "Choose one patient",
                formFieldName: "patient1",
                fragment: "findPatient",
                action: "search",
                itemValueProperty: "patientId",
                itemLabelFunction: "labelFunction"
        ])}

        <br/>

        ${ ui.includeFragment("emr", "field/autocomplete", [
                label: "Choose another patient",
                formFieldName: "patient2",
                fragment: "findPatient",
                action: "search",
                itemValueProperty: "patientId",
                itemLabelFunction: "labelFunction"
        ])}

        <br/>

        <input type="submit" value="${ ui.message("emr.continue") }"/>

        <input type="button" id="cancel-button" value="${ ui.message("emr.cancel") }"/>

    </form>

<% } %>