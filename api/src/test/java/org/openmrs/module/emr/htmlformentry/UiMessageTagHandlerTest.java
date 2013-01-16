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

package org.openmrs.module.emr.htmlformentry;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openmrs.messagesource.MessageSourceService;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
public class UiMessageTagHandlerTest {

    Locale locale;
    MessageSourceService messageSourceService;
    private String messageCodeWithNoArg;
    private String messageCodeWithArg;
    private String translatedMessageWithNoArg;
    private String translatedMessageWithArg;

    @Before
    public void setUp() throws Exception {
        locale = Locale.FRENCH;
        messageCodeWithNoArg = "message.code.noarg";
        messageCodeWithArg = "message.code.arg";
        translatedMessageWithNoArg = "Translated Message";
        translatedMessageWithArg = "Translated Message {0}";

        messageSourceService = mock(MessageSourceService.class);
        when(messageSourceService.getMessage(messageCodeWithNoArg, null, locale)).thenReturn(translatedMessageWithNoArg);
        when(messageSourceService.getMessage(eq(messageCodeWithArg), any(Object[].class), eq(locale))).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocationOnMock) {
                Object[] messageArgs = (Object[]) invocationOnMock.getArguments()[1];
                return translatedMessageWithArg.replace("{0}", messageArgs[0].toString());
            }
        });
    }

    @Test
    public void testGetSubstitutionWithNoArguments() throws Exception {
        Map<String, String> args = new HashMap<String, String>();
        args.put("code", messageCodeWithNoArg);

        UiMessageTagHandler tagHandler = new UiMessageTagHandler(messageSourceService, locale);
        String substitution = tagHandler.getSubstitution(null, null, args);

        assertThat(substitution, is(translatedMessageWithNoArg));
    }

    @Test
    public void testGetSubstitutionWithArguments() throws Exception {
        String argValue = "Arg Value";

        Map<String, String> args = new HashMap<String, String>();
        args.put("code", messageCodeWithArg);
        args.put("arg0", argValue);

        UiMessageTagHandler tagHandler = new UiMessageTagHandler(messageSourceService, locale);
        String substitution = tagHandler.getSubstitution(null, null, args);

        String expected = translatedMessageWithArg.replace("{0}", argValue);
        assertThat(substitution, is(expected));
    }

}
