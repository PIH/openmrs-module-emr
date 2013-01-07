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

function enableButton(){
    jq("#confirm-button").removeAttr("disabled");
    jq("#confirm-button").removeClass("disabled");
}

function disableButton(){
    jq("#confirm-button").attr("disabled","disabled");
    jq("#confirm-button").addClass('disabled');
}

function verifyPatientsToMerge(message, items, fieldId ){
    var hiddenId = '';
    var firstValue = jq("#choose-first-value").val();
    var secondValue = jq("#choose-second-value").val();

    if(fieldId=='choose-second'){
        hiddenId = firstValue;
    } else {
        hiddenId = secondValue;
    }

    if(items.length==1 && items[0].patientId == hiddenId){
        items[0].patientId = 0;
        items[0].label = message;

        disableButton();
    } else {
        for (var i = 0 ; i < items.length ; i++){
            if (items[i].patientId == hiddenId){
                //remove item from array
                items.splice(i, 1);

                enableButton();
                return;
            }
        }
    }

    if (firstValue!="" && items.length > 0) {
        enableButton();
    }

}