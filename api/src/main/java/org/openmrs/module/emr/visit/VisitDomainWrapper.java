package org.openmrs.module.emr.visit;


import org.openmrs.Visit;

import java.util.Date;

public class VisitDomainWrapper {
    private Visit visit;

    public VisitDomainWrapper(Visit visit) {
        this.visit = visit;
    }

    public Visit getVisit(){
        return visit;
    }

    public int getDifferenceInDaysBetweenCurrentDateAndStartDate() {
        Date today = new Date();
        Date startDatetime = visit.getStartDatetime();

        int millisecondsInADay = 1000 * 60 * 60 * 24;

        return (int)( (today.getTime() - startDatetime.getTime()) / millisecondsInADay);
    }
}
