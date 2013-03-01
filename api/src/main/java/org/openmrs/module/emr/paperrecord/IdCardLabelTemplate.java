package org.openmrs.module.emr.paperrecord;

import org.openmrs.Patient;

public interface IdCardLabelTemplate {

    String generateLabel(Patient patient);

    String getEncoding();

}
