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
    ui.decorateWith("emr", "standardEmrPage", [ title: ui.message("emr.systemAdministration") ])

    ui.includeCss("mirebalais", "systemAdministration.css")
%>

<div id="tasks">
    <a href="${ ui.pageLink("emr", "manageAccounts") }">
        <div class="task">
            <img src="${ ui.resourceLink("uilibrary", "images/address_book_32.png")}"/>
            ${ ui.message("emr.task.accountManagement.label") }
        </div>
    </a>
    <a href="${ ui.pageLink("emr", "printer/managePrinters") }">
        <div class="task">
            <img src="${ ui.resourceLink("uilibrary", "images/printer.gif")}"/>
            ${ ui.message("emr.printer.managePrinters") }
        </div>
    </a>
    <a href="${ ui.pageLink("emr", "printer/defaultPrinters") }">
        <div class="task">
            <img src="${ ui.resourceLink("uilibrary", "images/printer.gif")}"/>
            ${ ui.message("emr.printer.defaultPrinters") }
        </div>
    </a>
    <a href="${ ui.pageLink("emr", "mergePatients") }">
        <div class="task">
            <img src="${ ui.resourceLink("uilibrary", "images/users_32.png")}"/>
            ${ ui.message("emr.mergePatients") }
        </div>
    </a>
</div>
