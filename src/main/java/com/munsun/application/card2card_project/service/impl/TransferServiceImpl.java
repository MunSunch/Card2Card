package com.munsun.application.card2card_project.service.impl;

import com.munsun.application.card2card_project.dto.in.ConfirmTransferDtoIn;
import com.munsun.application.card2card_project.dto.in.TransferInfoDtoIn;
import com.munsun.application.card2card_project.dto.out.SuccessTransferDtoOut;
import com.munsun.application.card2card_project.dto.out.TransferInfoDtoOut;
import com.munsun.application.card2card_project.exception.*;
import com.munsun.application.card2card_project.model.ConfirmTransfer;
import com.munsun.application.card2card_project.model.TransferInfo;
import com.munsun.application.card2card_project.repository.CardRepository;
import com.munsun.application.card2card_project.repository.CrudRepository;
import com.munsun.application.card2card_project.repository.TransferRepository;
import com.munsun.application.card2card_project.repository.impl.CardRepositoryImpl;
import com.munsun.application.card2card_project.repository.impl.ConfirmRepositoryImpl;
import com.munsun.application.card2card_project.repository.impl.TransferRepositoryImpl;
import com.munsun.application.card2card_project.service.TransferService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TransferServiceImpl implements TransferService {
    private final TransferRepository transferRepository;
    private final CardRepository cardRepository;
    private final CrudRepository<ConfirmTransfer> confirmRepository;
    private final ModelMapper mapper;

    private static final String DEFAULT_CONFIRM_CODE = "0000";

    @Autowired
    public TransferServiceImpl(TransferRepository transferRepository, CardRepository cardRepository, CrudRepository<ConfirmTransfer> confirmRepository, ModelMapper mapper) {
        this.transferRepository = transferRepository;
        this.cardRepository = cardRepository;
        this.confirmRepository = confirmRepository;
        this.mapper = mapper;
    }

    @Override
    public SuccessTransferDtoOut send(TransferInfoDtoIn info) throws Exception {
        log.info("send amount to card");
        info.getAmountDtoIn().setValue(info.getAmountDtoIn().getValue() / 100);
        preCheckTransfer(info);
        TransferInfo result = mapper.map(info, TransferInfo.class);
        postCheckTransfer(result);
        var transfer = transferRepository.add(result);
        log.debug("CardFrom="+transfer.getCardFrom().getCardNumber() + " " +
                "CardTo="+transfer.getCardTo().getCardNumber() + " " +
                "Currency="+transfer.getCurrency() + " " +
                "Value="+transfer.getValue() + " " +
                "Status="+transfer.isStatus());
        log.info("send amount to card success");
        return new SuccessTransferDtoOut(transfer.getOperationId());
    }

    private void preCheckTransfer(TransferInfoDtoIn info) throws CardNotFoundException {
        log.info("pre check transfer");
        cardRepository.findCardByNumberAndValidTillAndCvv(info.getCardFromNumber(),
                        info.getCardFromValidTill(), info.getCardFromCVV())
                .orElseThrow(() -> {
                    log.error("Card not found: {}", info.getCardFromNumber());
                    return new CardNotFoundException();
                });
        cardRepository.findCardByNumber(info.getCardToNumber())
                .orElseThrow(() -> {
                    log.error("Card not found: {}", info.getCardToNumber());
                    return new CardNotFoundException();
                });
        log.info("pre check transfer success");
    }

    private void postCheckTransfer(TransferInfo info) throws Exception {
        log.info("post check transfer");
        var cardFrom = info.getCardFrom();
        var cardTo = info.getCardTo();

        if(!cardFrom.getIsActive() || !cardTo.getIsActive()) {
            long idOperation = transferRepository.add(info).getOperationId();
            log.error("Cards are not active: idOperation=" + idOperation);
            throw new CardNotActiveException(idOperation);
        }
        if(!cardFrom.getCurrency().equals(info.getCurrency())
                || !cardTo.getCurrency().equals(info.getCurrency())) {
            long idOperation = transferRepository.add(info).getOperationId();
            log.error("Invalid currency: idOperation=" + idOperation);
            throw new ErrorTransferCurrencyException(idOperation);
        }
        long valueFromAfterTransfer = cardFrom.getValue() - info.getValue();
        if(valueFromAfterTransfer <= 0) {
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
        confirmRepository.add(mapper.map(info, ConfirmTransfer.class));
        if(!DEFAULT_CONFIRM_CODE.equals(info.getCode())) {
            throw new InvalidConfirmException(Long.parseLong(info.getOperationId()));
        }
        long idOperation = Long.parseLong(info.getOperationId());
        executeTransfer(transferRepository.get(idOperation).get());
        log.info("confirm transfer success");
        return new SuccessTransferDtoOut(idOperation);
    }

    private void preCheckConfirm(ConfirmTransferDtoIn info) throws TransferNotFoundException {
        log.info("pre check confirm");
        long idOperation = Long.parseLong(info.getOperationId());
        transferRepository.findByIdOperation(idOperation)
                .orElseThrow(()->{
                    log.error("Transfer not found: idOperation={}", idOperation);
                    return new TransferNotFoundException(idOperation);
                });
        log.info("pre check confirm success");
    }

    private void executeTransfer(TransferInfo info) throws CardNotFoundException {
        log.info("execute transfer");
        long valueFromAfterTransfer = info.getCardFrom().getValue() - info.getValue();
        long valueToAfterTransfer = info.getCardTo().getValue() + info.getValue();
        cardRepository.setValueCard(info.getCardFrom().getCardNumber(), valueFromAfterTransfer);
        cardRepository.setValueCard(info.getCardTo().getCardNumber(), valueToAfterTransfer);
        info.setStatus(true);
        log.debug("CardFrom="+info.getCardFrom().getCardNumber() + " " +
                "CardTo="+info.getCardTo().getCardNumber() + " " +
                "Currency="+info.getCurrency() + " " +
                "Value="+info.getValue() + " " +
                "Status="+info.isStatus());
        log.info("execute transfer success");
    }

    @Override
    public List<TransferInfoDtoOut> getAll() {
        return transferRepository.getAll().stream()
                .map(x -> mapper.map(x, TransferInfoDtoOut.class))
                .toList();
    }

    @Override
    public TransferInfoDtoOut getById(long id) throws TransferNotFoundException {
        return mapper.map(transferRepository.get(id)
                                .orElseThrow(TransferNotFoundException::new),
                TransferInfoDtoOut.class);
    }
}
