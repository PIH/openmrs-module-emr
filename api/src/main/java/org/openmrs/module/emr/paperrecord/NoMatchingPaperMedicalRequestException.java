package org.openmrs.module.emr.paperrecord;

public class NoMatchingPaperMedicalRequestException extends Exception {

    private static final long serialVersionUID = 1L;

    public NoMatchingPaperMedicalRequestException() {
        super();
    }

    public NoMatchingPaperMedicalRequestException(String message) {
        super(message);
    }

    public NoMatchingPaperMedicalRequestException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
