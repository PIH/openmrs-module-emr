<%
    ui.includeJavascript("emr", "fragments/bootstrap-datetimepicker.min.js")
    ui.includeCss("emr", "datetimepicker.css")

    config.require("id", "concept", "formFieldName", "useTime")

    def concept = conceptService.getConceptByUuid(config.concept)

%>

<p id="${config.id}">
    <label for="${ config.id }-display">${ ui.format(concept) }</label>
    <span id="${ config.id }-wrapper" class="date">
        <input type="text" id="${ config.id }-display" />
        <span class="add-on"><i class="icon-calendar small"></i></span>
    </span>
    <input type="hidden" id="${ config.id }-field" name="${ config.formFieldName }" />
</p>

<script type="text/javascript">
    jq("#${ config.id }-wrapper").datetimepicker({
        minView: 2,
        autoclose: true,
        pickerPosition: "bottom-left",
        format: 'dd/mm/yyyy',
        linkField: "${ config.id }-field",
        linkFormat: "yyyy-mm-dd hh:ii:ss"
    }).on('hide', function(event) {
        var field = jq("#${ config.id }-field");
        field.val('{"concept": "${ concept.getUuid() }", "value": "' + field.val() +'", "datatype": "${ ui.format(concept.getDatatype()) }"}');
    });
</script>