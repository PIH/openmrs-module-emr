<%
    ui.decorateWith("emr", "standardEmrPage", [ title: ui.message("emr.mergePatients") ])
    ui.includeCss("mirebalais", "mergePatients.css")
    ui.includeJavascript("emr", "mergePatients.js")

    def id = ""
    def primaryId = ""
    def fullName = ""

    if (patient1 != null){
         id = patient1.patient.id
         primaryId = patient1.primaryIdentifiers.collect{ it.identifier }.join(',')
         fullName =  ui.format(patient1.patient)
    }

%>

<script type="text/javascript">
    jq(function(){

        unknownPatient("${id}", "${primaryId}",  "${fullName}", ${isUnknownPatient});

        function unknownPatient(id, primaryId, fullName, isUnknownPatient){
            if(id>0 && isUnknownPatient){
                jq("#patient1").val(id);
                jq("#patient1-text").val(primaryId);
                jq("#full-name-field").text(fullName);
                jq("#patient1-text").attr("disabled","disabled");
                jq("#patient1-text").addClass('disabled');
                jq("#patient2-text").focus();
            }
        }
    });

</script>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("emr.app.systemAdministration.label")}", link: '${ui.pageLink("emr", "systemAdministration")}' },
        { label: "${ ui.message("emr.mergePatients")}" }
    ];
</script>

<h3>${ ui.message("emr.mergePatients.selectTwo") }</h3>
<div id="merge-patient-container">
    <h4>${ ui.message("emr.mergePatients.enterIds") }</h4>
    <form>
        <input type= "hidden" name= "isUnknownPatient" value= "${isUnknownPatient}"/>
        <p>
            ${ ui.includeFragment("emr", "field/findPatientById",[
                    label: ui.message("emr.mergePatients.chooseFirstLabel"),
                    hiddenFieldName: "patient1",
                    textFieldName: "patient1-text",
                    callBack: "checkConfirmButton",
                    fullNameField: "full-name-field"
            ] )}
        </p>
        <p>
            ${ ui.includeFragment("emr", "field/findPatientById",[
                    label: ui.message("emr.mergePatients.chooseSecondLabel"),
                    hiddenFieldName: "patient2",
                    textFieldName: "patient2-text",
                    callBack: "checkConfirmButton"
            ] )}
        </p>
        <p class="right">
            <input class="cancel" type="button" id="cancel-button" value="${ ui.message("emr.cancel") }"/>

            <input class="confirm disabled" type="submit" disabled="disabled" id="confirm-button" value="${ ui.message("emr.continue") }"/>
        </p>
    </form>
</div>
