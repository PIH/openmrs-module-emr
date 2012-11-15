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

package org.openmrs.module.emr.printer.db;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openmrs.module.emr.api.db.hibernate.HibernateSingleClassDAO;
import org.openmrs.module.emr.printer.Printer;

public class HibernatePrinterDAO extends HibernateSingleClassDAO<Printer> implements PrinterDAO {

    public HibernatePrinterDAO() {
        super(Printer.class);
    }

    @Override
    public Printer getPrinterByIpAddress(String ipAddress) {
        Criteria criteria = createPrinterCriteria();
        addIpAddressRestriction(criteria, ipAddress);

        return (Printer) criteria.uniqueResult();
    }

    private Criteria createPrinterCriteria() {
        return sessionFactory.getCurrentSession().createCriteria(Printer.class);
    }

    private void addIpAddressRestriction(Criteria criteria, String ipAddress) {
        criteria.add(Restrictions.eq("ipAddress", ipAddress));
    }
}
