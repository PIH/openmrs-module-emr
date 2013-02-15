package org.openmrs.module.emr.task;

public enum ExtensionPoint {

    ACTIVE_VISITS("patient.visits.active.actions"),
    GLOBAL_ACTIONS("patient.globalActions");

    private final String value;

    ExtensionPoint(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
