<%
    ui.decorateWith("emr", "standardEmrPage")
%>

<h3>${ ui.message("emr.editAccount") }</h3>

<form method="post">
	<fieldset>
		<legend>${ ui.message("emr.personDetails") }</legend>
		${ ui.message("emr.person.givenName") } <input type="text" name="person.givenName" value="${user.person.givenName}" /> &nbsp;&nbsp;&nbsp; 
		${ ui.message("emr.person.familyName") } <input type="text" name="person.familyName" value="${user.person.familyName}" />
		
		<br /><br />
		${ ui.message("Person.gender") }
		<input type="radio" name="person.gender" value="M" <% if(user.person.gender == 'M'){ %>checked=checked<% } %> /> ${ ui.message("Person.gender.male") }
		<input type="radio" name="person.gender" value="F" <% if(user.person.gender == 'F'){ %>checked=checked<% } %> /> ${ ui.message("Person.gender.female") }
		
		<br /><br />
		<% if(user.userId){ %>
		<input type="hidden" name="userId" value="${ user.userId }" />
		<% } %>
		<input type="hidden" name="action" value="saveUser" />
		<input type="submit" value="${ ui.message("general.save") }" />
	</fieldset>
</form>