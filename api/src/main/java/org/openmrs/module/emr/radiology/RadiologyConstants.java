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

public class RadiologyConstants {
    // radiology global properties (will be most likely to be refactored into radiology module)
    public final static String GP_RADIOLOGY_ORDER_ENCOUNTER_TYPE = "emr.radiologyOrderEncounterType";

    public final static String GP_RADIOLOGY_STUDY_ENCOUNTER_TYPE = "emr.radiologyStudyEncounterType";

    public final static String GP_RADIOLOGY_REPORT_ENCOUNTER_TYPE = "emr.radiologyReportEncounterType";

    public final static String GP_RADIOLOGY_TECHNICIAN_ENCOUNTER_ROLE = "emr.radiologyTechnicianEncounterRole";

    public static final String GP_PRIMARY_RESULTS_INTERPRETER_ENCOUNTER_ROLE = "emr.primaryResultsInterpreterEncounterRole";


    public static final String GP_XRAY_ORDERABLES_CONCEPT = "emr.xrayOrderablesConcept";
    public static final String GP_CT_SCAN_ORDERABLES_CONCEPT = "emr.ctScanOrderablesConcept";
    public static final String GP_ULTRASOUND_ORDERABLES_CONCEPT = "emr.ultrasoundOrderablesConcept";
    public static final String GP_RADIOLOGY_TEST_ORDER_TYPE = "emr.radiologyTestOrderType";
}
