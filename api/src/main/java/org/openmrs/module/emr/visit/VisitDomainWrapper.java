package org.openmrs.module.emr.visit;


import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Visit;
import org.openmrs.module.emr.EmrProperties;
import org.openmrs.module.emr.consult.Diagnosis;
import org.openmrs.module.emr.consult.DiagnosisMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class VisitDomainWrapper {

    @Autowired
    @Qualifier("emrProperties")
    EmrProperties emrProperties;

    private Visit visit;

    public VisitDomainWrapper(Visit visit) {
        this.visit = visit;
    }

    public Visit getVisit(){
        return visit;
    }

    public void setEmrProperties(EmrProperties emrProperties) {
        this.emrProperties = emrProperties;
    }

    public int getDifferenceInDaysBetweenCurrentDateAndStartDate() {
        Date today = Calendar.getInstance().getTime();

        Calendar startDateVisit = getStartDateVisit();

        int millisecondsInADay = 1000 * 60 * 60 * 24;

        return (int)( (today.getTime() - startDateVisit.getTimeInMillis()) / millisecondsInADay);
    }

    public List<Diagnosis> getPrimaryDiagnoses() {
        List<Diagnosis> diagnoses = new ArrayList<Diagnosis>();
        DiagnosisMetadata diagnosisMetadata = emrProperties.getDiagnosisMetadata();
        for (Encounter encounter : visit.getEncounters()) {
            if (!encounter.isVoided()) {
                for (Obs obs : encounter.getObsAtTopLevel(false)) {
                    if (diagnosisMetadata.isDiagnosis(obs)) {
                        Diagnosis diagnosis = diagnosisMetadata.toDiagnosis(obs);
                        if (Diagnosis.Order.PRIMARY == diagnosis.getOrder()) {
                            diagnoses.add(diagnosis);
                        }
                    }
                }
            }
        }
        return diagnoses;
    }

    private Calendar getStartDateVisit() {
        Date startDatetime = visit.getStartDatetime();
        Calendar startDateCalendar = Calendar.getInstance();
        startDateCalendar.setTime(startDatetime);
        startDateCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startDateCalendar.set(Calendar.MINUTE, 0);
        startDateCalendar.set(Calendar.SECOND, 0);
        return startDateCalendar;
    }
}
