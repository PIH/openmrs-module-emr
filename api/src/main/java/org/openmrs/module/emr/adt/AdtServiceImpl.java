/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.emr.adt;

import org.apache.commons.lang.time.DateUtils;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.Visit;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.LocationService;
import org.openmrs.api.OrderService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emr.EmrProperties;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


public class AdtServiceImpl extends BaseOpenmrsService implements AdtService {

    @Autowired
    @Qualifier("emrProperties")
    private EmrProperties emrProperties;

    @Autowired
    @Qualifier("adminService")
    private AdministrationService administrationService;

    @Autowired
    @Qualifier("encounterService")
    private EncounterService encounterService;

    @Autowired
    @Qualifier("orderService")
    private OrderService orderService;

    @Autowired
    @Qualifier("visitService")
    private VisitService visitService;

    @Autowired
    @Qualifier("providerService")
    private ProviderService providerService;

    @Autowired
    @Qualifier("locationService")
    private LocationService locationService;

    public void setEmrProperties(EmrProperties emrProperties) {
        this.emrProperties = emrProperties;
    }

    public void setEncounterService(EncounterService encounterService) {
        this.encounterService = encounterService;
    }

    public void setVisitService(VisitService visitService) {
        this.visitService = visitService;
    }

    public void setAdministrationService(AdministrationService administrationService) {
        this.administrationService = administrationService;
    }

    public void setProviderService(ProviderService providerService) {
        this.providerService = providerService;
    }

