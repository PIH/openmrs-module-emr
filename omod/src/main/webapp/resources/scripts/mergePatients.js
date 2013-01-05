jq(function() {
    jq('input[type=text]').first().focus();

    jq('#cancel-button').click(function() {
        window.history.back();
    });
});

function labelFunction(item) {
    var id = item.patientId;
    if (item.primaryIdentifiers[0]) {
        id = item.primaryIdentifiers[0].identifier;
    }
    return id + ' - ' + item.preferredName.fullName;
}

function verifyPatientsToMerge(message){
    var firstValue = jq("#choose-first-value").val();
    var secondValue = jq("#choose-second-value").val();

    if (firstValue!="" && secondValue!="" && (firstValue == secondValue)){
        emr.errorAlert(message);
    } else if (firstValue!="" && secondValue!="") {
        jq("#confirm-button").removeAttr("disabled");
        jq("#confirm-button").removeClass("disabled");
    }
}