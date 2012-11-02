<%
    ui.decorateWith("emr", "standardEmrPage")
%>

<h3>${ ui.message("emr.editAccount") }</h3>

<form method="post">
	<fieldset>
		<legend>${ ui.message("emr.person.details") }</legend>
		${ ui.message("emr.person.givenName") } <input type="text" name="givenName" value="${account.givenName}" /> &nbsp;&nbsp;&nbsp; 
		${ ui.message("emr.person.familyName") } <input type="text" name="familyName" value="${account.familyName}" />
		
		<br /><br />
		${ ui.message("Person.gender") }
		<input type="radio" name="gender" value="M" <% if(account.gender == 'M'){ %>checked=checked<% } %> /> ${ ui.message("Person.gender.male") }
		<input type="radio" name="gender" value="F" <% if(account.gender == 'F'){ %>checked=checked<% } %> /> ${ ui.message("Person.gender.female") }
		
		<br /><br />
		<% if(account.user.userId){ %>
		<input type="hidden" name="userId" value="${ account.user.userId }" />
		<% } %>
	</fieldset>
	
	<fieldset>
		<legend>${ ui.message("emr.user.account.details") }</legend>
		<input type="checkbox" name="retired" value="${account.retired}" <% if(!account.retired){ %>checked=checked<% } %> /> ${ ui.message("general.enabled") }
		<br /><br />
		${ ui.message("emr.user.username") } <input type="text" name="username" value="${account.username}" />
		<br /><br /> 
		${ ui.message("emr.user.password") } <input type="text" name="password" value="${account.password}" />
		<br /><br />
		${ ui.message("emr.user.confirmPassword") } <input type="text" name="confirmPassword" value="${account.confirmPassword}" />
	</fieldset>
	
	<fieldset>
		<legend>${ ui.message("emr.provider.details") }</legend>
	</fieldset>
	
	<br /><br />
	<input type="hidden" name="action" value="saveUser" />
	<input type="submit" value="${ ui.message("general.save") }" /> &nbsp;&nbsp;&nbsp;
	<input type="button" value="${ ui.message("general.cancel") }" />
		
</form>