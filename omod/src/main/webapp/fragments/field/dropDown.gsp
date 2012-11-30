%{--
  - The contents of this file are subject to the OpenMRS Public License
  - Version 1.0 (the "License"); you may not use this file except in
  - compliance with the License. You may obtain a copy of the License at
  - http://license.openmrs.org
  -
  - Software distributed under the License is distributed on an "AS IS"
  - basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
  - License for the specific language governing rights and limitations
  - under the License.
  -
  - Copyright (C) OpenMRS, LLC.  All Rights Reserved.
  --}%

<%
    config.require("formFieldName")
    config.require("options")
%>

<label for="${ config.id }-field">${ config.label ?: '' }</label>


<select id="${ config.id }-field" name="${ config.formFieldName}" />

    <option value="">${ config.emptyOptionLabel ?: ''}</option>

    <% config.options.each {
        def selected = it.selected || it.value == config.initialValue
    %>
        <option value="${ it.value }"  <% if (selected) { %>selected<% } %>/>${ it.label }</option>
    <% } %>

</select>

${ ui.includeFragment("emr", "fieldErrors", [ fieldName: config.formFieldName ]) }

