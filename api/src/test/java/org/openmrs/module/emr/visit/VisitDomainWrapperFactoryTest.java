package org.openmrs.module.emr.visit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.*;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emr.EmrProperties;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class VisitDomainWrapperFactoryTest {

    @InjectMocks
    private VisitDomainWrapperFactory factory;
    @MockitoAnnotations.Mock
    private EmrProperties emrProperties;

    @Before
    public void setUp() {
        factory = new VisitDomainWrapperFactory();
        emrProperties = mock(EmrProperties.class);
        initMocks(this);
    }

    @Test
    public void shouldCreateWrapperForNewVisit() throws Exception {
        VisitType visitType = new VisitType();
        when(emrProperties.getAtFacilityVisitType()).thenReturn(visitType);

        Patient patient = new Patient();
        Location parentLocation = new Location();
        LocationTag locationTag = new LocationTag(); locationTag.setName(EmrConstants.LOCATION_TAG_SUPPORTS_VISITS);
        parentLocation.addTag(locationTag);
        Location location = new Location();
        location.setParentLocation(parentLocation);
        Date visitTime = new Date();
        VisitDomainWrapper visitWrapper = factory.createNewVisit(patient, location, visitTime);

        Visit visit = visitWrapper.getVisit();
        assertThat(visit.getId(), is(nullValue()));
        assertThat(visit.getPatient(), is(patient));
        assertThat(visit.getLocation(), is(parentLocation));
        assertThat(visit.getVisitType(), is(visitType));
        assertThat(visit.getStartDatetime(), is(visitTime));
    }
}
