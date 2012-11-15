<%
    config.require("label")
    config.require("formFieldName")

    def size = config.size ?: 20
%>

<label for="${ config.id }-field">
    ${ config.label }
</label>
<input type="text" id="${ config.id }-field" name="${ config.formFieldName }" size="${ size }" value="${ config.initialValue ?: '' }"/>
${ ui.includeFragment("emr", "fieldErrors", [ fieldName: config.formFieldName ]) }
