<%
    config.require("formFieldName")
    config.require("options")

    def selectDataBind = "";
    if (config.depends && config.depends.disable) {
        selectDataBind += "disable: ${ config.depends.variable }() == '${ config.depends.disable }'"
    }
    if (config.depends && config.depends.enable) {
        selectDataBind += "enable: ${ config.depends.variable }() == '${ config.depends.enable }'"
    }
    if (config.dependency || (config.classes && config.classes.contains("required"))) {
        selectDataBind += ", value: ${ config.observable }"
    }
%>

<p id="${ config.id }"
    <% if (config.depends) { %> data-bind="visible: ${ config.depends.variable }() == '${ config.depends.value }'" <% } %> >

    <label for="${ config.id }-field">
        ${ ui.message(config.label) ?: '' } <% if (config.classes && config.classes.contains("required")) { %><span>(${ ui.message("emr.formValidation.messages.requiredField.label") })</span><% } %>
    </label>

    <select id="${ config.id }-field" name="${ config.formFieldName}"
            <% if (config.classes) { %> class="${ config.classes.join(' ') }" <% } %>
            <% if (config.maximumSize) { %> size="${ [config.maximumSize, config.options.size()].min() }" <% } %>
            <% if (selectDataBind) { %> data-bind="${ selectDataBind }" <% } %> >

        <% if(!config.hideEmptyLabel) { %>
            <option value="">${ ui.message(config.emptyOptionLabel ?: '') }</option>
        <% } %>
        <% config.options.each {
            def selected = it.selected || it.value == config.initialValue
        %>
            <option value="${ it.value }"  <% if (selected) { %>selected<% } %>/>${ it.label }</option>
        <% } %>
    </select>

    ${ ui.includeFragment("uicommons", "fieldErrors", [ fieldName: config.formFieldName ]) }
</p>

<% if (config.dependency || (config.classes && config.classes.contains("required"))) { %>
<script type="text/javascript">
    viewModel.${ config.observable } = ko.observable();
    <% if (config.classes && config.classes.contains("required")) { %>
    viewModel.validations.push(function() {
        return jq('#${ config.id }-field').is(':disabled') || viewModel.${ config.observable }();
    });
    <% } %>
</script>
<% } %>