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

package org.openmrs.module.emr.ui;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.PatientService;
import org.openmrs.module.emr.EmrContext;
import org.openmrs.ui.framework.fragment.FragmentContext;
import org.openmrs.ui.framework.fragment.FragmentModelConfigurator;
import org.openmrs.ui.framework.fragment.PossibleFragmentActionArgumentProvider;
import org.openmrs.ui.framework.fragment.PossibleFragmentControllerArgumentProvider;
import org.openmrs.ui.framework.page.PageContext;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.page.PageModelConfigurator;
import org.openmrs.ui.framework.page.PossiblePageControllerArgumentProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Makes an EmrContext argument available in PageModel and FragmentModel, and for inject into page and fragment
 * controller methods.
 */
@Component
public class EmrContextArgumentProvider implements PageModelConfigurator, FragmentModelConfigurator,
        PossiblePageControllerArgumentProvider, PossibleFragmentControllerArgumentProvider,
        PossibleFragmentActionArgumentProvider {

    @Autowired
    PatientService patientService;

    @Override
    public void configureModel(PageContext pageContext) {
        // on any page request this method will be called first (before all fragments), so we construct the EmrContext here
        HttpServletRequest request = pageContext.getRequest().getRequest();
        EmrContext emrContext = buildEmrContext(request);
        pageContext.getModel().addAttribute("emrContext", emrContext);
    }

    @Override
    public void configureModel(FragmentContext fragmentContext) {
        // emrContext should already have been set by configureModel(PageContext)
        PageModel pageModel = fragmentContext.getPageContext().getModel();
        EmrContext emrContext = (EmrContext) pageModel.get("emrContext");
        fragmentContext.getModel().addAttribute("emrContext", emrContext);
    }

    @Override
    public void addPossiblePageControllerArguments(Map<Class<?>, Object> possibleArguments) {
        // emrContext should already have been set by configureModel(PageContext)
        PageModel pageModel = (PageModel) possibleArguments.get(PageModel.class);
        EmrContext emrContext = (EmrContext) pageModel.get("emrContext");
        possibleArguments.put(EmrContext.class, emrContext);
    }

    @Override
    public void addPossibleFragmentControllerArguments(Map<Class<?>, Object> possibleArguments) {
        addPossiblePageControllerArguments(possibleArguments);
    }

    @Override
    public void addPossibleFragmentActionArguments(Map<Class<?>, Object> possibleArguments) {
        // this is called from its own HTTP Request (without a page) so we need to construct the emrContext
        HttpServletRequest request = (HttpServletRequest) possibleArguments.get(HttpServletRequest.class);
        EmrContext emrContext = buildEmrContext(request);
        possibleArguments.put(EmrContext.class, emrContext);
    }

    private EmrContext buildEmrContext(HttpServletRequest request) {
        HttpSession session = request.getSession();
        EmrContext emrContext = new EmrContext(session);

        // if the request has a "patientId" or "patient" parameter, apply that
        String patientId = request.getParameter("patientId");
        if (StringUtils.isEmpty(patientId)) {
            patientId = request.getParameter("patient");
        }

        if (StringUtils.isNotEmpty(patientId)) {
            try {
                emrContext.setCurrentPatient(patientService.getPatient(Integer.valueOf(patientId)));
            } catch (Exception ex) {
                // don't fail, even if the patientId or patient parameter isn't as expected
            }
        }
        return emrContext;
    }

}