    @Override
    public boolean isActive(Visit visit) {
        if (visit.getStopDatetime() != null) {
            return false;
        }

        Date now = new Date();
        Date mustHaveSomethingAfter = DateUtils.addHours(now, -emrProperties.getVisitExpireHours());

        if (OpenmrsUtil.compare(visit.getStartDatetime(), mustHaveSomethingAfter) >= 0) {
            return true;
        }

        if (visit.getEncounters() != null) {
            for (Encounter candidate : visit.getEncounters()) {
                if (OpenmrsUtil.compare(candidate.getEncounterDatetime(), mustHaveSomethingAfter) >= 0) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    @Transactional
    public Visit getActiveVisit(Patient patient, Location department) {
        Date now = new Date();

        List<Visit> candidates = visitService.getVisitsByPatient(patient);
        Visit ret = null;
        for (Visit candidate : candidates) {
            if (!isActive(candidate)) {
                if (candidate.getStopDatetime() == null) {
                    candidate.setStopDatetime(guessVisitStopDatetime(candidate));
                    visitService.saveVisit(candidate);
                }
                continue;
            }
            if (isSuitableVisit(candidate, department, now)) {
                ret = candidate;
            }
        }

        return ret;
    }

    @Override
    @Transactional
    public Visit ensureActiveVisit(Patient patient, Location department) {
        Visit activeVisit = getActiveVisit(patient, department);
        if (activeVisit == null) {
            Date now = new Date();
            activeVisit = buildVisit(patient, department, now);
            visitService.saveVisit(activeVisit);
        }
        return activeVisit;
    }

    @Override
    public Date guessVisitStopDatetime(Visit visit) {
        if (visit.getStopDatetime() != null) {
            throw new IllegalStateException("Visit already stopped");
        }
        Encounter latest = null;
        if (visit.getEncounters() != null) {
            for (Encounter candidate : visit.getEncounters()) {
                if (OpenmrsUtil.compareWithNullAsEarliest(candidate.getEncounterDatetime(), latest.getEncounterDatetime()) > 0) {
                    latest = candidate;
                }
            }
        }
        Date lastKnownDate = latest == null ? visit.getStartDatetime() : latest.getEncounterDatetime();
        return lastKnownDate;
    }

    @Override
    @Transactional
    public Encounter checkInPatient(Patient patient, Location where, Provider checkInClerk,
                                    List<Obs> obsForCheckInEncounter, List<Order> ordersForCheckInEncounter) {
        if (checkInClerk == null) {
            checkInClerk = getProvider(Context.getAuthenticatedUser());
        }
        Visit activeVisit = ensureActiveVisit(patient, where);

        if (activeVisit.getEncounters() != null) {
            for (Encounter encounter : activeVisit.getEncounters()) {
                if (encounter.getEncounterType().equals(emrProperties.getCheckInEncounterType())) {
                    // TODO verify this is the behavior we want
                    throw new IllegalStateException("Already checked in: encounterId = " + encounter.getEncounterId());
                }
            }
        }

        Encounter encounter = buildEncounter(emrProperties.getCheckInEncounterType(), patient, where, new Date(), obsForCheckInEncounter, ordersForCheckInEncounter);
        encounter.addProvider(emrProperties.getCheckInClerkEncounterRole(), checkInClerk);
        activeVisit.addEncounter(encounter);
        encounterService.saveEncounter(encounter);
        return encounter;
    }

    private Provider getProvider(User accountBelongingToUser) {
        Collection<Provider> candidates = providerService.getProvidersByPerson(accountBelongingToUser.getPerson());
        if (candidates.size() == 0) {
            throw new IllegalStateException("User " + accountBelongingToUser.getUsername() + " does not have a Provider account");
        } else if (candidates.size() > 1) {
            throw new IllegalStateException("User " + accountBelongingToUser.getUsername() + " has more than one Provider account");
        } else {
            return candidates.iterator().next();
        }
    }

    private Encounter buildEncounter(EncounterType encounterType, Patient patient, Location location, Date when, List<Obs> obsToCreate, List<Order> ordersToCreate) {
        Encounter encounter = new Encounter();
        encounter.setPatient(patient);
        encounter.setEncounterType(encounterType);
        encounter.setLocation(location);
        encounter.setEncounterDatetime(when);
        if (obsToCreate != null) {
            for (Obs obs : obsToCreate) {
                encounter.addObs(obs);
            }
        }
        if (ordersToCreate != null) {
            for (Order order : ordersToCreate) {
                encounter.addOrder(order);
            }
        }
        return encounter;
    }

    private Visit buildVisit(Patient patient, Location location, Date when) {
        Visit visit = new Visit();
        visit.setPatient(patient);
        visit.setLocation(getLocationThatSupportsVisits(location));
        visit.setStartDatetime(when);
        visit.setVisitType(emrProperties.getAtFacilityVisitType());
        return visit;
    }

    /**
     * Looks at location, and if necessary its ancestors in the location hierarchy, until it finds one tagged with
     * "Visit Location"
     * @param location
     * @return location, or an ancestor
     * @throws IllegalArgumentException if neither location nor its ancestors support visits
     */
    @Override
    public Location getLocationThatSupportsVisits(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Location does not support visits");
        } else if (location.hasTag(EmrConstants.LOCATION_TAG_SUPPORTS_VISITS)) {
            return location;
        } else {
            return getLocationThatSupportsVisits(location.getParentLocation());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Location> getAllLocationsThatSupportVisits() {
        return locationService.getLocationsByTag(emrProperties.getSupportsVisitsLocationTag());
    }

    /**
     * @param visit
     * @param location
     * @param when
     * @return true if when falls in the visits timespan AND location is within visit.location
     */
    @Override
    public boolean isSuitableVisit(Visit visit, Location location, Date when) {
        if (OpenmrsUtil.compare(when, visit.getStartDatetime()) < 0) {
            return false;
        }
        if (OpenmrsUtil.compareWithNullAsLatest(when, visit.getStopDatetime()) > 0) {
            return false;
        }
        return isSameOrAncestor(visit.getLocation(), location);
    }

    /**
     * @param a
     * @param b
     * @return true if a.equals(b) or a is an ancestor of b.
     */
    private boolean isSameOrAncestor(Location a, Location b) {
        if (b == null) {
            return a == null;
        }
        return a.equals(b) || isSameOrAncestor(a, b.getParentLocation());
    }
	
	/**
	 * @see org.openmrs.module.emr.adt.AdtService#getActiveVisitSummaries(org.openmrs.Location)
	 */
	@Override
	public List<VisitSummary> getActiveVisitSummaries(Location location) {
		if(location == null){
			throw new IllegalArgumentException("Location is required");
		}
		Set<Location> locations = getChildLocationsRecursively(location, null);
		List<Visit> candidates = visitService.getVisits(null, null, locations, null, null, null, null, null, null, false,
		    false);
		
		List<VisitSummary> active = new ArrayList<VisitSummary>();
		for (Visit candidate : candidates) {
			if (isActive(candidate)) {
				active.add(new VisitSummary(candidate, emrProperties));
			}
		}
		
		return active;
	}
	
	/**
	 * Utility method that returns all child locations and children of its child locations
	 * recursively
	 * 
	 * @param location
	 * @param foundLocations
	 * @return
	 */
	private Set<Location> getChildLocationsRecursively(Location location, Set<Location> foundLocations) {
		if (foundLocations == null)
			foundLocations = new LinkedHashSet<Location>();

        foundLocations.add(location);

		if (location.getChildLocations() != null) {
			for (Location l : location.getChildLocations()) {
				foundLocations.add(l);
				getChildLocationsRecursively(l, foundLocations);
			}
		}
		
		return foundLocations;
	}

}
