<%
    ui.decorateWith("emr", "standardEmrPage")
%>

<script type="text/javascript">
function emr_cancel(){
	window.location='/${contextPath}/emr/manageAccounts.page';
}
</script>

<h3>${ ui.message("emr.editAccount") }</h3>

<form method="post">
	<fieldset>
		<legend>${ ui.message("emr.person.details") }</legend>
		${ ui.message("emr.person.givenName") } <input type="text" name="givenName" value="${(account.givenName) ? account.givenName : ""}" /> &nbsp;&nbsp;&nbsp; 
		${ ui.message("emr.person.familyName") } <input type="text" name="familyName" value="${(account.familyName) ? account.familyName : ""}" />
		
		<br /><br />
		${ ui.message("Person.gender") } &nbsp;
		<input type="radio" name="gender" value="M" <% if(account.gender == 'M'){ %>checked=checked<% } %> /> ${ ui.message("Person.gender.male") }
		<input type="radio" name="gender" value="F" <% if(account.gender == 'F'){ %>checked=checked<% } %> /> ${ ui.message("Person.gender.female") }
	</fieldset>
	
	<br />
	
	<fieldset>
		<legend>${ ui.message("emr.user.account.details") }</legend>
		<% if(account.user){ %>
		<input type="checkbox" name="retired" value="${account.retired}" <% if(!account.retired){ %>checked='checked'<% } %> /> ${ ui.message("emr.user.enabled") }
		
		<br /><br />
		<table cellpadding="0" cellspacing="5" border="0">
			<tr>
				<td>${ ui.message("emr.user.username") }</td>
				<td><input type="text" name="username" value="${(account.username) ? account.username : ""}" /></td>
			</tr> 
			<tr>
				<td>${ ui.message("emr.user.password") }</td>
				<td class="label"><input type="password" name="password" value="" autocomplete="off" /></td>
			</tr>
			<tr>
				<td>${ ui.message("emr.user.confirmPassword") }</td>
				<td><input type="password" name="confirmPassword" value="" autocomplete="off" /></td>
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
				</td>
			</tr> 
		</table>
		
		<br />
		${ ui.message("emr.user.Capabilities") } 
		<div style="padding-left: 20px">
		<% capabilities.each{ %>
			<br />
			<input type="checkbox" name="capabilities" value="${ it.name }" <% if(account.capabilities.contains(it)){ %>checked='checked'<% } %> /> ${ it.name }
		<% } %>
		</div>
		<% } else {%>
			<input type="button" value="${ ui.message("emr.user.createUserAccount") }" onclick="javascript:window.location='/${ contextPath }/emr/manageUsers.page'" />
		<% } %>
	</fieldset>
	
	<br />
	<fieldset>
		<legend>${ ui.message("emr.provider.details") }</legend>
		<input type="checkbox" name="interactsWithPatients" value="true" <% if(account.interactsWithPatients){ %>checked='checked'<% } %> /> ${ ui.message("emr.provider.interactsWithPatients") }  
		
		<br /><br />
		${ ui.message("emr.provider.identifier") } <input type="text" name="providerIdentifier" value="${(account.providerIdentifier) ? account.providerIdentifier : ""}" />
	</fieldset>
	
	<br /><br />
	<input type="hidden" name="personId" value="${ account.person.personId }" />
	<input type="submit" value="${ ui.message("general.save") }" /> &nbsp;&nbsp;&nbsp;
	<input type="button" value="${ ui.message("general.cancel") }" onclick="javascript:window.location='/${ contextPath }/emr/manageUsers.page'" />
		
</form>