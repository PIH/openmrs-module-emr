var requestPaperRecordDialog = null;
var editPatientIdentifierDialog = null;
var deleteEncounterDialog= null;

function showRequestChartDialog () {
    requestPaperRecordDialog.show();
    return false;
}
function showEditPatientIdentifierDialog () {
    editPatientIdentifierDialog.show();
    return false;
}

function showDeleteEncounterDialog () {
    deleteEncounterDialog.show();
    return false;
}

function createPaperRecordDialog(patientId) {
    requestPaperRecordDialog = emr.setupConfirmationDialog({
        selector: '#request-paper-record-dialog',
        actions: {
            confirm: function() {
                emr.getFragmentActionWithCallback('emr', 'paperrecord/requestPaperRecord', 'requestPaperRecord'
                    , { patientId: patientId, locationId: sessionLocationModel.id() }
                    , function(data) {
                        emr.successMessage(data.message);
                        requestPaperRecordDialog.close();
                    });
            },
            cancel: function() {
                requestPaperRecordDialog.close();
            }
        }
    });
}
function createEditPatientIdentifierDialog(patientId) {
    editPatientIdentifierDialog = emr.setupConfirmationDialog({
        selector: '#edit-patient-identifier-dialog',
        actions: {
            confirm: function() {
                emr.getFragmentActionWithCallback('emr', 'editPatientIdentifier', 'editPatientIdentifier'
                    , { patientId: patientId,
                        identifierTypeId: jq("#hiddenIdentifierTypeId").val(),
                        identifierValue: jq("#patientIdentifierValue").val()
                    }
                    , function(data) {
                        emr.successMessage(data.message);
                        editPatientIdentifierDialog.close();
                        var newValue= jq("#patientIdentifierValue").val();
                        if(newValue.length>0){
                            jq(".editPatientIdentifier").parents("span:first").removeClass('add-id');
                            jq(".editPatientIdentifier").attr("data-patient-identifier-value", newValue);
                            jq(".editPatientIdentifier").text(newValue);
                        }else{
                            jq(".editPatientIdentifier").parents("span:first").addClass('add-id');
                            jq(".editPatientIdentifier").text(addMessage);
                        }
                    },function(err){
                        emr.handleError(err);
                        editPatientIdentifierDialog.close();
                    });
            },
            cancel: function() {
                editPatientIdentifierDialog.close();
            }
        }
    });
}
function createDeleteEncounterDialog(encounterId, deleteElement) {
    deleteEncounterDialog = emr.setupConfirmationDialog({
        selector: '#delete-encounter-dialog',
        actions: {
            confirm: function() {
                emr.getFragmentActionWithCallback('emr', 'visit/visitDetails', 'deleteEncounter'
                    , { encounterId: encounterId}
                    , function(data) {
                        emr.successMessage(data.message);
                        deleteEncounterDialog.close();
                        var encounterElement = deleteElement.parents("li:first");
                        if(encounterElement!=null && encounterElement!=undefined){
                            encounterElement.remove();
                        }
                    },function(err){
                        emr.handleError(err);
                        deleteEncounterDialog.close();
                    });
            },
            cancel: function() {
                deleteEncounterDialog.close();
            }
        }
    });
}
