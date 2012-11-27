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
    <a href="${ ui.pageLink("emr", "mergePatients") }">
        <div class="task">
            <img src="${ ui.resourceLink("uilibrary", "images/users_32.png")}"/>
            ${ ui.message("emr.mergePatients") }
        </div>
    </a>
</div>
