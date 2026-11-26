package com.lipari.bank.shared.exception;

/** Violazione di una regola di business (importo oltre soglia, fondi insufficienti, ...). */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
