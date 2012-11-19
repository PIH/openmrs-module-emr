<%
    ui.decorateWith("emr", "standardEmrPage", [ title: ui.message("emr.mergePatients") ])
    ui.includeCss("mirebalais", "mergePatients.css")

    def formatBirthdate = {
        it.birthdate ?
            ((it.birthdateEstimated ? "~ " : "") + ui.format(it.birthdate)) :
            "?"
    }
%>

<script type="text/javascript">
    jq(function() {
        jq('#cancel-button').click(function() {
            emr.navigateTo({ page: 'systemAdministration' });
        });
        jq('#prefer-form').on('change', 'input[name=preferred]', function(eventObject) {
            jq('.choose-demographics').removeClass('primary');
            var preferId = jq(eventObject.target).val();
            jq('.prefer-' + preferId).closest("label").addClass('primary');
            jq('#perform-merge').removeAttr('disabled').addClass("primary");
        });
    });
</script>

<h3>About to merge records...</h3>

<span class="panel first-patient">
    ${ patient1.primaryIdentifier.identifier } - ${ ui.format(patient1.patient) }
</span>

and

<span class="panel second-patient">
    ${ patient2.primaryIdentifier.identifier } - ${ ui.format(patient2.patient) }
</span>

<br/>
<br/>

<form id="prefer-form" method="post">
    <h3>Choose demographics to keep:</h3>

    <label for="choose1" class="panel choose-demographics prefer-${ patient1.patient.id }">
        <input type="radio" id="choose1" name="preferred" value="${ patient1.patient.id }" class="hidden"/>

        Gender: ${ patient1.gender } <br/>
        Birthdate: ${ formatBirthdate(patient1.patient) } <br/>
        Age: ${ patient1.age ?: "?" }
    </label>

    <label for="choose2" class="panel choose-demographics prefer-${ patient2.patient.id }">
        <input type="radio" id="choose2" name="preferred" value="${ patient2.patient.id }" class="hidden"/>

        Gender: ${ patient2.gender } <br/>
        Birthdate: ${ formatBirthdate(patient2.patient) } <br/>
        Age: ${ patient2.age ?: "?" }
    </label>

    <br/><br/>

    <% if (overlappingVisits) { %>
        <h3>These records have visits that overlap, and will be joined together.</h3>
    <% } %>

    <h3>All other data will be combined.</h3>

    <br/><br/>

    <input type="button" id="cancel-button" class="button secondary" value="${ ui.message("emr.cancel") }"/>

    <input type="submit" id="perform-merge" disabled="disabled" class="button" value="Perform Merge"/>
</form>