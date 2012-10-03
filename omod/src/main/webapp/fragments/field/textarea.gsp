<%
    config.require("label")
    config.require("formFieldName")

    ui.decorateWith("emr", "labeledField", config)
%>

<textarea class="field-value" name="${ config.formFieldName }">${ config.initialValue ?: "" }</textarea>

<span class="field-error" style="display: none"></span>