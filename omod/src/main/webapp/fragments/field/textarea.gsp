<%
    config.require("label")
    config.require("formFieldName")

    ui.decorateWith("emr", "labeledField", config)

    def rows = config.rows ?: 5;
    def cols = config.cols ?: 60;
%>

<textarea class="field-value" rows="${ rows }" cols="${ cols }" name="${ config.formFieldName }">${ config.initialValue ?: "" }</textarea>

<span class="field-error" style="display: none"></span>