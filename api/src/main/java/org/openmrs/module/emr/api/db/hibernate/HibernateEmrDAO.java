/**
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
package org.openmrs.module.emr.api.db.hibernate;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptName;
import org.openmrs.ConceptSearchResult;
import org.openmrs.ConceptSource;
import org.openmrs.ConceptWord;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Visit;
import org.openmrs.api.db.hibernate.PatientSearchCriteria;
import org.openmrs.module.emr.EmrProperties;
import org.openmrs.module.emr.api.db.EmrDAO;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class HibernateEmrDAO implements EmrDAO {

    private SessionFactory sessionFactory;
    private EmrProperties emrProperties;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void setEmrProperties(EmrProperties emrProperties) {
        this.emrProperties = emrProperties;
    }

    @Override
    public List<Patient> findPatients(String query, Location checkedInAt, Integer start, Integer maxResults) {

        Criteria criteria;
        if (checkedInAt != null) {
            criteria = sessionFactory.getCurrentSession().createCriteria(Visit.class);
            criteria.setProjection(Property.forName("patient"));
            criteria.add(Restrictions.isNull("stopDatetime"));
            criteria.add(Restrictions.eq("location", checkedInAt));
            Criteria patientCriteria = criteria.createCriteria("patient");
            if (StringUtils.isNotBlank(query)) {
                patientCriteria = buildCriteria(query, patientCriteria);
            }
            criteria = patientCriteria;
        }
        else {
            criteria = sessionFactory.getCurrentSession().createCriteria(Patient.class);
            criteria = buildCriteria(query, criteria);
        }

        if (start != null) {
            criteria.setFirstResult(start);
        }

        if (maxResults != null) {
            criteria.setMaxResults(maxResults);
        }

        return (List<Patient>) criteria.list();
    }

    private Criteria buildCriteria(String query, Criteria criteria) {
        if (query.matches(".*\\d.*")) {
            // has at least one digit, so treat as an identifier
            return new PatientSearchCriteria(sessionFactory, criteria).prepareCriteria(null, query, emrProperties.getIdentifierTypesToSearch(), true, true);
        } else {
            // no digits, so treat as a name
            return new PatientSearchCriteria(sessionFactory, criteria).prepareCriteria(query, null, new ArrayList<PatientIdentifierType>(), true, true);
        }
    }

    @Override
    @Transactional(readOnly=true)
    public List<ConceptSearchResult> conceptSearch(String query, Locale locale, Collection<ConceptClass> classes, Collection<ConceptSource> sources, Integer limit) {
        List<String> uniqueWords = ConceptWord.getUniqueWords(query, locale);
        if (uniqueWords.size() == 0) {
            return Collections.emptyList();
        }

        List<ConceptSearchResult> results = new ArrayList<ConceptSearchResult>();

        // find matches based on name
        {
            Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ConceptName.class);
            criteria.add(Restrictions.eq("voided", false));
            criteria.add(Restrictions.eq("locale", locale));
            criteria.setMaxResults(limit);

            Criteria conceptCriteria = criteria.createCriteria("concept");
            conceptCriteria.add(Restrictions.eq("retired", false));
            if (classes != null) {
                conceptCriteria.add(Restrictions.in("conceptClass", classes));
            }

            for (String word : uniqueWords) {
                criteria.add(Restrictions.ilike("name", word, MatchMode.ANYWHERE));
            }

            Set<Concept> conceptsMatchedByPreferredName = new HashSet<Concept>();
            for (ConceptName matchedName : (List<ConceptName>) criteria.list()) {
                results.add(new ConceptSearchResult(null, matchedName.getConcept(), matchedName, calculateMatchScore(query, uniqueWords, matchedName)));
                if (matchedName.isLocalePreferred()) {
                    conceptsMatchedByPreferredName.add(matchedName.getConcept());
                }
            }

            // don't display synonym matches if the preferred name matches too
            for (Iterator<ConceptSearchResult> i = results.iterator(); i.hasNext(); ) {
                ConceptSearchResult candidate = i.next();
                if (!candidate.getConceptName().isLocalePreferred() && conceptsMatchedByPreferredName.contains(candidate.getConcept())) {
                    i.remove();
                }
            }
        }

        // find matches based on mapping
        if (sources != null) {
            Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ConceptMap.class);
            criteria.setMaxResults(limit);

            Criteria conceptCriteria = criteria.createCriteria("concept");
            conceptCriteria.add(Restrictions.eq("retired", false));
            if (classes != null) {
                conceptCriteria.add(Restrictions.in("conceptClass", classes));
            }

            Criteria mappedTerm = criteria.createCriteria("conceptReferenceTerm");
            mappedTerm.add(Restrictions.eq("retired", false));
            mappedTerm.add(Restrictions.in("conceptSource", sources));
            mappedTerm.add(Restrictions.ilike("code", query, MatchMode.EXACT));

            for (ConceptMap mapping : (List<ConceptMap>) criteria.list()) {
                results.add(new ConceptSearchResult(null, mapping.getConcept(), null, calculateMatchScore(query, mapping)));
            }
        }

        Collections.sort(results, new Comparator<ConceptSearchResult>() {
            @Override
            public int compare(ConceptSearchResult left, ConceptSearchResult right) {
                return right.getTransientWeight().compareTo(left.getTransientWeight());
            }
        });

        if (results.size() > limit) {
            results = results.subList(0, limit);
        }
        return results;
    }

    private Double calculateMatchScore(String query, ConceptMap matchedMapping) {
        // eventually consider weighting this by map type (e.g. same-as > narrower-than > others)
        return 10000d;
    }

    private Double calculateMatchScore(String query, List<String> uniqueWords, ConceptName matchedName) {
        double score = 0d;
        if (query.equalsIgnoreCase(matchedName.getName())) {
            score += 1000d;
        }
        if (matchedName.isLocalePreferred()) {
            score += 500d;
        }
        score -= matchedName.getName().length();
        return score;
    }

}
