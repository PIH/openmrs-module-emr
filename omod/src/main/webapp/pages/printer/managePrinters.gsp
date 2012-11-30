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
    ui.decorateWith("emr", "standardEmrPage")
%>

<h3>${  ui.message("emr.printer.managePrinters") }</h3>

<table class="bordered">
    <tr>
        <th>${ ui.message("emr.printer.type") }</th>
        <th>${ ui.message("emr.printer.physicalLocation") }</th>
        <th>${ ui.message("emr.printer.name") }</th>
        <th>${ ui.message("emr.printer.ipAddress") }</th>
        <th>${ ui.message("emr.printer.port") }</th>
        <th>&nbsp;</th>
    </tr>

    <% printers.sort { it.name }.each {   %>
    <tr>
        <td>
            ${ ui.message("emr.printer." + it.type) }
        </td>
        <td>
            ${ ui.format(it.physicalLocation) ?: '&nbsp;'}
        </td>
        <td>
            ${ ui.format(it.name) }
        </td>
        <td>
            ${ ui.format(it.ipAddress) }
        </td>
        <td>
            ${ ui.format(it.port) }
        </td>
        <td>
            <a href="/${ contextPath }/emr/printer/printer.page?printerId=${ it.printerId }">
                <button>${ ui.message("general.edit") }</button>
            </a>
        </td>
    </tr>
    <% } %>
</table>

<a href="/${ contextPath }/emr/printer/printer.page"><button>${ ui.message("emr.printer.add") }</button></a>