<%
    ui.includeJavascript("emr", "fragments/datetimepicker/bootstrap-datetimepicker.min.js")
    ui.includeJavascript("emr", "fragments/datetimepicker/locales/bootstrap-datetimepicker.${ emrContext.getUserContext().getLocale() }.js")
    ui.includeCss("emr", "datetimepicker.css")

    config.require("id", "concept", "formFieldName", "useTime")

    def concept = conceptService.getConceptByUuid(config.concept)

    def required = config.classes && config.classes.contains("required");
%>

<p id="${config.id}">
    <label for="${ config.id }-display">
        ${ ui.format(concept) } <% if (required) { %><span>(${ ui.message("emr.formValidation.messages.requiredField.label") })</span><% } %>
    </label>
    <span id="${ config.id }-wrapper" class="date">
        <input type="text" id="${ config.id }-display" />
        <span class="add-on"><i class="icon-calendar small"></i></span>
    </span>
    <input type="hidden" id="${ config.id }-field" name="${ config.formFieldName }[${ config.concept }]"
        <% if (config.classes) { %> class="${ config.classes.join(' ') }" <% } %>
        <% if (config.dependency || required) { %> data-bind="value: ${ config.observable }" <% } %> />

    ${ ui.includeFragment("uicommons", "fieldErrors", [ fieldName: config.formFieldName ]) }
</p>

<script type="text/javascript">
    jq("#${ config.id }-wrapper").datetimepicker({
        minView: 2,
        autoclose: true,
        pickerPosition: "bottom-left",
        language: "${ emrContext.getUserContext().getLocale() }",
        format: 'dd/mm/yyyy',
        linkField: "${ config.id }-field",
        linkFormat: "yyyy-mm-dd hh:ii:ss"
    });

    <% if (config.dependency || required) { %>
        viewModel.${ config.observable } = ko.observable();
        <% if (required) { %>
        viewModel.validations.push(function() {
            return viewModel.${ config.observable }() !== undefined;
        });
        <% } %>
    <% } %>
</script>
