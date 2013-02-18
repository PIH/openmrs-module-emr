package org.openmrs.module.emr.fragment.controller;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientService;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created with IntelliJ IDEA.
 * User: cospih
 * Date: 2/15/13
 * Time: 4:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class EditPatientIdentifierFragmentController {

    public SimpleObject editPatientIdentifier (UiUtils ui,
                                               @RequestParam("patientId") Patient patient,
                                               @RequestParam("identifierTypeId") PatientIdentifierType identifierType,
                                               @RequestParam(value="identifierValue", required = false) String identifierValue,
                                               @RequestParam("locationId") Location location,
                                               @SpringBean("patientService")PatientService service) {

        if(patient!=null && StringUtils.isNotBlank(identifierValue) && identifierType!=null){
            PatientIdentifier patientIdentifier = patient.getPatientIdentifier(identifierType);
            if(patientIdentifier==null){
                patientIdentifier = new PatientIdentifier(identifierValue, identifierType, location);
            }else{
                patientIdentifier.setIdentifier(identifierValue);
            }
            patient.addIdentifier(patientIdentifier);
            service.savePatient(patient);
        }
        return SimpleObject.create("message", ui.message("emr.patientDashBoard.editPatientIdentifier.successMessage"));
    }
}
