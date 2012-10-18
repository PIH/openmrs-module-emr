/*
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

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.module.emr.api.db.PaperRecordRequestDAO;
import org.openmrs.module.emr.domain.PaperRecordRequest;

import java.util.List;

public class HibernatePaperRecordRequestDAO  extends HibernateSingleClassDAO<PaperRecordRequest> implements PaperRecordRequestDAO {

    public HibernatePaperRecordRequestDAO() {
        super(PaperRecordRequest.class);
    }

    @Override
    public List<PaperRecordRequest> getOpenPaperRecordRequests() {

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PaperRecordRequest.class);
        criteria.add(Restrictions.eq("status", PaperRecordRequest.Status.OPEN));
        criteria.addOrder(Order.asc("dateCreated"));

        return (List<PaperRecordRequest>) criteria.list();
    }
}
