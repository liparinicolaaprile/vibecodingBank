package com.lipari.bank.shared.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Logga i parametri di configurazione principali all'avvio del sistema.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BankSystemLifecycle {

    private final LipariBankProperties properties;
    private final Environment environment;

    @PostConstruct
    void logStartupConfiguration() {
        String[] profiles = environment.getActiveProfiles();
        log.info("==================================================");
        log.info(" LipariBank Core Backend - avvio sistema");
        log.info(" Profili attivi       : {}", profiles.length == 0 ? "[default]" : Arrays.toString(profiles));
        log.info(" Codice banca         : {}", properties.bankCode());
        log.info(" Importo max bonifico : {}", properties.maxTransferAmount().toPlainString());
        log.info(" Audit abilitato      : {}", properties.audit().enabled());
        log.info("==================================================");
    }
}
