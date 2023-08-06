package com.munsun.application.card2card_project.web;

import com.munsun.application.card2card_project.dto.in.ConfirmTransferDtoIn;
import com.munsun.application.card2card_project.dto.in.TransferInfoDtoIn;
import com.munsun.application.card2card_project.dto.out.SuccessTransferDtoOut;
import com.munsun.application.card2card_project.dto.out.TransferInfoDtoOut;
import com.munsun.application.card2card_project.service.TransferService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Log4j2
@RestController
public class TransferRestController {
    private final TransferService transferService;

    @Autowired
    public TransferRestController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/transfer")
    public SuccessTransferDtoOut sendMoneyToCard(@RequestBody @Valid TransferInfoDtoIn info) throws Exception {
        log.info("POST /transfer");
        return transferService.send(info);
    }

    @PostMapping("/confirmOperation")
    public SuccessTransferDtoOut confirm(@RequestBody @Valid ConfirmTransferDtoIn info) throws Exception {
        log.info("POST /confirmOperation");
        return transferService.confirm(info);
    }

    @GetMapping("/transfer/all")
    public List<TransferInfoDtoOut> getAll() {
        log.info("GET /transfer/all");
        return transferService.getAll();
    }
}
