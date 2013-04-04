package org.openmrs.module.emr.page.controller.radiology;

import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emr.radiology.RadiologyConstants;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class OrderUltrasoundPageController {

    public void controller(@RequestParam("patientId") Patient patient,
                           @SpringBean("conceptService") ConceptService conceptService,
                           UiUtils ui,
                           PageModel model) {

        Collection<Provider> providers = Context.getProviderService().getProvidersByPerson(Context.getAuthenticatedUser().getPerson());
        model.addAttribute("currentProvider", providers.iterator().next());

        model.addAttribute("ultrasoundOrderables", ui.toJson(getUltrasoundOrderables(conceptService, Context.getLocale())));
        model.addAttribute("patient", patient);
    }

    private List<SimpleObject> getUltrasoundOrderables(ConceptService conceptService, Locale locale) {
        List<SimpleObject> items = new ArrayList<SimpleObject>();
        Concept ultrasoundOrderables = conceptService.getConceptByUuid(Context.getAdministrationService().getGlobalProperty(RadiologyConstants.GP_ULTRASOUND_ORDERABLES_CONCEPT));
        if( ultrasoundOrderables != null ) {
            for (Concept concept : ultrasoundOrderables.getSetMembers()) {
                SimpleObject item = new SimpleObject();
                item.put("value", concept.getId());

                // TODO: this should really be fully specified name based on local
                item.put("label", concept.getName().getName());
                items.add(item);
            }
        }
        
        return items;
    }
}
