<%
    config.require("label")
    config.require("formFieldName")
    config.require("options")
%>


<h3>${ config.label }</h3>
<% config.options.eachWithIndex { it, idx ->
    def checked = it.checked || it.value == config.initialValue %>

    <p class="radio-btn">
        <input type="radio" id="${ config.id }-${ idx }-field" name="${ config.formFieldName }" value="${ it.value }" <% if (checked) { %>checked="true"<% } %>
            <% if (config.dependency) { %> data-bind="checked: ${ config.dependency }" <% } %>/>
        <label for="${ config.id }-${ idx }-field">${ it.label }</label>
    </p>
<% } %>

<% if (config.dependency) { %>
    <script type="text/javascript">
        viewModel.${ config.dependency } = ko.observable();
    </script>
<% } %>

${ ui.includeFragment("emr", "fieldErrors", [ fieldName: config.formFieldName ]) }
