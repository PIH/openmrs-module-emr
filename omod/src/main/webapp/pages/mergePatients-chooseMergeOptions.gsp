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
            window.history.back();
        });
        jq('#prefer-form').on('change', 'input[name=preferred]', function(eventObject) {
            jq('.choose-demographics').removeClass('primary');
            var preferId = jq(eventObject.target).val();
            jq('.prefer-' + preferId).closest("label").addClass('primary');
            jq('#perform-merge').removeAttr('disabled').addClass("primary");

            if (jq('.prefer-' + preferId).closest("label").hasClass("left-option")) {
                jq('#separator').html('<img src="${ ui.resourceLink("uilibrary", "images/blue_arrow_left_32.png") }"/>');
            } else {
                jq('#separator').html('<img src="${ ui.resourceLink("uilibrary", "images/blue_arrow_right_32.png") }"/>');
            }
        });
    });
</script>

<h3>${ ui.message("emr.mergePatients.choosePreferred.question") }</h3>
<em>${ ui.message("emr.mergePatients.choosePreferred.description") }</em>
<br/><br/>

<form id="prefer-form" method="post">

    <label for="choose1" class="panel choose-demographics prefer-${ patient1.patient.id } left-option">
        <input type="radio" id="choose1" name="preferred" value="${ patient1.patient.id }" class="hidden"/>

        <strong>${ ui.format(patient1.patient) }</strong><br/>
        ${ ui.format(patient1.primaryIdentifier) }
        <hr/>
        ${ ui.message("emr.gender") }: ${ ui.message("emr.gender." + patient1.gender) } <br/>
        ${ ui.message("emr.birthdate") }: ${ formatBirthdate(patient1.patient) } <br/>
        ${ ui.message("emr.age") }: ${ patient1.age ? ui.message("emr.ageYears", patient1.age) : ui.message("emr.unknownAge") }
    </label>

    <span id="separator"></span>

    <label for="choose2" class="panel choose-demographics prefer-${ patient2.patient.id } right-option">
        <input type="radio" id="choose2" name="preferred" value="${ patient2.patient.id }" class="hidden"/>

        <strong>${ ui.format(patient2.patient) }</strong> <br/>
        ${ ui.format(patient2.primaryIdentifier) }
        <hr/>
        ${ ui.message("emr.gender") }: ${ ui.message("emr.gender." + patient2.gender) } <br/>
        ${ ui.message("emr.birthdate") }: ${ formatBirthdate(patient2.patient) } <br/>
        ${ ui.message("emr.age") }: ${ patient2.age ? ui.message("emr.ageYears", patient2.age) : ui.message("emr.unknownAge") }
    </label>

    <br/><br/>


    <h4>${ ui.message("emr.mergePatients.allDataWillBeCombined") }</h4>

    <% if (overlappingVisits) { %>
        <h4>${ ui.message("emr.mergePatients.overlappingVisitsWillBeJoined") }</h4>
    <% } %>
    <br/>

    <input type="button" id="cancel-button" class="button secondary" value="${ ui.message("emr.cancel") }"/>

    <input type="submit" id="perform-merge" disabled="disabled" class="button" value="${ ui.message("emr.mergePatients.performMerge") }"/>
</form>