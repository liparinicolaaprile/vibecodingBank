package com.lipari.bank.shared.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

/**
 * Configurazione applicativa LipariBank, mappata dal prefisso {@code liparibank}.
 * <p>
 * Record immutabile: il binding avviene via costruttore e la validazione
 * fa fallire l'avvio se la configurazione e' incompleta o incoerente.
 */
@Validated
@ConfigurationProperties(prefix = "liparibank")
public record LipariBankProperties(

        @NotBlank
        String bankCode,

        @NotNull
        @DecimalMin("0.01")
        BigDecimal maxTransferAmount,

        @Valid
        @DefaultValue
        Audit audit
) {

    public record Audit(
            @DefaultValue("true") boolean enabled
    ) {
    }
}
