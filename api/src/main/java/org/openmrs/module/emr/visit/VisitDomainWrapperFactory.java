package org.openmrs.module.emr.visit;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class VisitDomainWrapperFactory {

    @Autowired
    private EmrApiProperties emrApiProperties;

    public VisitDomainWrapper createNewVisit(Patient patient, Location location, Date visitTime) {
        Visit visit = new Visit();
        visit.setPatient(patient);
        visit.setLocation(getVisitLocation(location));
        visit.setStartDatetime(visitTime);

        visit.setVisitType(emrApiProperties.getAtFacilityVisitType());

        return new VisitDomainWrapper(visit);
    }

    private Location getVisitLocation(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Location does not support visits");
        }
        return location.hasTag(EmrConstants.LOCATION_TAG_SUPPORTS_VISITS) ? location : getVisitLocation(location.getParentLocation());
    }
}
