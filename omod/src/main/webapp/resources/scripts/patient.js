var requestPaperRecordDialog = null;
var editPatientIdentifierDialog = null;

function showRequestChartDialog () {
    requestPaperRecordDialog.show();
    return false;
}
function showEditPatientIdentifierDialog () {
    editPatientIdentifierDialog.show();
    return false;
}

function createPaperRecordDialog(patientId) {
    requestPaperRecordDialog = emr.setupConfirmationDialog({
        selector: '#request-paper-record-dialog',
        actions: {
            confirm: function() {
                emr.getFragmentActionWithCallback('emr', 'paperrecord/requestPaperRecord', 'requestPaperRecord', { patientId: patientId, locationId: sessionLocationModel.id() }, function(data) {
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
                        location.reload();
                });
            },
            cancel: function() {
                editPatientIdentifierDialog.close();
            }
        }
    });
}