package org.openmrs.module.emr.page.controller.disposition;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.module.emrapi.disposition.Disposition;
import org.openmrs.module.emrapi.disposition.DispositionDescriptor;
import org.openmrs.module.emrapi.disposition.DispositionService;
import org.openmrs.module.emrapi.disposition.actions.DispositionAction;
import org.openmrs.module.emrapi.encounter.EncounterDomainWrapper;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DispositionActionPageController {


    public String controller(@RequestParam(value = "encounterId", required = false) Encounter encounter,
                           @RequestParam(value = "patientId", required = false) String patientId,
                           @RequestParam(value = "visitId", required = false) String visitId,
                           @SpringBean("dispositionService") DispositionService dispositionService,
                           @SpringBean("applicationContext") ApplicationContext applicationContext,
                           UiUtils ui) throws IOException {


        // fetch the disposition off the obs
        if (encounter != null) {

            Disposition disposition = null;
            Obs dispositionObsGroup = null;

            DispositionDescriptor dispositionDescriptor = dispositionService.getDispositionDescriptor();

            for (Obs obs : encounter.getAllObs()) {
                if (dispositionDescriptor.isDisposition(obs)) {
                    disposition = dispositionService.getDispositionFromObsGroup(obs);
                    dispositionObsGroup = obs;
                }
            }

            // TODO if we actually want to keep this, we should pull this into a DispositionService service method

            if (disposition != null) {
                for (String actionBeanName : disposition.getActions()) {
                    DispositionAction action = applicationContext.getBean(actionBeanName, DispositionAction.class);
                    action.action(new EncounterDomainWrapper(encounter), dispositionObsGroup, null);
                }
            }

        }

        Map<String,Object> params = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(patientId)) {
            params.put("patientId", patientId);
        }
        if (StringUtils.isNotBlank(visitId)) {
            params.put("visitId", visitId);
        }

        return "redirect:" + ui.pageLinkWithoutContextPath("coreapps", "patientdashboard/patientDashboard", params) ;

    }


}
