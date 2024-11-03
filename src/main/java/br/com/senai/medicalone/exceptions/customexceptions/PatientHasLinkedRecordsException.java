package br.com.senai.medicalone.exceptions.customexceptions;

public class PatientHasLinkedRecordsException extends RuntimeException {
    public PatientHasLinkedRecordsException(String message) {
        super(message);
    }
}