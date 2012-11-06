package org.openmrs.module.emr.fragment.controller;

import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

public class FieldErrorsFragmentController {
	
	public void controller(PageModel pageModel, @FragmentParam("fieldName") String fieldName, FragmentModel model,
	                       @SpringBean("messageSourceService") MessageSourceService mss) {
		
		String errorMessage = null;
		if (pageModel.getAttribute("errors") != null) {
			Errors errors = (Errors) pageModel.getAttribute("errors");
			FieldError error = errors.getFieldError(fieldName);
			if (error != null) {
				errorMessage = mss.getMessage(error, Context.getLocale());
			}
		}
		model.addAttribute("errorMessage", errorMessage);
	}
	
}
