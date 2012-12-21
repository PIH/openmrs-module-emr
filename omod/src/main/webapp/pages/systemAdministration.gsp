<%
    ui.decorateWith("emr", "standardEmrPage", [ title: ui.message("emr.systemAdministration") ])

    ui.includeCss("mirebalais", "systemAdministration.css")
%>

<div id="tasks">
    <a class="button big" href="${ ui.pageLink("emr", "manageAccounts") }">
        <div class="task">
            <i class="icon-book"></i>
            ${ ui.message("emr.task.accountManagement.label") }
        </div>
    </a>
    <a class="button big" href="${ ui.pageLink("emr", "printer/managePrinters") }">
        <div class="task">
            <i class="icon-print"></i>
            ${ ui.message("emr.printer.managePrinters") }
        </div>
    </a>
    <a class="button big" href="${ ui.pageLink("emr", "printer/defaultPrinters") }">
        <div class="task">
            <i class="icon-print"></i>
            ${ ui.message("emr.printer.defaultPrinters") }
        </div>
    </a>
    <a class="button big" href="${ ui.pageLink("emr", "mergePatients") }">
        <div class="task">
            <i class="icon-group"></i>
            ${ ui.message("emr.mergePatients") }
        </div>
    </a>
</div>
