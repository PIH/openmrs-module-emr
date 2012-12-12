<%
    ui.decorateWith("emr", "standardEmrPage")

    ui.includeCss("emr", "account.css")

    def createAccount = (account.person.personId == null ? true : false);
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
        <p>
            <label for="givenName">${ ui.message("emr.person.givenName") }</label>
            <input type="text" id="givenName" name="givenName" value="${(account.givenName) ? account.givenName : ""}" />
            ${ ui.includeFragment("emr", "fieldErrors", [ fieldName: "givenName" ])}
        </p>

        <p>
            <label for="familyName">${ ui.message("emr.person.familyName") }</label>
            <input type="text" id="familyName" name="familyName" value="${(account.familyName) ? account.familyName : ""}" />
            ${ ui.includeFragment("emr", "fieldErrors", [ fieldName: "familyName" ])}
        </p>

		<p>
            <strong>${ ui.message("Person.gender") }</strong>
        </p>

        <p>
            <input type="radio" id="male" name="gender" value="M" <% if(account.gender == 'M'|| createAccount){ %>checked=checked<% } %> />
            <label for="male">${ ui.message("Person.gender.male") }</label>
        </p>

        <p>
            <input type="radio" id="female" name="gender" value="F" <% if(account.gender == 'F'){ %>checked=checked<% } %> />
            <label for="female">${ ui.message("Person.gender.female") }</label>
        </p>
	</fieldset>
	
	<fieldset>
		<legend>${ ui.message("emr.user.account.details") }</legend>
		<div class="emr_userDetails" <% if (!account.user) { %> style="display: none" <% } %>>
			<p>
                <input id="enabled" type="checkbox" name="enabled" value="true" <% if(account.enabled){ %>checked='checked'<% } %> />
                <label for="enabled">${ ui.message("emr.user.enabled") }</label>
            </p>
		
            <p>
                <label for="username">${ ui.message("emr.user.username") }</label>
                <input type="text" name="username" id="username" value="${(account.username) ? account.username : ""}" />
                ${ ui.includeFragment("emr", "fieldErrors", [ fieldName: "username" ])}
            </p>

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

            <p>
                <label for="privilegeLevel">${ ui.message("emr.user.privilegeLevel") }</label>

                <select id="privilegeLevel" name="privilegeLevel">
                    <option></option>
                    <% privilegeLevels.each{ %>
                        <option value="${ it.name }" <% if(account.privilegeLevel == it){ %>selected='selected'<% } %>>${ it.name }</option>
                    <% } %>
                </select>
                ${ ui.includeFragment("emr", "fieldErrors", [ fieldName: "privilegeLevel" ])}
            </p>

            <p>
                <label for="secretQuestion">${ ui.message("emr.user.secretQuestion") }</label>
                <input type="text" id="secretQuestion" name="secretQuestion"
                    value="${ (account.secretQuestion) ? account.secretQuestion : "" }" /> ${ ui.message("general.optional") }
            </p>

            <p>
                <label for="secretAnswer">${ ui.message("emr.user.secretAnswer") }</label>
                <input type="password" id="secretAnswer" name="secretAnswer" value="" autocomplete="off" />
                ${ ui.message("general.optional") }
                ${ ui.includeFragment("emr", "fieldErrors", [ fieldName: "secretAnswer" ])}
            </p>

            <p>
                <strong>${ ui.message("emr.user.Capabilities") }</strong>
            </p>

			<% capabilities.each{ %>
				<p>
                    <input type="checkbox" name="capabilities" id="${ it.name - rolePrefix }" value="${ it.name }" <% if(account.capabilities.contains(it)){ %>checked='checked'<% } %> />
                    <label for="${ it.name - rolePrefix }">${ ui.message("emr.app." + (it.name - rolePrefix) + ".label") }</label>
                    ${ ui.includeFragment("emr", "fieldErrors", [ fieldName: "capabilities" ])}
                </p>
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
			<input id="interactsWithPatients" type="checkbox" name="interactsWithPatients" value="true" 
				<% if(account.interactsWithPatients){ %>checked='checked'<% } %> />
            <label for="interactsWithPatients">${ ui.message("emr.provider.interactsWithPatients") }</label>

            <p>
                <label for="providerIdentifier">${ ui.message("emr.provider.identifier") }</label>
                <input type="text" name="providerIdentifier" id="providerIdentifier" value="${(account.providerIdentifier) ? account.providerIdentifier : ""}" />
                ${ ui.includeFragment("emr", "fieldErrors", [ fieldName: "providerIdentifier" ])}
            </p>
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
        <input type="button" class="cancel" value="${ ui.message("general.cancel") }" onclick="javascript:window.location='/${ contextPath }/emr/systemAdministration.page'" />
        <input type="button" class="confirm" id="save-button" value="${ ui.message("general.save") }" onclick="javascript:sendData()" />
    </div>

</form>
