package org.openmrs.module.emr.paperrecord;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.context.Context;
import org.openmrs.layout.web.address.AddressSupport;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.emr.EmrProperties;
import org.openmrs.module.emr.utils.GeneralUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class DefaultZplPaperRecordLabelTemplate implements PaperRecordLabelTemplate {

    private final Log log = LogFactory.getLog(getClass());

    private MessageSourceService messageSourceService;

    private EmrProperties emrProperties;

    public void setMessageSourceService(MessageSourceService messageSourceService) {
        this.messageSourceService = messageSourceService;
    }

    public void setEmrProperties(EmrProperties emrProperties) {
        this.emrProperties = emrProperties;
    }

    @Override
    public String generateLabel(Patient patient, String paperRecordIdentifier) {

        if (patient.getPersonName() == null) {
            throw new IllegalArgumentException("Patient needs to have at least one name");
        }

        PatientIdentifier primaryIdentifier = patient.getPatientIdentifier(emrProperties.getPrimaryIdentifierType());

        if (primaryIdentifier == null) {
            throw new IllegalArgumentException("No primary identifier for this patient");
        }

        DateFormat df = new SimpleDateFormat("dd/MMM/yyyy", Context.getLocale());

        // TODO: potentially pull this formatting code into a configurable template?
        // build the command to send to the printer -- written in ZPL
        StringBuilder data = new StringBuilder();
        data.append("^XA");
        data.append("^CI28");   // specify Unicode encoding

        /* LEFT COLUMN */

        /* Name (Only print first and last name) */
        data.append("^FO140,40^AVN^FD" + (patient.getPersonName().getGivenName() != null ? patient.getPersonName().getGivenName() : "") + " "
                + (patient.getPersonName().getFamilyName() != null ? patient.getPersonName().getFamilyName() : "") + "^FS");

        /* Address (using address template) */
        if (patient.getPersonAddress() != null) {

            int verticalPosition = 250;

            // print out the address using the layout format
            // first iterate through all the lines in the format
            if (AddressSupport.getInstance().getDefaultLayoutTemplate() != null && AddressSupport.getInstance().getDefaultLayoutTemplate().getLines() != null) {

                List<List<Map<String,String>>> lines = AddressSupport.getInstance().getDefaultLayoutTemplate().getLines();
                ListIterator<List<Map<String,String>>> iter = lines.listIterator();

                while(iter.hasNext()){
                    List<Map<String,String>> line = iter.next();
                    // now iterate through all the tokens in the line and build the string to print
                    StringBuffer output = new StringBuffer();
                    for (Map<String,String> token : line) {
                        // find all the tokens on this line, and then add them to that output line
                        if(token.get("isToken").equals(AddressSupport.getInstance().getDefaultLayoutTemplate().getLayoutToken())) {

                            String property = GeneralUtils.getPersonAddressProperty(patient.getPersonAddress(), token.get("codeName"));

                            if (!StringUtils.isBlank(property)) {
                                output.append(property + ", ");
                            }
                        }
                    }

                    if (output.length() > 2) {
                        // drop the trailing comma and space from the last token on the line
                        output.replace(output.length() - 2, output.length(), "");
                    }

                    if (!StringUtils.isBlank(output.toString())) {
                        data.append("^FO140," + verticalPosition + "^ATN^FD" + output.toString() + "^FS");
                        verticalPosition = verticalPosition + 50;
                    }
                }
            }
            else {
                log.error("Address template not properly configured");
            }
        }

        /* Birthdate */
        if (patient.getBirthdate() != null) {
            data.append("^FO140,140^ATN^FD" + df.format(patient.getBirthdate()) + " " + (patient.getBirthdateEstimated() ? "(*)" : " ") + "^FS");
        }

        /* Gender */
        data.append("^FO140,190^ATN^FD" + messageSourceService.getMessage("emr.gender." + patient.getGender()) + "^FS");

        /* RIGHT COLUMN */

        /* Print the patient's primary identifier */
        data.append("^FO350,140^FB350,1,0,R,0^AVN^FD" + primaryIdentifier.getIdentifier() + "^FS");

        if(StringUtils.isNotBlank(paperRecordIdentifier)){
            /* Print the bar code, based on the primary identifier */
            data.append("^FO780,40^ATN^BY4^BCN,150^FD" + paperRecordIdentifier+ "^FS");    // print barcode & identifier
        }
        /* Print command */
        data.append("^XZ");

        return data.toString();
    }

    @Override
    public String getEncoding() {
        return "UTF-8";
    }
}
