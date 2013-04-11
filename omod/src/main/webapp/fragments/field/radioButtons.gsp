<%
    config.require("label")
    config.require("formFieldName")
    config.require("options")
%>


<h3>${ config.label }</h3>
<% config.options.each {
    def checked = it.checked || it.value == config.initialValue %>

    <p class="radio-btn">
        <input type="radio" id="${ it.value }-field" name="${ config.formFieldName }" value="${ it.value }" <% if (checked) { %>checked="true"<% } %>/>
        <label for="${ it.value }-field">${ it.label }</label>
    </p>
<% } %>

${ ui.includeFragment("emr", "fieldErrors", [ fieldName: config.formFieldName ]) }
