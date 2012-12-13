<%
    config.require("label")
    config.require("formFieldName")
%>

<p>
    <label for="${ config.id }-field">
        ${ config.label }
    </label>
    <input type="text" id="${ config.id }-field" name="${ config.formFieldName }" value="${ config.initialValue ?: '' }"/>
    ${ ui.includeFragment("emr", "fieldErrors", [ fieldName: config.formFieldName ]) }
    <% if (config.optional) { %>
        ${ ui.message("emr.optional") }
    <% } %>
</p>