package org.openmrs.module.emr.visit;

import org.openmrs.api.db.VisitDAO;
import org.springframework.beans.factory.annotation.Autowired;

public class VisitDomainWrapperRepository {

    @Autowired
    private VisitDAO visitDAO;

    public void persist(VisitDomainWrapper visitWrapper) {
        visitDAO.saveVisit(visitWrapper.getVisit());
    }
}
