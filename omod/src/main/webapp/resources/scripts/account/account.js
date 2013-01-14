
function emr_createProviderAccount(){
    jQuery('.emr_providerDetails').toggle();
    jQuery('#providerEnabled-field').attr('checked','checked');
    jQuery("#providerIdentifier").focus();
    }
function emr_createUserAccount(){
    jQuery('.emr_passwordDetails').show();
    jQuery('.emr_userDetails').toggle();
    jQuery('#userEnabled-field').attr('checked','checked');
    jQuery("#username").focus();
    }

jq(function() {
    jq('#unlock-button').click(function(e) {

        jq.post(emr.fragmentActionLink("emr", "account/account", "unlock", { personId: jq(this).val() }), function (data) {
            emr.successMessage(data.message);
            jq('#locked-warning').hide();
        }, 'json').error(function(xhr) {
            emr.handleError(xhr);
}       );
    });
});

