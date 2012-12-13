<%
    ui.decorateWith("emr", "standardEmrPage")

    ui.includeCss("emr", "account.css")

    def createAccount = (account.person.personId == null ? true : false);

    def genderOptions = [ [label: ui.message("Person.gender.male"), value: 'M'],
                          [label: ui.message("Person.gender.female"), value: 'F'] ]

    def privilegeLevelOptions = []
    privilegeLevels.each {
        privilegeLevelOptions.push([ label: it.name, value: it.name ])
    }
%>

<script type="text/javascript" xmlns="http://www.w3.org/1999/html">
function emr_cancel(){
	window.location='/${contextPath}/emr/manageAccounts.page';
}

function sendData(){
    if (jQuery('.emr_userDetails').css('display') != 'none' && jQuery('input[name="capabilities"]').is(':checked') == false){
        alert("${ ui.message("emr.user.Capabilities.required") }");
    } else {
        jQuery("#accountForm").submit();
    }
}

function emr_createProviderAccount(){
	jQuery('.emr_providerDetails').toggle();
	jQuery('#createProviderAccount').val('true');
	jQuery('#interactsWithPatients').attr('checked','checked');
    jQuery("#providerIdentifier").focus();
}
function emr_createUserAccount(){
	jQuery('.emr_userDetails').toggle();
	jQuery('#createUserAccount').val('true');
	jQuery('#enabled').attr('checked','checked');
    jQuery("#username").focus();
}

jq(function() {
   jq('#unlock-button').click(function() {
       jq.post('${ ui.actionLink("emr", "account/account", "unlock", [ personId: account.person.id ]) }', function(data) {
           emr.successMessage(data.message);
           jq('#locked-warning').hide();
       }, 'json').error(function() {
           emr.errorMessage('${ ui.message("emr.account.unlock.failedMessage") }');
       });
   });
});
</script>

<style type="text/css">
    #unlock-button {
        margin-top: 1em;
    }
</style>

<% if (account.locked) { %>
    <div id="locked-warning" class="note warning">
        <div class="icon"><i class="icon-warning-sign medium"></i></div>
        <div class="text">
            <p><strong>${ ui.message("emr.account.locked.title") }</strong></p>
            <p><em>${ ui.message("emr.account.locked.description") }</em></p>

            <button id="unlock-button">${ ui.message("emr.account.locked.button") }</button>

        </div>
    </div>
<% } %>

<h3>${ (createAccount) ? ui.message("emr.createAccount") : ui.message("emr.editAccount") }</h3>

