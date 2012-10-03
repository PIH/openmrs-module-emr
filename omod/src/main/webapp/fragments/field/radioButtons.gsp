<%
    config.require("label")
    config.require("formFieldName")
    config.require("options")

    ui.decorateWith("emr", "labeledField", config)
%>

<% config.options.each {
    def checked = it.checked || it.value == config.initialValue
%>
    <input type="radio" class="field-value" name="${ config.formFieldName }" value="${ it.value }" <% if (checked) { %>checked="true"<% } %>/>
    ${ it.label }
<% } %>

<span class="field-error" style="display: none"></span>