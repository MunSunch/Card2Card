package com.munsun.application.card2card_project.service.impl;

import com.munsun.application.card2card_project.dto.in.ConfirmTransferDtoIn;
import com.munsun.application.card2card_project.dto.in.TransferInfoDtoIn;
import com.munsun.application.card2card_project.dto.out.SuccessTransferDtoOut;
import com.munsun.application.card2card_project.dto.out.TransferInfoDtoOut;
import com.munsun.application.card2card_project.exception.*;
import com.munsun.application.card2card_project.model.ConfirmTransfer;
import com.munsun.application.card2card_project.model.TransferInfo;
import com.munsun.application.card2card_project.repository.impl.CardRepositoryImpl;
import com.munsun.application.card2card_project.repository.impl.ConfirmRepositoryImpl;
import com.munsun.application.card2card_project.repository.impl.TransferRepositoryImpl;
import com.munsun.application.card2card_project.service.TransferService;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TransferServiceImpl implements TransferService {
    private final TransferRepositoryImpl transferRepository;
    private final CardRepositoryImpl cardRepository;
    private final ConfirmRepositoryImpl confirmRepository;
    private final ModelMapper mapper;

    private static final String DEFAULT_CONFIRM_CODE = "0000";

    @Autowired
    public TransferServiceImpl(TransferRepositoryImpl transferRepository, CardRepositoryImpl cardRepository, ConfirmRepositoryImpl confirmRepository, ModelMapper mapper) {
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
        return new SuccessTransferDtoOut(transfer.getOperationId());
    }

    private void preCheckTransfer(TransferInfoDtoIn info) throws CardNotFoundException {
        log.info("pre check transfer");
        cardRepository.findCardByNumberAndValidTillAndCvv(info.getCardFromNumber(),
                        info.getCardFromValidTill(), info.getCardFromCVV())
                .orElseThrow(CardNotFoundException::new);
        cardRepository.findCardByNumber(info.getCardToNumber())
                .orElseThrow(CardNotFoundException::new);
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
        return new SuccessTransferDtoOut(idOperation);
    }

    private void preCheckConfirm(ConfirmTransferDtoIn info) throws TransferNotFoundException {
        log.info("pre check confirm");
        transferRepository.get(Long.parseLong(info.getOperationId()))
                .orElseThrow(TransferNotFoundException::new);
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
    }

    @Override
    public List<TransferInfoDtoOut> getAll() {
        return transferRepository.getAll().stream()
                .map(x -> mapper.map(x, TransferInfoDtoOut.class))
                .toList();
    }
}
