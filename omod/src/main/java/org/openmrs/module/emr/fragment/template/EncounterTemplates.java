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

import java.util.List;

import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.page.PageAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 *
 */
@Component
public class EncounterTemplates {
	
	@Autowired
	private List<EncounterTemplate> encounterTemplates;
	
	public String include(UiUtils ui) throws PageAction {
		StringBuilder out = new StringBuilder();
		
		out.append("<!-- Encounter templates -->\n");
		out.append("<script type=\"text/javascript\">\n");
		out.append("var encounterTemplates;\nvar defaultEncounterTemplate;\njq(function() {\n");
		out.append("	encounterTemplates = {\n");
		List<String> templates = Lists.newArrayList();
		for (EncounterTemplate template : encounterTemplates) {
			if (!template.getSupportedEncounterTypeUuids().isEmpty()) {
				for (String uuid : template.getSupportedEncounterTypeUuids()) {
	                templates.add("		'" + uuid + "': _.template(jq('#" + template.getTemplateId() + "').html())\n");
                }
			}
		}
		out.append(Joiner.on(",").join(templates));
		out.append("	};\n");
		out.append("	defaultEncounterTemplate = _.template(jq('#defaultEncounterTemplate').html());\n");
		out.append("});\n");
		out.append("function displayEncounter(encounter) {\n");
		out.append("	var data = new Object();\n");
		out.append("	data.encounter = encounter;\n");
		out.append("	if (encounterTemplates[encounter.encounterType.uuid]) {\n");
		out.append("		return encounterTemplates[encounter.encounterType.uuid](data);\n");
		out.append("	} else {\n");
		out.append("		return defaultEncounterTemplate(data);\n");
		out.append("	}\n");
		out.append("};\n");
		out.append("</script>\n\n");
		
		for (EncounterTemplate template : encounterTemplates) {
			out.append(ui.includeFragment(template.getTemplateFragmentProviderName(), template.getTemplateFragmentId()));
			out.append("\n");
		}
		
		out.append("<!-- End of encounter templates -->\n");
		
		return out.toString();
	}
}
