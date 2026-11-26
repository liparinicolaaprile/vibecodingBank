package com.lipari.bank.controller.dto;

import com.lipari.bank.model.Account;

import java.math.BigDecimal;
import java.time.Instant;

public record AccountResponse(
        Long id,
        String iban,
        String holderName,
        BigDecimal balance,
        String currency,
        Instant createdAt
) {

    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getIban(),
                account.getHolderName(),
                account.getBalance(),
                account.getCurrency(),
                account.getCreatedAt());
    }
}
