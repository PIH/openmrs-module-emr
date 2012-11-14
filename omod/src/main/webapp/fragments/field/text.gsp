<%
    config.require("label")
    config.require("formFieldName")

    def size = config.size ?: 20
%>

${ config.label }<input type="text" name="${ config.formFieldName }" size="${ size }" value="${ config.initialValue ?: '' }"/>
${ ui.includeFragment("emr", "fieldErrors", [ fieldName: config.formFieldName ]) }
