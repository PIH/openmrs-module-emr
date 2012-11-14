<%
    config.require("label")
    config.require("formFieldName")
    config.require("options")
%>

<span class="dropDown-label">${ config.label }</span>

<div>
    <select name="${ config.formFieldName}" />
    <option>${ config.emptyOptionLabel ?: ''}</option>
<% config.options.each {
    def selected = it.selected || it.value == config.initialValue
%>
     <option value="${ it.value }"  <% if (selected) { %>selected<% } %>/>${ it.label }</option>
<% } %>
    </select>
</div>

${ ui.includeFragment("emr", "fieldErrors", [ fieldName: config.formFieldName ]) }
