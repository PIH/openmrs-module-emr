<%
    config.require("label")
    config.require("formFieldName")
%>

<p <% if (config.left) { %> class="left" <% } %> >
    <label for="${ config.id }-field">
        ${ config.label } <% if (config.mandatory) { %>*<% } %>
    </label>
    <input type="text" id="${ config.id }-field" name="${ config.formFieldName }" value="${ config.initialValue ?: '' }"/>
    ${ ui.includeFragment("emr", "fieldErrors", [ fieldName: config.formFieldName ]) }
    <% if (config.optional) { %>
        ${ ui.message("emr.optional") }
    <% } %>
</p>