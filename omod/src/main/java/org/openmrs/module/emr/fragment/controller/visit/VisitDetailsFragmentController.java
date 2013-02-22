package org.openmrs.module.emr.fragment.controller.visit;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Visit;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.EncounterService;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiFrameworkConstants;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.action.FragmentActionResult;
import org.openmrs.ui.framework.fragment.action.SuccessResult;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VisitDetailsFragmentController {

    public SimpleObject getVisitDetails(
            @SpringBean("adminService") AdministrationService administrationService,
            @RequestParam("visitId") Visit visit,
            UiUtils uiUtils) throws ParseException {

        SimpleObject simpleObject = SimpleObject.fromObject(visit, uiUtils, "id", "location");

        Date startDatetime = visit.getStartDatetime();
        Date stopDatetime = visit.getStopDatetime();

        simpleObject.put("startDatetime", DateFormatUtils.format(startDatetime, "dd MMM yyyy hh:mm a"));

        if (stopDatetime!=null){
            simpleObject.put("stopDatetime", DateFormatUtils.format(stopDatetime, "dd MMM yyyy hh:mm a"));
        } else {
            simpleObject.put("stopDatetime", null);
        }

        List<SimpleObject> encounters = new ArrayList<SimpleObject>();
        simpleObject.put("encounters", encounters);

        String[] datePatterns = { administrationService.getGlobalProperty(UiFrameworkConstants.GP_FORMATTER_DATETIME_FORMAT) };
        for (Encounter e : visit.getEncounters()) {
            if (!e.getVoided()) {
                SimpleObject simpleEncounter = SimpleObject.fromObject(e, uiUtils,  "encounterId", "encounterDatetime", "location", "encounterProviders.provider", "voided");

                Date encounterDatetime = DateUtils.parseDate((String) simpleEncounter.get("encounterDatetime"), datePatterns);
                simpleEncounter.put("encounterDate", DateFormatUtils.format(encounterDatetime, "dd MMM yyyy"));
                simpleEncounter.put("encounterTime", DateFormatUtils.format(encounterDatetime, "hh:mm a"));

                EncounterType encounterType = e.getEncounterType();
                simpleEncounter.put("encounterType", SimpleObject.create("uuid", encounterType.getUuid(), "name", uiUtils.format(encounterType)));

                encounters.add(simpleEncounter);
            }
        }

        return simpleObject;
    }

    public FragmentActionResult deleteEncounter(UiUtils ui,
                                        @RequestParam("encounterId")Encounter encounter,
                                        @SpringBean("encounterService")EncounterService encounterService){

       if(encounter!=null){
           encounterService.voidEncounter(encounter, "delete encounter");
           encounterService.saveEncounter(encounter);
       }
       return new SuccessResult(ui.message("emr.patientDashBoard.deleteEncounter.successMessage"));
    }
}
