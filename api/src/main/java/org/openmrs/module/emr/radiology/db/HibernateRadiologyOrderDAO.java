package org.openmrs.module.emr.radiology.db;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openmrs.module.emr.radiology.RadiologyOrder;
import org.openmrs.module.emrapi.db.HibernateSingleClassDAO;

public class HibernateRadiologyOrderDAO extends HibernateSingleClassDAO<RadiologyOrder> implements RadiologyOrderDAO {

    public HibernateRadiologyOrderDAO() {
        super(RadiologyOrder.class);
    }

    @Override
    public RadiologyOrder getRadiologyOrderByAccessionNumber(String accessionNumber) {
        Criteria criteria = createRadiologyOrderCriteria();
        addAccessionNumberRestriction(criteria, accessionNumber);
        return (RadiologyOrder) criteria.uniqueResult();
    }


    private Criteria createRadiologyOrderCriteria() {
        return sessionFactory.getCurrentSession().createCriteria(RadiologyOrder.class);
    }

    private void addAccessionNumberRestriction(Criteria criteria, String accessionNumber) {
        criteria.add(Restrictions.eq("accessionNumber", accessionNumber));
    }
}
