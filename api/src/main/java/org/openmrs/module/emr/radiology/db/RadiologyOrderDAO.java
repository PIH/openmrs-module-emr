package org.openmrs.module.emr.radiology.db;

import org.openmrs.module.emr.api.db.SingleClassDAO;
import org.openmrs.module.emr.radiology.RadiologyOrder;

public interface RadiologyOrderDAO extends SingleClassDAO<RadiologyOrder> {

    public RadiologyOrder getRadiologyOrderByAccessionNumber(String accessionNumber);

}
