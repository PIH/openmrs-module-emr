package org.openmrs.module.emr.paperrecord;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.emr.EmrProperties;
import org.openmrs.module.emr.printer.Printer;
import org.openmrs.module.emr.printer.PrinterServiceImpl;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Calendar;
import java.util.Locale;

import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class})
public class DefaultZplPaperRecordLabelTemplateTest {

    DefaultZplPaperRecordLabelTemplate template;

    PatientIdentifierType primaryIdentifierType;

    @Before
    public void setup() {

        mockStatic(Context.class);
        when(Context.getLocale()).thenReturn(new Locale("en"));

        MessageSourceService messageSourceService = mock(MessageSourceService.class);
        when(messageSourceService.getMessage(any(String.class))).thenReturn("test");

        EmrProperties emrProperties = mock(EmrProperties.class);
        primaryIdentifierType = new PatientIdentifierType();
        primaryIdentifierType.setUuid("e0987dc0-460f-11e2-bcfd-0800200c9a66");
        when(emrProperties.getPrimaryIdentifierType()).thenReturn(primaryIdentifierType);

        template = new DefaultZplPaperRecordLabelTemplate();
        template.setMessageSourceService(messageSourceService);
        template.setEmrProperties(emrProperties);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testGenerateLabelShouldFailIfPatientHasNoName() {

        Patient patient = new Patient();
        patient.setGender("M");

        PatientIdentifier primaryIdentifier = new PatientIdentifier();
        primaryIdentifier.setIdentifierType(primaryIdentifierType);
        primaryIdentifier.setIdentifier("ABC");
        patient.addIdentifier(primaryIdentifier);

        PaperRecordRequest request = new PaperRecordRequest();
        request.setIdentifier("123");
        request.setPatient(patient);

       template.generateLabel(request);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGenerateLabelShouldFailIfNoPaperRecordIdentifier() {

        Patient patient = new Patient();
        patient.setGender("M");

        PatientIdentifier primaryIdentifier = new PatientIdentifier();
        primaryIdentifier.setIdentifierType(primaryIdentifierType);
        primaryIdentifier.setIdentifier("ABC");
        patient.addIdentifier(primaryIdentifier);

        PersonName personName = new PersonName();
        patient.addName(personName);

        PaperRecordRequest request = new PaperRecordRequest();
        request.setPatient(patient);

        template.generateLabel(request);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGenerateLabelShouldFailIfNoPrimaryIdentifier() {

        Patient patient = new Patient();
        patient.setGender("M");

        PersonName personName = new PersonName();
        patient.addName(personName);

        PaperRecordRequest request = new PaperRecordRequest();
        request.setIdentifier("123");
        request.setPatient(patient);

        String result = template.generateLabel(request);
        Assert.assertTrue(result.contains("123"));
    }

    @Test
    public void testGenerateLabelShouldNotFailWithMinimumValidPatientRecord() {

        Patient patient = new Patient();
        patient.setGender("M");

        PatientIdentifier primaryIdentifier = new PatientIdentifier();
        primaryIdentifier.setIdentifierType(primaryIdentifierType);
        primaryIdentifier.setIdentifier("ABC");
        patient.addIdentifier(primaryIdentifier);

        PersonName personName = new PersonName();
        patient.addName(personName);

        PaperRecordRequest request = new PaperRecordRequest();
        request.setIdentifier("123");
        request.setPatient(patient);

        String result = template.generateLabel(request);
        Assert.assertTrue(result.contains("123"));
        Assert.assertTrue(result.contains("ABC"));
    }


    @Test
    public void testGenerateLabelShouldGenerateLabel() {

        // we aren't testing addresses because mocking the AddressSupport singleton is problematic

        Patient patient = new Patient();
        patient.setGender("F");

        Calendar cal = Calendar.getInstance();
        cal.set(2010, 11, 2);
        patient.setBirthdate(cal.getTime());

        PatientIdentifier primaryIdentifier = new PatientIdentifier();
        primaryIdentifier.setIdentifierType(primaryIdentifierType);
        primaryIdentifier.setIdentifier("ABC");
        patient.addIdentifier(primaryIdentifier);

        PersonName name = new PersonName();
        name.setFamilyName("Jones");
        name.setGivenName("Indiana");
        patient.addName(name);

        PaperRecordRequest request = new PaperRecordRequest();
        request.setIdentifier("123");
        request.setPatient(patient);

        String data = template.generateLabel(request);
        System.out.println(data);
        Assert.assertTrue(data.equals("^XA^CI28^FO140,40^AVN^FDIndiana Jones^FS^FO140,350^ATN^FD02/Dec/2010  ^FS^FO140,400^ATN^FDtest^FS^FO870,50^FB350,1,0,R,0^AVN^FDABC^FS^FO790,250^ATN^BY4^BCN,150^FD123^FS^XZ"));

    }

    // the following test requires that the label printer actually be online and available
    // (and that the ip address and port are set properly)

    @Test
    @Ignore
    public void testPrintingLabel() {

        Patient patient = new Patient();
        patient.setGender("F");

        Calendar cal = Calendar.getInstance();
        cal.set(2010, 11, 2);
        patient.setBirthdate(cal.getTime());

        PatientIdentifier primaryIdentifier = new PatientIdentifier();
        primaryIdentifier.setIdentifierType(primaryIdentifierType);
        primaryIdentifier.setIdentifier("ABC");
        patient.addIdentifier(primaryIdentifier);

        PersonName name = new PersonName();
        name.setFamilyName("Jones");
        name.setGivenName("Indiana");
        patient.addName(name);

        PaperRecordRequest request = new PaperRecordRequest();
        request.setIdentifier("123");
        request.setPatient(patient);

        String data = template.generateLabel(request);

        Printer printer = new Printer();
        printer.setIpAddress("10.3.18.114");
        printer.setPort("9100");

        new PrinterServiceImpl().printViaSocket(data, printer, "UTF-8");


    }

}