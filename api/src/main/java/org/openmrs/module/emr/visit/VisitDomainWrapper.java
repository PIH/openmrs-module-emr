package org.openmrs.module.emr.visit;


import org.openmrs.Visit;

import java.util.Calendar;
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

        Calendar startDateVisit = getStartDateVisit();

        int millisecondsInADay = 1000 * 60 * 60 * 24;

        return (int)( (today.getTime() - startDateVisit.getTimeInMillis()) / millisecondsInADay);
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
