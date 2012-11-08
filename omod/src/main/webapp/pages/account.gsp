
<%
    ui.decorateWith("emr", "standardEmrPage")
%>

<script type="text/javascript">
function emr_cancel(){
	window.location='/${contextPath}/emr/manageAccounts.page';
}
function emr_createProviderAccount(){
	jQuery('.emr_providerDetails').toggle();
	jQuery('#createProviderAccount').val('true');
	jQuery('#interactsWithPatients').attr('checked','checked');
}
function emr_createUserAccount(){
	jQuery('.emr_userDetails').toggle();
	jQuery('#createUserAccount').val('true');
	jQuery('#enabled').attr('checked','checked');
}
</script>

<h3>${ ui.message("emr.editAccount") }</h3>

<form method="post">
	<fieldset>
		<legend>${ ui.message("emr.person.details") }</legend>
		${ ui.message("emr.person.givenName") } 
		<input type="text" name="givenName" value="${(account.givenName) ? account.givenName : ""}" /> &nbsp;&nbsp;&nbsp; 
		${ ui.includeFragment("emr", "fieldErrors", [ fieldName: "givenName" ])}
		
		${ ui.message("emr.person.familyName") } 
		<input type="text" name="familyName" value="${(account.familyName) ? account.familyName : ""}" />
		${ ui.includeFragment("emr", "fieldErrors", [ fieldName: "familyName" ])}
		
		<br /><br />
		${ ui.message("Person.gender") } &nbsp;
		<input type="radio" name="gender" value="M" <% if(account.gender == 'M'){ %>checked=checked<% } %> /> ${ ui.message("Person.gender.male") }
		<input type="radio" name="gender" value="F" <% if(account.gender == 'F'){ %>checked=checked<% } %> /> ${ ui.message("Person.gender.female") }
	</fieldset>
	
	<br />
	
	<fieldset>
		<legend>${ ui.message("emr.user.account.details") }</legend>
		<div class="emr_userDetails" <% if (!account.user) { %> style="display: none" <% } %>>
			<input id="enabled" type="checkbox" name="enabled" value="true" <% if(account.enabled){ %>checked='checked'<% } %> /> ${ ui.message("emr.user.enabled") }
		
			<br /><br />
			<table cellpadding="0" cellspacing="5" border="0">
				<tr>
					<td>${ ui.message("emr.user.username") }</td>
					<td>
						<input type="text" name="username" value="${(account.username) ? account.username : ""}" />
						${ ui.includeFragment("emr", "fieldErrors", [ fieldName: "username" ])} &nbsp;
						<% if (!showPasswordFields) { %>
						<input class="emr_passwordDetails emr_userDetails" type="button" value="${ ui.message("emr.user.changeUserPassword") }" 
							onclick="javascript:jQuery('.emr_passwordDetails').toggle()" />
						<% } %>
					</td>
				</tr>
				<tr class="emr_passwordDetails" <% if(!showPasswordFields && account.user) { %>style="display: none"<% } %>>
					<td>${ ui.message("emr.user.password") }</td>
					<td>
						<input type="password" name="password" value="" autocomplete="off" />
						${ ui.includeFragment("emr", "fieldErrors", [ fieldName: "password" ])}
					</td>
				</tr>
				<tr class="emr_passwordDetails" <% if(!showPasswordFields && account.user) { %>style="display: none"<% } %>>
					<td>${ ui.message("emr.user.confirmPassword") }</td>
					<td>
						<input type="password" name="confirmPassword" value="" autocomplete="off" />
						${ ui.includeFragment("emr", "fieldErrors", [ fieldName: "confirmPassword" ])}
					</td>
				</tr>
				<tr>
					<td>${ ui.message("emr.user.privilegeLevel") }</td>
					<td>
						<select name="privilegeLevel">
						<option></option>
						<% privilegeLevels.each{ %>
						<option value="${ it.name }" <% if(account.privilegeLevel == it){ %>selected='selected'<% } %>>${ it.name }</option>
						<% } %>
						</select>
						${ ui.includeFragment("emr", "fieldErrors", [ fieldName: "privilegeLevel" ])}
					</td>
				</tr>
				<tr>
					<td>${ ui.message("emr.user.secretQuestion") }</td>
					<td>
						<input type="text" name="secretQuestion" size="50" 
							value="${ (account.secretQuestion) ? account.secretQuestion : "" }" /> ${ ui.message("general.optional") }
					</td>
				</tr>
				<tr>
					<td>${ ui.message("emr.user.secretAnswer") }</td>
					<td>
						<input type="password" name="secretAnswer" size="50" 
							value="" autocomplete="off" /> ${ ui.message("general.optional") } 
						${ ui.includeFragment("emr", "fieldErrors", [ fieldName: "secretAnswer" ])}
					</td>
				</tr>
			</table>
		
			<br />
			${ ui.message("emr.user.Capabilities") } 
			<div style="padding-left: 20px">
			<% capabilities.each{ %>
				<br />
				<input type="checkbox" name="capabilities" value="${ it.name }" <% if(account.capabilities.contains(it)){ %>checked='checked'<% } %> /> ${ ui.message("emr.app." + (it.name - rolePrefix) + ".label") }
				${ ui.includeFragment("emr", "fieldErrors", [ fieldName: "capabilities" ])}
				<% } %>
			</div>
		</div>
		<div class="emr_userDetails">
			<% if(!account.user) { %>
				<input type="button" value="${ ui.message("emr.user.createUserAccount") }" 
					onclick="javascript:emr_createUserAccount()" />
			<% } %>
		</div>
	</fieldset>
	
	<br />
	<fieldset>
		<legend>${ ui.message("emr.provider.details") }</legend>
		<div class="emr_providerDetails" ${ (!account.provider) ? "style='display: none'" : "" }>
			<input id="interactsWithPatients" type="checkbox" name="interactsWithPatients" value="true" 
				<% if(account.interactsWithPatients){ %>checked='checked'<% } %> /> ${ ui.message("emr.provider.interactsWithPatients") }  
		
			<br /><br />
			${ ui.message("emr.provider.identifier") } 
			<input type="text" name="providerIdentifier" value="${(account.providerIdentifier) ? account.providerIdentifier : ""}" />
			${ ui.includeFragment("emr", "fieldErrors", [ fieldName: "providerIdentifier" ])}
		</div>
		<div class="emr_providerDetails">
		<% if(!account.provider) { %>
			<input type="button" value="${ ui.message("emr.provider.createProviderAccount") }" 
				onclick="javascript:emr_createProviderAccount()" />
		<% } %>
		</div>
	</fieldset>
	
	<br /><br />
	<input type="hidden" name="personId" value="${ account.person.personId }" />
	<input id="createUserAccount" type="hidden" name="createUserAccount" value="${account.user != null && account.user.userId == null}" />
	<input id="createProviderAccount" type="hidden" name="createProviderAccount" value="${account.provider != null && account.provider.providerId == null}" />
	<input type="submit" value="${ ui.message("general.save") }" /> &nbsp;&nbsp;&nbsp;
	<input type="button" value="${ ui.message("general.cancel") }" onclick="javascript:window.location='/${ contextPath }/emr/manageAccounts.page'" />
		
</form>
<br /><br />