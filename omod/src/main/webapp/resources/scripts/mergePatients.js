jq(function() {
    jq('input[type=text]').first().focus();

    jq('#cancel-button').click(function() {
        window.history.back();
    });

    jq("#choose-first-search").keyup(function(){
        var length = jq("#choose-first-search").val().length;

        if(length<3){
            jq("#choose-first-value").val('');
            disableButton();
        } else {
            if (jq("#choose-first-value").val()){
                enableButton();
            }
        }

    });

    jq("#choose-second-search").keyup(function(){
        var length = jq("#choose-second-search").val().length;

        if(length<3){
            jq("#choose-second-value").val('');
            disableButton();
        } else {
            if (jq("#choose-second-value").val()){
                enableButton();
            }
        }

    });

    jq(document).on('click','li a', function(){
        var firstValue = jq("#choose-first-value").val();
        var secondValue = jq("#choose-second-value").val();

        if (firstValue!="" && firstValue!="0" && secondValue!="" && secondValue!="0"){
            enableButton();
        } else {
            disableButton();
        }
    });
});

function labelFunction(item) {
    var id = item.patientId;
    if (id != 0 && item.primaryIdentifiers && item.primaryIdentifiers[0]) {
        id = item.primaryIdentifiers[0].identifier;
        return id + ' - ' + item.preferredName.fullName;
    }else{
        return item.textValue;
    }
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

    if(items.length==1 && (items[0].patientId == hiddenId || items[0].patientId==0)){
        items[0].patientId = 0;
        items[0].label = message;

        disableButton();
        return ;
    } else {
        for (var i = 0 ; i < items.length ; i++){
            if (items[i].patientId == hiddenId){

                //remove item from array
                items.splice(i, 1);

                return;
            }
        }
    }

    if (firstValue!="" && firstValue!="0" && secondValue!="" && secondValue!="0") {
        enableButton();
    }

}