package com.lipari.bank.controller;

import com.lipari.bank.controller.dto.AccountResponse;
import com.lipari.bank.controller.dto.OpenAccountRequest;
import com.lipari.bank.controller.dto.TransferRequest;
import com.lipari.bank.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Conti", description = "Gestione conti e bonifici")
@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "Apre un nuovo conto")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse open(@Valid @RequestBody OpenAccountRequest request) {
        return AccountResponse.from(accountService.openAccount(
                request.iban(), request.holderName(), request.initialBalance(), request.currency()));
    }

    @Operation(summary = "Elenca tutti i conti")
    @GetMapping
    public List<AccountResponse> list() {
        return accountService.findAll().stream().map(AccountResponse::from).toList();
    }

    @Operation(summary = "Dettaglio conto per IBAN")
    @GetMapping("/{iban}")
    public AccountResponse get(@PathVariable String iban) {
        return AccountResponse.from(accountService.getByIban(iban));
    }

    @Operation(summary = "Esegue un bonifico tra due conti")
    @PostMapping("/transfers")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void transfer(@Valid @RequestBody TransferRequest request) {
        accountService.transfer(request.fromIban(), request.toIban(), request.amount());
    }
}
