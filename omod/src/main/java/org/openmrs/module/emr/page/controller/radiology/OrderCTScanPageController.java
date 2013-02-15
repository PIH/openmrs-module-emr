package org.openmrs.module.emr.page.controller.radiology;

import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class OrderCTScanPageController {

    public void controller(@RequestParam("patientId") Patient patient,
                           @SpringBean("conceptService") ConceptService conceptService,
                           UiUtils ui,
                           PageModel model) {

        Collection<Provider> providers = Context.getProviderService().getProvidersByPerson(Context.getAuthenticatedUser().getPerson());
        model.addAttribute("currentProvider", providers.iterator().next());

        model.addAttribute("ctScanOrderables", ui.toJson(getCTScanOrderables(conceptService, Context.getLocale())));
        model.addAttribute("patient", patient);
    }

    private List<SimpleObject> getCTScanOrderables(ConceptService conceptService, Locale locale) {
        List<SimpleObject> items = new ArrayList<SimpleObject>();
        Concept ctScanOrderables = conceptService.getConceptByUuid(Context.getAdministrationService().getGlobalProperty(EmrConstants.GP_CT_SCAN_ORDERABLES_CONCEPT));
        for (Concept concept : ctScanOrderables.getSetMembers()) {
            SimpleObject item = new SimpleObject();
            item.put("value", concept.getId());

            // TODO: this should really be fully specified name based on local
            item.put("label", concept.getName().getName());
            items.add(item);
        }
        return items;
    }
}
