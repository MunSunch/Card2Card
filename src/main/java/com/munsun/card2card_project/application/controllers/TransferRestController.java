package com.munsun.card2card_project.application.controllers;

import com.munsun.card2card_project.application.dto.in.ConfirmTransferDtoIn;
import com.munsun.card2card_project.application.dto.in.TransferInfoDtoIn;
import com.munsun.card2card_project.application.dto.out.SuccessTransferDtoOut;
import com.munsun.card2card_project.application.dto.out.TransferInfoDtoOut;
import com.munsun.card2card_project.application.exception.TransferNotFoundException;
import com.munsun.card2card_project.application.service.TransferService;
import com.munsun.card2card_project.application.service.impl.TransferServiceImpl;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@Tag(name="Transfers", description = "Методы для работы с переводами")
@RestController
public class TransferRestController {
    private final TransferServiceImpl transferService;

    @Autowired
    public TransferRestController(TransferServiceImpl transferService) {
        this.transferService = transferService;
    }

    @Operation(summary = "Совершить перевод с карты на карту")
    @PostMapping("/transfer")
    public SuccessTransferDtoOut sendMoneyToCard(@RequestBody @Valid TransferInfoDtoIn info) throws Exception {
        log.info("POST /transfer");
        return transferService.send(info);
    }

    @Operation(summary = "Подтвердить перевод с карты на карту")
    @PostMapping("/confirmOperation")
    public SuccessTransferDtoOut confirm(@RequestBody @Valid ConfirmTransferDtoIn info) throws Exception {
        log.info("POST /confirmOperation");
        return transferService.confirm(info);
    }

    @Hidden
    @GetMapping("/transfers/get")
    public List<TransferInfoDtoOut> getAll() {
        log.info("GET /transfer/all");
        return transferService.getAll();
    }

    @GetMapping("/transfers/get/{id}")
    public TransferInfoDtoOut getById(@PathVariable long id) throws TransferNotFoundException {
        return transferService.findByIdOperation(id);
    }
}
