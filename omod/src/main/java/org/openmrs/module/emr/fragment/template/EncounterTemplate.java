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
package org.openmrs.module.emr.fragment.template;

import java.util.Collections;
import java.util.Set;

import com.google.common.collect.Sets;

/**
 * Beans extending this class will be picked up automatically and used to determine the right
 * template for the given encounter.
 */
public abstract class EncounterTemplate {
	
	private final String templateId;
	
	private final String templateFragmentProviderName;
	
	private final String templateFragmentId;
	
	private final Set<String> supportedEncounterTypeUuids;
	
	/**
	 * @param templateId
	 * @param templateFragmentProviderName
	 * @param templateFragmentId
	 * @param supportedEncounterTypeUuids
	 */
	public EncounterTemplate(String templateId, String templateFragmentProviderName, String templateFragmentId,
	    String... supportedEncounterTypeUuids) {
		this.templateId = templateId;
		this.templateFragmentProviderName = templateFragmentProviderName;
		this.templateFragmentId = templateFragmentId;
		if (supportedEncounterTypeUuids != null) {
			this.supportedEncounterTypeUuids = Sets.newHashSet(supportedEncounterTypeUuids);
		} else {
			this.supportedEncounterTypeUuids = Collections.emptySet();
		}
	}
	
	public String getTemplateId() {
		return templateId;
	}
	
	public String getTemplateFragmentProviderName() {
		return templateFragmentProviderName;
	}
	
	public String getTemplateFragmentId() {
		return templateFragmentId;
	}
	
	public Set<String> getSupportedEncounterTypeUuids() {
		return supportedEncounterTypeUuids;
	}
	
}
