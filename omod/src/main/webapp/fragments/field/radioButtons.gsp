<%
    config.require("label")
    config.require("formFieldName")
    config.require("options")
%>

<label for="${ config.id }-field">${ config.label }</label>

<% config.options.each {
    def checked = it.checked || it.value == config.initialValue
%>
    <input type="radio" id="${ config.id }-field" class="field-value" name="${ config.formFieldName }" value="${ it.value }" <% if (checked) { %>checked="true"<% } %>/>
    <span>${ it.label }</span>
<% } %>

 ${ ui.includeFragment("emr", "fieldErrors", [ fieldName: config.formFieldName ]) }

