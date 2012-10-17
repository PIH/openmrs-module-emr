package org.openmrs.module.emr.api.db.hibernate;

import org.hibernate.SessionFactory;
import org.openmrs.module.emr.api.db.PaperRecordRequestDAO;
import org.openmrs.module.emr.domain.PaperRecordRequest;

import java.util.List;

public class HibernatePaperRecordDAO implements PaperRecordRequestDAO {

    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public PaperRecordRequest getById(Integer id) {
        return null;
    }

    @Override
    public List<PaperRecordRequest> getAll() {
        return null;
    }

    @Override
    public PaperRecordRequest saveOrUpdate(PaperRecordRequest object) {
        return null;
    }

    @Override
    public PaperRecordRequest update(PaperRecordRequest object) {
        return null;
    }

    @Override
    public void delete(PaperRecordRequest object) {

    }
}
