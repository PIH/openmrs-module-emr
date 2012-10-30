package org.openmrs.module.emr.adt;

import org.openmrs.Encounter;
import org.openmrs.Visit;
import org.openmrs.module.emr.EmrProperties;

/**
 * Wrapper around a Visit, that provides convenience methods to find particular encounters of interest.
 */
public class VisitSummary {
	
	private Visit visit;
	
	private EmrProperties props;
	
	public VisitSummary(Visit visit, EmrProperties props) {
		this.visit = visit;
		this.props = props;
	}
	
	/**
	 * @return the check-in encounter for this visit, or null if none exists
	 */
	public Encounter getCheckInEncounter() {
		for (Encounter e : visit.getEncounters()) {
			if (props.getCheckInEncounterType().equals(e.getEncounterType()))
				return e;
		}
		return null;
	}
	
	/**
	 * @return the most recent encounter in the visit
	 */
	public Encounter getLastEncounter() {
		if (visit.getEncounters().size() > 0)
			return visit.getEncounters().iterator().next();
		return null;
	}
	
	/**
	 * @return the visit
	 */
	public Visit getVisit() {
		return visit;
	}
	
}
