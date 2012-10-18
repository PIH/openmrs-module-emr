package org.openmrs.module.emr.page.controller;

import org.openmrs.module.emr.api.PaperRecordService;
import org.openmrs.module.emr.domain.PaperRecordRequest;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;

import java.util.List;

public class ArchivesRoomPageController {

    public void get(PageModel model, @SpringBean PaperRecordService paperRecordService) {
        List<PaperRecordRequest> openPaperRecordRequests = paperRecordService.getOpenPaperRecordRequests();

        model.addAttribute("openRequests", openPaperRecordRequests);
    }
}
