package org.openmrs.module.emr.utils;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.ConceptService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public enum ObservationFactory {
    CODED {
        @Override
        public Obs createObs(ConceptService conceptService, Concept concept, String value) {
            Obs obs = new Obs();
            obs.setConcept(concept);
            obs.setValueCoded(conceptService.getConceptByUuid(value));

            return obs;
        }
    },

    DATE {
        @Override
        public Obs createObs(ConceptService conceptService, Concept concept, String value) {
            try {
                Obs obs = new Obs();
                obs.setConcept(concept);

                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                obs.setValueDate(formatter.parse(value));

                return obs;
            } catch (ParseException e) {
                throw new RuntimeException("Invalid date format to create date observation: " + value, e);
            }
        }
    },

    TEXT {
        @Override
        public Obs createObs(ConceptService conceptService, Concept concept, String value) {
            Obs obs = new Obs();
            obs.setConcept(concept);
            obs.setValueText(value);

            return obs;
        }
    };

    public abstract Obs createObs(ConceptService conceptService, Concept concept, String value);
}
