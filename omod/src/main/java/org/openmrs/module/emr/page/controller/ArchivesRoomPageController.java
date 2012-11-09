package org.openmrs.module.emr.page.controller;

import org.openmrs.Person;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.emr.paperrecord.PaperRecordRequest;
import org.openmrs.module.emr.paperrecord.PaperRecordService;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.WebConstants;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

import static org.openmrs.module.emr.EmrConstants.PRIMARY_IDENTIFIER_TYPE;

public class ArchivesRoomPageController {

    public void get(PageModel model,
                    @RequestParam(value="createPaperMedicalRecord", required=false, defaultValue="false") boolean createPaperMedicalRecord,
                    @SpringBean PaperRecordService paperRecordService,
                    @SpringBean("adminService") AdministrationService administrationService) {
        List<PaperRecordRequest> openPaperRecordRequestsToPull = paperRecordService.getOpenPaperRecordRequestsToPull();
        List<PaperRecordRequest> openPaperRecordRequestsToCreate = paperRecordService.getOpenPaperRecordRequestsToCreate();
        List<PaperRecordRequest> assignedPaperRecordRequestsToPull = paperRecordService.getAssignedPaperRecordRequestsToPull();
        String primaryIdentifierType = administrationService.getGlobalProperty(PRIMARY_IDENTIFIER_TYPE);

        model.addAttribute("openRequestsToCreate", openPaperRecordRequestsToCreate);
        model.addAttribute("openRequestsToPull", openPaperRecordRequestsToPull);
        model.addAttribute("assignedRequestsToPull", assignedPaperRecordRequestsToPull);
        model.addAttribute("primaryIdentifierType", primaryIdentifierType);
        model.addAttribute("createPaperMedicalRecord", createPaperMedicalRecord);
    }

    /**
     * Marks the given requests as "Assigned"
     * @param requests
     */
    public String post(@RequestParam("requestId") List<PaperRecordRequest> requests,
                       @RequestParam(value="createPaperMedicalRecord", required=false, defaultValue="false") boolean createPaperMedicalRecord,
                       @RequestParam("assignTo") Person assignTo,
                       @SpringBean PaperRecordService paperRecordService,
                       UiUtils ui,
                       HttpSession session) {
        try {
            paperRecordService.assignRequests(requests, assignTo);
        } catch (IllegalStateException ex) {
            session.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, ui.message("emr.archivesRoom.alreadyAssigned"));
        }
        return "redirect:emr/archivesRoom.page?createPaperMedicalRecord=" + createPaperMedicalRecord;
    }

}
