package org.openmrs.module.emr.fragment.controller.visit;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emr.visit.VisitDomainWrapperFactory;
import org.openmrs.module.emr.visit.VisitDomainWrapperRepository;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.action.FragmentActionResult;
import org.openmrs.ui.framework.fragment.action.SuccessResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

public class QuickVisitFragmentController {

    @Transactional
    public FragmentActionResult create(
            @SpringBean("visitDomainWrapperFactory") VisitDomainWrapperFactory visitWrapperFactory,
            @SpringBean("visitDomainWrapperRepository")VisitDomainWrapperRepository visitWrapperRepository,
            @RequestParam("patientId") Patient patient,
            @RequestParam("locationId") Location location,
            UiUtils uiUtils, HttpServletRequest request) {

        VisitDomainWrapper visitWrapper = visitWrapperFactory.createNewVisit(patient, location, new Date());
        visitWrapperRepository.persist(visitWrapper);

        request.getSession().setAttribute(EmrConstants.SESSION_ATTRIBUTE_INFO_MESSAGE, uiUtils.message("emr.visit.createQuickVisit.successMessage", uiUtils.format(patient)));
        request.getSession().setAttribute(EmrConstants.SESSION_ATTRIBUTE_TOAST_MESSAGE, "true");

        return new SuccessResult();
    }
}
