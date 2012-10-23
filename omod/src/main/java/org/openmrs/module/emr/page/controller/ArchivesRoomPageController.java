package org.openmrs.module.emr.page.controller;

import org.openmrs.Person;
import org.openmrs.module.emr.paperrecord.PaperRecordService;
import org.openmrs.module.emr.paperrecord.PaperRecordRequest;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.WebConstants;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

public class ArchivesRoomPageController {

    public void get(PageModel model, @SpringBean PaperRecordService paperRecordService) {
        List<PaperRecordRequest> openPaperRecordRequests = paperRecordService.getOpenPaperRecordRequests();

        model.addAttribute("openRequests", openPaperRecordRequests);
    }

    /**
     * Marks the given requests as "Assigned"
     * @param requests
     */
    public String post(@RequestParam("requestId") List<PaperRecordRequest> requests,
                       @RequestParam("assignTo") Person assignTo,
                       @SpringBean PaperRecordService paperRecordService,
                       UiUtils ui,
                       HttpSession session) {
        try {
            paperRecordService.assignRequests(requests, assignTo);
        } catch (IllegalStateException ex) {
            session.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, ui.message("emr.pullRecords.alreadyAssigned"));
        }
        return "redirect:emr/archivesRoom.page";
    }

}