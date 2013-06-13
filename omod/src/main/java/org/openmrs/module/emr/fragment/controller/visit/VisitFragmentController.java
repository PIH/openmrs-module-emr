package org.openmrs.module.emr.fragment.controller.visit;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emr.EmrContext;
import org.openmrs.module.emr.visit.VisitDomainWrapperFactory;
import org.openmrs.module.emr.visit.VisitDomainWrapperRepository;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.action.FragmentActionResult;
import org.openmrs.ui.framework.fragment.action.SuccessResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

public class VisitFragmentController {

    @Transactional
    public FragmentActionResult start(
            @SpringBean("visitDomainWrapperFactory") VisitDomainWrapperFactory visitWrapperFactory,
            @SpringBean("visitDomainWrapperRepository") VisitDomainWrapperRepository visitWrapperRepository,
            @SpringBean("adtService") AdtService adtService,
            @RequestParam("patientId") Patient patient,
            @RequestParam("locationId") Location location,
            @RequestParam(value = "stopActiveVisit", required = false) Boolean stopActive,
            EmrContext emrContext,
            UiUtils uiUtils, HttpServletRequest request) {

        // if patient has an active visit close it
        if(patient!=null && (emrContext.getActiveVisit()!=null)){
            Visit visit = emrContext.getActiveVisit().getVisit();
            if(visit!=null && stopActive){
                adtService.closeAndSaveVisit(visit);
            }
        }
        // create new visit and save it
        VisitDomainWrapper visitWrapper = visitWrapperFactory.createNewVisit(patient, location, new Date());
        visitWrapperRepository.persist(visitWrapper);

        request.getSession().setAttribute(EmrConstants.SESSION_ATTRIBUTE_INFO_MESSAGE, uiUtils.message("emr.visit.createQuickVisit.successMessage", uiUtils.format(patient)));
        request.getSession().setAttribute(EmrConstants.SESSION_ATTRIBUTE_TOAST_MESSAGE, "true");
        return new SuccessResult();
    }

}
