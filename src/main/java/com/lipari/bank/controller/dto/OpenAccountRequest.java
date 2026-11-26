package com.lipari.bank.controller.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record OpenAccountRequest(
        @NotBlank @Pattern(regexp = "^[A-Z]{2}[0-9]{2}[A-Z0-9]{11,30}$", message = "IBAN non valido") String iban,
        @NotBlank @Size(max = 140) String holderName,
        @NotNull @DecimalMin("0.00") @Digits(integer = 17, fraction = 2) BigDecimal initialBalance,
        @NotBlank @Pattern(regexp = "^[A-Z]{3}$", message = "codice ISO 4217 a 3 lettere") String currency
) {
}
