package org.openmrs.module.emr.api.impl;

import org.openmrs.Concept;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.emr.EMRConstants;
import org.openmrs.module.emr.api.RadiologyService;
import org.openmrs.module.emr.utils.GlobalPropertyUtils;

import java.util.Date;
import java.util.List;

public class RadiologyServiceImpl extends BaseOpenmrsService implements RadiologyService {

    @Override
    public List<Concept> getRadiologyOrderables() {
        List<Concept> orderables = GlobalPropertyUtils.getGlobalPropertyAsConceptList(EMRConstants.RADIOLOGY_ORDERABLE_CONCEPTS_GP);
        return orderables;
    }

    @SuppressWarnings("deprecation")
    @Override
    public Order placeRadiologyOrder(Patient p, Concept orderable) {
        Order order = new Order();
        order.setPatient(p);
        order.setConcept(orderable);
        order.setOrderType(getRadiologyOrderType());
        order.setStartDate(new Date());
        order.setOrderer(Context.getAuthenticatedUser());
        return Context.getOrderService().saveOrder(order);
    }


    /**
     * Utility methods
     */
    @SuppressWarnings("deprecation")
    private OrderType getRadiologyOrderType() {
        return GlobalPropertyUtils.getGlobalPropertyAsOrderType(EMRConstants.RADIOLOGY_ORDERTYPE_GP);
    }
}
