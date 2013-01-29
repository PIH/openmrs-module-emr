<%
    ui.decorateWith("emr", "standardEmrPage", [ title: ui.message("emr.mergePatients") ])
    ui.includeCss("mirebalais", "mergePatients.css")
    ui.includeJavascript("emr", "mergePatients.js")

    def id = ""
    def fullName = ""

    if (patient1 != null){
         id = patient1.patient.id
         fullName = patient1.primaryIdentifiers.collect{ it.identifier }.join(',') + "-" + ui.format(patient1.patient)
    }

%>

<script type="text/javascript">
    jq(function(){

        unknownPatient(${id}, "${fullName}", ${isUnknownPatient});

        function unknownPatient(id, fullName, isUnknownPatient){
            if(id>0 && isUnknownPatient){
                jq("#patient1").val(id);
                jq("#patient1-text").val(fullName);
                jq("#patient1-text").attr("disabled","disabled");
                jq("#patient1-text").addClass('disabled');
                jq("#patient2-text").focus();
            }
        }

    });

</script>

<h3>${ ui.message("emr.mergePatients.selectTwo") }</h3>
<input type= "hidden" name= "isUnknownPatient" value= "${isUnknownPatient}"/>
<div id="merge-patient-container">
    <h4>Please enter the ZL EMR IDs of the two record to merge</h4>
    <form>
        <p>
            ${ ui.includeFragment("emr", "field/findPatientById",[
                    label: "First Patient ID",
                    hiddenFieldName: "patient1",
                    textFieldName: "patient1-text",
                    callBack: "checkConfirmButton",
                    fullNameField: "full-name-field"
            ] )}
        </p>
        <p>
            ${ ui.includeFragment("emr", "field/findPatientById",[
                    label: "Second Patient ID",
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
