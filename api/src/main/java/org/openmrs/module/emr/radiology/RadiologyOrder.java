package org.openmrs.module.emr.radiology;

import org.openmrs.Location;
import org.openmrs.TestOrder;

public class RadiologyOrder extends TestOrder {

    private Location examLocation;

    public Location getExamLocation() {
        return examLocation;
    }

    public void setExamLocation(Location examLocation) {
        this.examLocation = examLocation;
    }
}
