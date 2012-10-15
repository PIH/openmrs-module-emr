<%
    config.require("label")
    config.require("formFieldName")
    config.require("options")
%>

<span class="radio-label">${ config.label }</span>
<% config.options.each {
    def checked = it.checked || it.value == config.initialValue
%>

    <div>
    <input type="radio" class="field-value" name="${ config.formFieldName }" value="${ it.value }" <% if (checked) { %>checked="true"<% } %>/>
    <span>${ it.label }</span>
    </div>
<% } %>

<span class="field-error" style="display: none"></span>