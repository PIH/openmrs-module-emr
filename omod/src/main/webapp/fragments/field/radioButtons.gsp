<%
    config.require("label")
    config.require("formFieldName")
    config.require("options")
%>

<p>
    <strong>${ config.label }</strong>
</p>

<% config.options.each {
    def checked = it.checked || it.value == config.initialValue %>

    <p>
        <input type="radio" id="${ it.value }-field" name="${ config.formFieldName }" value="${ it.value }" <% if (checked) { %>checked="true"<% } %>/>
        <label for="${ it.value }-field">${ it.label }</label>
    </p>
<% } %>

${ ui.includeFragment("emr", "fieldErrors", [ fieldName: config.formFieldName ]) }