<form method="post" id="accountForm">
	<fieldset>
		<legend>${ ui.message("emr.person.details") }</legend>

        ${ ui.includeFragment("emr", "field/text", [ label: ui.message("emr.person.givenName"), formFieldName: "givenName", initialValue: (account.givenName ?: '') ])}
        ${ ui.includeFragment("emr", "field/text", [ label: ui.message("emr.person.familyName"), formFieldName: "familyName", initialValue: (account.familyName ?: '') ])}
        ${ ui.includeFragment("emr", "field/radioButtons", [ label: ui.message("Person.gender"), formFieldName: "gender", initialValue: (account.gender ?: 'M'), options: genderOptions ])}
	</fieldset>
	
	<fieldset>
		<legend>${ ui.message("emr.user.account.details") }</legend>
		<div class="emr_userDetails" <% if (!account.user) { %> style="display: none" <% } %>>

            ${ ui.includeFragment("emr", "field/checkbox", [ label: ui.message("emr.user.enabled"), formFieldName: "enabled", value: "true", checked: account.enabled ])}
            ${ ui.includeFragment("emr", "field/text", [ label: ui.message("emr.user.username"), formFieldName: "username", initialValue: (account.username ?: '') ])}

            <% if (!showPasswordFields) { %>
                <input class="emr_passwordDetails emr_userDetails" type="button" value="${ ui.message("emr.user.changeUserPassword") }"
                    onclick="javascript:jQuery('.emr_passwordDetails').toggle()" />
                <p></p>
            <% } %>

            <p class="emr_passwordDetails" <% if(!showPasswordFields && account.user) { %>style="display: none"<% } %>>
                <label for="password">${ ui.message("emr.user.password") }</label>
                <input type="password" id="password" name="password" value="" autocomplete="off" />
                ${ ui.includeFragment("emr", "fieldErrors", [ fieldName: "password" ])}
            </p>

            <p class="emr_passwordDetails" <% if(!showPasswordFields && account.user) { %>style="display: none"<% } %>>
                <label for="confirmPassword">${ ui.message("emr.user.confirmPassword") }</label>
                <input type="password" id="confirmPassword" name="confirmPassword" value="" autocomplete="off" />
                ${ ui.includeFragment("emr", "fieldErrors", [ fieldName: "confirmPassword" ])}
            </p>

            ${ ui.includeFragment("emr", "field/dropDown", [ label: ui.message("emr.user.privilegeLevel"), formFieldName: "privilegeLevel", initialValue: (account.privilegeLevel ? account.privilegeLevel.getName() : ''), options: privilegeLevelOptions ])}
            ${ ui.includeFragment("emr", "field/text", [ label: ui.message("emr.user.secretQuestion"), formFieldName: "secretQuestion", initialValue: (account.secretQuestion ?: ''), optional: true ])}

            <p>
                <label for="secretAnswer">${ ui.message("emr.user.secretAnswer") }</label>
                <input type="password" id="secretAnswer" name="secretAnswer" value="" autocomplete="off" />
                ${ ui.message("emr.optional") }
                ${ ui.includeFragment("emr", "fieldErrors", [ fieldName: "secretAnswer" ])}
            </p>

            <p>
                <strong>${ ui.message("emr.user.Capabilities") }</strong>
            </p>

			<% capabilities.each{ %>
                ${ ui.includeFragment("emr", "field/checkbox", [ label: ui.message("emr.app." + (it.name - rolePrefix) + ".label"), formFieldName: "capabilities", value: it.name, checked: account.capabilities.contains(it) ])}
            <% } %>
		</div>
		<div class="emr_userDetails">
			<% if(!account.user) { %>
				<input type="button" id="createUserAccountButton" value="${ ui.message("emr.user.createUserAccount") }"
					onclick="javascript:emr_createUserAccount()" />
			<% } %>
		</div>
	</fieldset>
	
	<fieldset>
		<legend>${ ui.message("emr.provider.details") }</legend>
		<div class="emr_providerDetails" ${ (!account.provider) ? "style='display: none'" : "" }>
            ${ ui.includeFragment("emr", "field/checkbox", [ label: ui.message("emr.provider.interactsWithPatients"), formFieldName: "interactsWithPatients", value: "true", checked: account.interactsWithPatients ])}

            ${ ui.includeFragment("emr", "field/text", [ label: ui.message("emr.provider.identifier"), formFieldName: "providerIdentifier", initialValue: (account.providerIdentifier ?: '') ])}
		</div>
		<div class="emr_providerDetails">
		<% if(!account.provider) { %>
			<input type="button" id="createProviderAccountButton" value="${ ui.message("emr.provider.createProviderAccount") }"
				onclick="javascript:emr_createProviderAccount()" />
		<% } %>
		</div>
	</fieldset>
	
	<input id="createUserAccount" type="hidden" name="createUserAccount" value="${account.user != null && account.user.userId == null}" />
	<input id="createProviderAccount" type="hidden" name="createProviderAccount" value="${account.provider != null && account.provider.providerId == null}" />

    <div>
        <input type="button" class="cancel" value="${ ui.message("emr.cancel") }" onclick="javascript:window.location='/${ contextPath }/emr/systemAdministration.page'" />
        <input type="button" class="confirm" id="save-button" value="${ ui.message("emr.save") }" onclick="javascript:sendData()" />
    </div>

</form>
