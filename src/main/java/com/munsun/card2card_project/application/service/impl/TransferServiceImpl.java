package com.munsun.card2card_project.application.service.impl;

import com.munsun.card2card_project.application.dto.in.ConfirmTransferDtoIn;
import com.munsun.card2card_project.application.dto.in.TransferInfoDtoIn;
import com.munsun.card2card_project.application.dto.out.SuccessTransferDtoOut;
import com.munsun.card2card_project.application.dto.out.TransferInfoDtoOut;
import com.munsun.card2card_project.application.exception.*;
import com.munsun.card2card_project.application.mapping.TransferMapperImpl;
import com.munsun.card2card_project.application.model.ConfirmTransfer;
import com.munsun.card2card_project.application.model.TransferInfo;
import com.munsun.card2card_project.application.repository.CardRepository;
import com.munsun.card2card_project.application.repository.CrudRepository;
import com.munsun.card2card_project.application.repository.TransferRepository;
import com.munsun.card2card_project.application.service.TransferService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class TransferServiceImpl implements TransferService {
    private final TransferRepository transferRepository;
    private final CardRepository cardRepository;
    private final CrudRepository<ConfirmTransfer> confirmRepository;
    private final TransferMapperImpl mapper;

    private static final String DEFAULT_CONFIRM_CODE = "0000";

    @Override
    public SuccessTransferDtoOut send(TransferInfoDtoIn info) throws Exception {
        log.info("send amount to card");
        preCheckTransfer(info);
        TransferInfo result = mapper.map(info);
        postCheckTransfer(result);
        var transfer = transferRepository.add(result);
        log.debug("CardFrom=" + transfer.getCardFrom().getCardNumber() + " " +
                "CardTo=" + transfer.getCardTo().getCardNumber() + " " +
                "Currency=" + transfer.getCurrency() + " " +
                "Value=" + transfer.getValue() + " " +
                "Status=" + transfer.isStatus());
        log.info("send amount to card success");
        return new SuccessTransferDtoOut(transfer.getOperationId());
    }

    private void preCheckTransfer(TransferInfoDtoIn info) throws CardNotFoundException {
        log.info("pre check transfer");
        cardRepository.findCardByNumberAndValidTillAndCvv(info.cardFromNumber(),
                        info.cardFromValidTill(), info.cardFromCVV())
                .orElseThrow(() -> {
                    log.error("Card not found: {}", info.cardFromNumber());
                    return new CardNotFoundException(0L);
                });
        cardRepository.findCardByNumber(info.cardToNumber())
                .orElseThrow(() -> {
                    log.error("Card not found: {}", info.cardToNumber());
                    return new CardNotFoundException(0L);
                });
        log.info("pre check transfer success");
    }

    private void postCheckTransfer(TransferInfo info) throws Exception {
        log.info("post check transfer");
        var cardFrom = info.getCardFrom();
        var cardTo = info.getCardTo();

        if (!cardFrom.getIsActive() || !cardTo.getIsActive()) {
            long idOperation = transferRepository.add(info).getOperationId();
            log.error("Cards are not active: idOperation=" + idOperation);
            throw new CardNotActiveException(idOperation);
        }
        if (!cardFrom.getCurrency().equals(info.getCurrency())
                || !cardTo.getCurrency().equals(info.getCurrency())) {
            long idOperation = transferRepository.add(info).getOperationId();
            log.error("Invalid currency: idOperation=" + idOperation);
            throw new ErrorTransferCurrencyException(idOperation);
        }
        long valueFromAfterTransfer = cardFrom.getValue() - info.getValue() / 100;
        if (valueFromAfterTransfer <= 0) {
            long idOperation = transferRepository.add(info).getOperationId();
            log.error("Invalid transfer value: idOperation=" + idOperation + " value=" + info.getValue());
            throw new NegativeBalanceAfterTransfer(idOperation);
        }
        log.info("post check transfer success");
    }

    @Override
    public SuccessTransferDtoOut confirm(ConfirmTransferDtoIn info) throws Exception {
        log.info("confirm transfer");
        preCheckConfirm(info);
        confirmRepository.add(mapper.map(info));
        if (!DEFAULT_CONFIRM_CODE.equals(info.code())) {
            throw new InvalidConfirmException(Long.parseLong(info.operationId()));
        }
        long idOperation = Long.parseLong(info.operationId());
        executeTransfer(transferRepository.findByIdOperation(idOperation).get());
        log.info("confirm transfer success");
        return new SuccessTransferDtoOut(idOperation);
    }

    private void preCheckConfirm(ConfirmTransferDtoIn info) throws TransferNotFoundException {
        log.info("pre check confirm");
        long idOperation = Long.parseLong(info.operationId());
        transferRepository.findByIdOperation(idOperation)
                .orElseThrow(() -> {
                    log.error("Transfer not found: idOperation={}", idOperation);
                    return new TransferNotFoundException(idOperation);
                });
        log.info("pre check confirm success");
    }

    private void executeTransfer(TransferInfo info) throws CardNotFoundException {
        log.info("execute transfer");
        long valueFromAfterTransfer = info.getCardFrom().getValue() - info.getValue() / 100;
        long valueToAfterTransfer = info.getCardTo().getValue() + info.getValue() / 100;
        cardRepository.setValueCard(info.getCardFrom().getCardNumber(), valueFromAfterTransfer);
        cardRepository.setValueCard(info.getCardTo().getCardNumber(), valueToAfterTransfer);
        info.setStatus(true);
        log.debug("CardFrom=" + info.getCardFrom().getCardNumber() + " " +
                "CardTo=" + info.getCardTo().getCardNumber() + " " +
                "Currency=" + info.getCurrency() + " " +
                "Value=" + info.getValue() + " " +
                "Status=" + info.isStatus());
        log.info("execute transfer success");
    }

    @Override
    public List<TransferInfoDtoOut> getAll() {
        return transferRepository.getAll().stream()
                .map(mapper::map)
                .toList();
    }

    @Override
    public TransferInfoDtoOut findByIdOperation(long id) throws TransferNotFoundException {
        return mapper.map(transferRepository.findByIdOperation(id)
                .orElseThrow(TransferNotFoundException::new));
    }

    public void clear() {
        transferRepository.clear();
        confirmRepository.clear();
    }
}