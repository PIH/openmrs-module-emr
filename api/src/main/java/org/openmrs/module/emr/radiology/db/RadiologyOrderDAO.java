package org.openmrs.module.emr.radiology.db;

import org.openmrs.module.emr.radiology.RadiologyOrder;
import org.openmrs.module.emrapi.db.SingleClassDAO;

public interface RadiologyOrderDAO extends SingleClassDAO<RadiologyOrder> {

    public RadiologyOrder getRadiologyOrderByAccessionNumber(String accessionNumber);

}
