package com.lipari.bank.service;

import com.lipari.bank.model.Account;
import com.lipari.bank.repository.AccountRepository;
import com.lipari.bank.shared.config.LipariBankProperties;
import com.lipari.bank.shared.exception.AccountNotFoundException;
import com.lipari.bank.shared.exception.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {

    private final AccountRepository accountRepository;
    private final LipariBankProperties properties;

    @Transactional
    public Account openAccount(String iban, String holderName, BigDecimal initialBalance, String currency) {
        if (accountRepository.existsByIban(iban)) {
            throw new BusinessRuleException("IBAN gia' registrato: " + iban);
        }
        Account saved = accountRepository.save(new Account(iban, holderName, initialBalance, currency));
        audit("APERTURA_CONTO iban={} titolare={}", iban, holderName);
        return saved;
    }

    public Account getByIban(String iban) {
        return accountRepository.findByIban(iban)
                .orElseThrow(() -> new AccountNotFoundException(iban));
    }

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Transactional
    public void transfer(String fromIban, String toIban, BigDecimal amount) {
        if (fromIban.equals(toIban)) {
            throw new BusinessRuleException("Conto di origine e destinazione coincidono");
        }
        if (amount.compareTo(properties.maxTransferAmount()) > 0) {
            throw new BusinessRuleException("Importo " + amount.toPlainString()
                    + " oltre il massimo consentito di " + properties.maxTransferAmount().toPlainString());
        }

        // Lock acquisiti sempre nello stesso ordine (per IBAN) per evitare deadlock
        // tra bonifici incrociati A->B e B->A.
        boolean fromFirst = fromIban.compareTo(toIban) < 0;
        Account first = lockAccount(fromFirst ? fromIban : toIban);
        Account second = lockAccount(fromFirst ? toIban : fromIban);
        Account from = fromFirst ? first : second;
        Account to = fromFirst ? second : first;

        if (!from.getCurrency().equals(to.getCurrency())) {
            throw new BusinessRuleException("Bonifici tra valute diverse non supportati");
        }
        if (from.getBalance().compareTo(amount) < 0) {
            throw new BusinessRuleException("Fondi insufficienti sul conto " + fromIban);
        }

        from.debit(amount);
        to.credit(amount);
        audit("BONIFICO da={} a={} importo={} {}", fromIban, toIban, amount.toPlainString(), from.getCurrency());
    }

    private Account lockAccount(String iban) {
        return accountRepository.findByIbanForUpdate(iban)
                .orElseThrow(() -> new AccountNotFoundException(iban));
    }

    private void audit(String message, Object... args) {
        if (properties.audit().enabled()) {
            log.info("[AUDIT][" + properties.bankCode() + "] " + message, args);
        }
    }
}
