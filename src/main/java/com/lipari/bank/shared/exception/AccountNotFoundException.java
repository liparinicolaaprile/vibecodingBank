package com.lipari.bank.shared.exception;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String iban) {
        super("Conto non trovato: " + iban);
    }
}
