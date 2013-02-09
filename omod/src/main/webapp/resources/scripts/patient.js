var requestPaperRecordDialog = null;

function showRequestChartDialog () {
    requestPaperRecordDialog.show();
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