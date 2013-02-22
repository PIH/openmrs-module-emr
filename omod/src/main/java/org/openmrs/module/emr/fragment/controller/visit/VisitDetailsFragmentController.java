package org.openmrs.module.emr.fragment.controller.visit;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.openmrs.Encounter;
import org.openmrs.Visit;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.EncounterService;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiFrameworkConstants;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class VisitDetailsFragmentController {

    public SimpleObject getVisitDetails(
            @SpringBean("adminService") AdministrationService administrationService,
            @RequestParam("visitId") Visit visit,
            UiUtils uiUtils) throws ParseException {

        SimpleObject simpleObject = SimpleObject.fromObject(visit, uiUtils, "location",
                "encounters.encounterId", "encounters.encounterDatetime", "encounters.encounterType",
                "encounters.location", "encounters.encounterProviders.provider", "encounters.voided");

        Date startDatetime = visit.getStartDatetime();
        Date stopDatetime = visit.getStopDatetime();

        simpleObject.put("startDatetime", DateFormatUtils.format(startDatetime, "dd MMM yyyy hh:mm a"));

        if (stopDatetime!=null){
            simpleObject.put("stopDatetime", DateFormatUtils.format(stopDatetime, "dd MMM yyyy hh:mm a"));
        } else {
            simpleObject.put("stopDatetime", null);
        }

        for (Iterator<SimpleObject> i = ((List<SimpleObject>) simpleObject.get("encounters")).iterator(); i.hasNext(); ) {
            if((Boolean)i.next().get("voided")){
                i.remove();
            }
        }
        List<SimpleObject> encounters = (List<SimpleObject>) simpleObject.get("encounters");
        String[] datePatterns = { administrationService.getGlobalProperty(UiFrameworkConstants.GP_FORMATTER_DATETIME_FORMAT) };
        for(SimpleObject so: encounters) {
            Date encounterDatetime = null;
            encounterDatetime = DateUtils.parseDate((String) so.get("encounterDatetime"), datePatterns);
            so.put("encounterDate", DateFormatUtils.format(encounterDatetime, "dd MMM yyyy"));
            so.put("encounterTime", DateFormatUtils.format(encounterDatetime, "hh:mm a"));
        }

        return simpleObject;
    }

    public SimpleObject deleteEncounter(UiUtils ui,
                                        @RequestParam("encounterId")Encounter encounter,
                                        @SpringBean("encounterService")EncounterService encounterService){

       if(encounter!=null){
           encounterService.voidEncounter(encounter, "delete encounter");
           encounterService.saveEncounter(encounter);
       }
       return SimpleObject.create("message", ui.message("emr.patientDashBoard.deleteEncounter.successMessage"));
    }
}
