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

package org.openmrs.module.emr.radiology;

import org.openmrs.Concept;
import org.openmrs.Provider;

import java.util.Date;

public class RadiologyReport {

    public static enum Type { PRELIM, FINAL, CORRECTION }

    private RadiologyOrder associatedRadiologyOrder;

    private Provider principalResultsInterpreter;

    private Concept procedure;

    private Type reportType;

    private String reportBody;

    private Date reportDate;


    public RadiologyOrder getAssociatedRadiologyOrder() {
        return associatedRadiologyOrder;
    }

    public void setAssociatedRadiologyOrder(RadiologyOrder associatedRadiologyOrder) {
        this.associatedRadiologyOrder = associatedRadiologyOrder;
    }

    public Provider getPrincipalResultsInterpreter() {
        return principalResultsInterpreter;
    }

    public void setPrincipalResultsInterpreter(Provider principalResultsInterpreter) {
        this.principalResultsInterpreter = principalResultsInterpreter;
    }

    public Concept getProcedure() {
        return procedure;
    }

    public void setProcedure(Concept procedure) {
        this.procedure = procedure;
    }

    public Type getReportType() {
        return reportType;
    }

    public void setReportType(Type reportType) {
        this.reportType = reportType;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    public String getReportBody() {
        return reportBody;
    }

    public void setReportBody(String reportBody) {
        this.reportBody = reportBody;
    }
}
