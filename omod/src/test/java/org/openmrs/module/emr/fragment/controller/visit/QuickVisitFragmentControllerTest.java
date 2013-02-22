package org.openmrs.module.emr.fragment.controller.visit;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emr.visit.VisitDomainWrapper;
import org.openmrs.module.emr.visit.VisitDomainWrapperFactory;
import org.openmrs.module.emr.visit.VisitDomainWrapperRepository;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.fragment.action.FragmentActionResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class QuickVisitFragmentControllerTest {
    
    private QuickVisitFragmentController controller;
    private VisitDomainWrapperFactory visitWrapperFactory;
    private VisitDomainWrapperRepository visitWrapperRepository;
    private UiUtils uiUtils;

    @Before
    public void setUp() {
        controller = new QuickVisitFragmentController();
        visitWrapperFactory = mock(VisitDomainWrapperFactory.class);
        visitWrapperRepository = mock(VisitDomainWrapperRepository.class);
        uiUtils = mock(UiUtils.class);
    }
    
    @Test
    public void shouldCreateNewVisit() throws Exception {
        Patient patient = new Patient();
        Location location = new Location();

        Visit visit = new Visit();
        VisitDomainWrapper visitWrapper = new VisitDomainWrapper(visit);
        when(visitWrapperFactory.createNewVisit(eq(patient), eq(location), any(Date.class))).thenReturn(visitWrapper);

        String successMessage = "Success message";
        String formattedPatient = "Patient name";
        when(uiUtils.format(patient)).thenReturn(formattedPatient);
        when(uiUtils.message("emr.visit.createQuickVisit.successMessage", formattedPatient)).thenReturn(successMessage);

        HttpSession session = mock(HttpSession.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getSession()).thenReturn(session);


        FragmentActionResult result = controller.create(visitWrapperFactory, visitWrapperRepository, patient, location, uiUtils, request);

        verify(visitWrapperRepository).persist(visitWrapper);
        verify(session).setAttribute(EmrConstants.SESSION_ATTRIBUTE_INFO_MESSAGE, successMessage);
        verify(session).setAttribute(EmrConstants.SESSION_ATTRIBUTE_TOAST_MESSAGE, "true");
        assertThat(result, is(CoreMatchers.any(FragmentActionResult.class)));
    }
}
