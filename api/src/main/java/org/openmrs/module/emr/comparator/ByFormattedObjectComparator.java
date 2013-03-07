package org.openmrs.module.emr.comparator;

import java.util.Comparator;

import org.openmrs.api.APIException;
import org.openmrs.ui.framework.UiUtils;

public class ByFormattedObjectComparator implements Comparator<Object> {

    private UiUtils ui;

    public ByFormattedObjectComparator(UiUtils ui) {
        if (ui == null) {
            throw new APIException("ByFormattedObjectComparator must be initialized with UiUtils");
        }

        this.ui = ui;
    }

    @Override
    public int compare(Object o1, Object o2) {
        return ui.format(o1).compareTo(ui.format(o2));
    }
}
