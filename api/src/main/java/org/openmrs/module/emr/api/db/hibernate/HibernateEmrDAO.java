/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.emr.api.db.hibernate;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Visit;
import org.openmrs.api.db.hibernate.PatientSearchCriteria;
import org.openmrs.module.emr.api.db.EmrDAO;
import org.openmrs.module.emrapi.EmrApiProperties;

import java.util.ArrayList;
import java.util.List;

public class HibernateEmrDAO implements EmrDAO {

    private SessionFactory sessionFactory;
    private EmrApiProperties emrApiProperties;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void setEmrApiProperties(EmrApiProperties emrApiProperties) {
        this.emrApiProperties = emrApiProperties;
    }

    @Override
    public List<Patient> findPatients(String query, Location checkedInAt, Integer start, Integer maxResults) {

        Criteria criteria;
        if (checkedInAt != null) {
            criteria = sessionFactory.getCurrentSession().createCriteria(Visit.class);
            criteria.setProjection(Property.forName("patient"));
            criteria.add(Restrictions.isNull("stopDatetime"));
            criteria.add(Restrictions.eq("location", checkedInAt));
            Criteria patientCriteria = criteria.createCriteria("patient");
            if (StringUtils.isNotBlank(query)) {
                patientCriteria = buildCriteria(query, patientCriteria);
            }
            criteria = patientCriteria;
        }
        else {
            criteria = sessionFactory.getCurrentSession().createCriteria(Patient.class);
            criteria = buildCriteria(query, criteria);
        }

        if (start != null) {
            criteria.setFirstResult(start);
        }

        if (maxResults != null) {
            criteria.setMaxResults(maxResults);
        }

        return (List<Patient>) criteria.list();
    }

    @Override
    public List<Object[]> getInpatientsList(Location location) {
        EncounterType admissionEncounterType = emrApiProperties.getAdmissionEncounterType();
        EncounterType dischargeEncounterType = emrApiProperties.getExitFromInpatientEncounterType();
        EncounterType transferEncounterType = emrApiProperties.getTransferWithinHospitalEncounterType();
        PatientIdentifierType primaryIdentifierType = emrApiProperties.getPrimaryIdentifierType();

        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("select distinct v.patient_id as patientId " +
                    ", id1.identifier as primaryIdentifierType" +
                    ", n.given_name as firstName, n.family_name as lastName" +
                    ", admission.encounter_datetime as admissionDateTime, al.name as admissionLocation" +
                    ", mostRecentAdt.encounter_datetime as mostRecentAdtDateTime , tl.name as currentWard" +
                " from visit v inner join person_name n on n.person_id = v.patient_id" +
                " LEFT JOIN patient_identifier as id1" +
                    " ON (v.patient_id = id1.patient_id" +
                    " and id1.identifier_type = :primaryIdentifierType)" +
                " inner join encounter admission" +
                    " on v.visit_id = admission.visit_id" +
                    " and admission.voided = false" +
                    " and admission.encounter_type = :admissionEncounterType" +
                    " and admission.encounter_datetime = ( " +
                        " select max(encounter_datetime)" +
                        " from encounter" +
                        " where visit_id = v.visit_id" +
                        " and voided = false" +
                        " and encounter_type = :admissionEncounterType" +
                    " )" +
                " inner join encounter mostRecentAdt" +
                    " on v.visit_id = mostRecentAdt.visit_id" +
                    " and mostRecentAdt.voided = false" +
                    " and mostRecentAdt.encounter_type in (:adtEncounterTypes) " +
                    " and mostRecentAdt.date_created = (" +
                        " select max(date_created) from encounter" +
                        " where visit_id = v.visit_id and voided = false" +
                        " and encounter_type in (:adtEncounterTypes) " +
                    " )" +
                " left join location as al" +
                    " on (admission.location_id = al.location_id)" +
                " left join location as tl" +
                    " on (mostRecentAdt.location_id = tl.location_id)" +
                " where v.voided = false and v.location_id = :visitLocation" +
                " and v.date_stopped is null and mostRecentAdt.encounter_type in (:admitOrTransferEncounterTypes)" +
                " order by admission.encounter_datetime desc; ");

        query.setInteger("visitLocation", location.getId());
        query.setInteger("primaryIdentifierType", primaryIdentifierType.getId());
        query.setInteger("admissionEncounterType", admissionEncounterType.getId());
        query.setParameterList("adtEncounterTypes", new Integer[]{admissionEncounterType.getId(), dischargeEncounterType.getId(), transferEncounterType.getId()});
        query.setParameterList("admitOrTransferEncounterTypes", new Integer[] { admissionEncounterType.getId(), transferEncounterType.getId() });

        List<Object[]> inpatients = query.list();
        return inpatients;
    }

    private Criteria buildCriteria(String query, Criteria criteria) {
        if (query.matches(".*\\d.*")) {
            // has at least one digit, so treat as an identifier
            return new PatientSearchCriteria(sessionFactory, criteria).prepareCriteria(null, query, emrApiProperties.getIdentifierTypesToSearch(), true, true, true);
        } else {
            // no digits, so treat as a name
            return new PatientSearchCriteria(sessionFactory, criteria).prepareCriteria(query, null, new ArrayList<PatientIdentifierType>(), true, true, true);
        }
    }

}
