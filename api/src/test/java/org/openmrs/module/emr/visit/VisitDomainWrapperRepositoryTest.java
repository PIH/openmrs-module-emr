package org.openmrs.module.emr.visit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.Visit;
import org.openmrs.api.db.VisitDAO;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class VisitDomainWrapperRepositoryTest {

    @InjectMocks
    private VisitDomainWrapperRepository repository;
    @MockitoAnnotations.Mock
    private VisitDAO visitDAO;

    @Before
    public void setUp() {
        repository = new VisitDomainWrapperRepository();
        visitDAO = mock(VisitDAO.class);

        initMocks(this);
    }



    @Test
    public void shouldPersistAVisitDomainWrapper() throws Exception {
        Visit visit = new Visit();
        VisitDomainWrapper wrapper = new VisitDomainWrapper(visit);

        repository.persist(wrapper);

        verify(visitDAO).saveVisit(visit);
    }


}
