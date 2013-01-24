package org.openmrs.module.emr.fragment.controller.visit;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.openmrs.Visit;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.springframework.web.bind.annotation.RequestParam;

public class VisitDetailsFragmentController {

    public SimpleObject getVisitDetails(
            @RequestParam("visitId") Visit visit,
            UiUtils uiUtils) throws ParseException {

        SimpleObject simpleObject = SimpleObject.fromObject(visit, uiUtils, "startDatetime", "stopDatetime", "location",
                "encounters.encounterId", "encounters.encounterDatetime", "encounters.encounterType",
                "encounters.location", "encounters.encounterProviders.provider");

        List<SimpleObject> encounters = (List<SimpleObject>) simpleObject.get("encounters");
        String[] datePatterns = {"dd-MMM-yyyy (HH:mm:ss)"};
        for(SimpleObject so: encounters) {
            Date encounterDatetime = null;
            encounterDatetime = DateUtils.parseDate((String) so.get("encounterDatetime"), datePatterns);
            so.put("encounterDate", DateFormatUtils.format(encounterDatetime, "dd/MM/yyyy"));
            so.put("encounterTime", DateFormatUtils.format(encounterDatetime, "hh:mm a"));
        }

        return simpleObject;
    }
}
