package org.openmrs.module.emr.visit;

import org.openmrs.ui.framework.SimpleObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper for converting the contents of an encounter to JSON
 */
public class ParsedObs {

    private List<SimpleObject> obs = new ArrayList<SimpleObject>();
    private List<SimpleObject> diagnoses = new ArrayList<SimpleObject>();
    private List<SimpleObject> dispositions = new ArrayList<SimpleObject>();

    public ParsedObs() {
    }

    public List<SimpleObject> getObs() {
        return obs;
    }

    public void setObs(List<SimpleObject> obs) {
        this.obs = obs;
    }

    public List<SimpleObject> getDiagnoses() {
        return diagnoses;
    }

    public void setDiagnoses(List<SimpleObject> diagnoses) {
        this.diagnoses = diagnoses;
    }

    public List<SimpleObject> getDispositions() {
        return dispositions;
    }

    public void setDispositions(List<SimpleObject> dispositions) {
        this.dispositions = dispositions;
    }

}
