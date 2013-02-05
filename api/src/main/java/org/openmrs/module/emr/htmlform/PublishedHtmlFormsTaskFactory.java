package org.openmrs.module.emr.htmlform;

import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.emr.EmrContext;
import org.openmrs.module.emr.task.TaskDescriptor;
import org.openmrs.module.emr.task.TaskFactory;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class PublishedHtmlFormsTaskFactory implements TaskFactory {

    @Autowired
    @Qualifier("messageSourceService")
    private MessageSourceService messageSourceService;

    @Autowired
    @Qualifier("htmlFormEntryService")
    private HtmlFormEntryService htmlFormEntryService;

    @Override
    public List<TaskDescriptor> getTaskDescriptors(EmrContext emrContext) {
        if (emrContext.getCurrentPatient() == null) {
            return Collections.emptyList();
        }
        List<TaskDescriptor> ret = new ArrayList<TaskDescriptor>();
        if (emrContext.getUserContext().hasPrivilege(PrivilegeConstants.FORM_ENTRY)) {
            for (HtmlForm htmlForm : htmlFormEntryService.getAllHtmlForms()) {
                if (htmlForm.getForm().getPublished()) {
                    EnterHtmlFormWithOwnUiTask taskDescriptor = new EnterHtmlFormWithOwnUiTask() {
                        @Override
                        public boolean isAvailable(EmrContext context) {
                            return true;
                        }
                    };
                    taskDescriptor.setMessageSourceService(messageSourceService);
                    taskDescriptor.setHtmlFormId(htmlForm.getId());
                    taskDescriptor.setTiming(EntryTiming.REAL_TIME_OR_RETROSPECTIVE);
                    ret.add(taskDescriptor);
                }
            }
        }
        return ret;
    }

}
