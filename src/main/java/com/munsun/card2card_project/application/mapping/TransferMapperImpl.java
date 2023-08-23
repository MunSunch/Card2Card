package com.munsun.card2card_project.application.mapping;

import com.munsun.card2card_project.application.dto.in.ConfirmTransferDtoIn;
import com.munsun.card2card_project.application.dto.in.TransferInfoDtoIn;
import com.munsun.card2card_project.application.dto.out.AmountDtoOut;
import com.munsun.card2card_project.application.dto.out.TransferInfoDtoOut;
import com.munsun.card2card_project.application.exception.CardNotFoundException;
import com.munsun.card2card_project.application.exception.TransferNotFoundException;
import com.munsun.card2card_project.application.model.ConfirmTransfer;
import com.munsun.card2card_project.application.model.TransferInfo;
import com.munsun.card2card_project.application.repository.CardRepository;
import com.munsun.card2card_project.application.repository.TransferRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TransferMapperImpl {
    private CardRepository cardRepository;
    private TransferRepository transferRepository;

    public TransferInfo map(TransferInfoDtoIn info) throws CardNotFoundException {
        var transferInfo = new TransferInfo();
            transferInfo.setValue(info.amountDtoIn().value());
            transferInfo.setCurrency(info.amountDtoIn().currency());
            transferInfo.setCardFrom(cardRepository.findCardByNumberAndValidTillAndCvv(
                    info.cardFromNumber(), info.cardFromValidTill(), info.cardFromCVV()
            ).orElseThrow(CardNotFoundException::new));
            transferInfo.setCardTo(cardRepository.findCardByNumber(info.cardToNumber())
                    .orElseThrow(CardNotFoundException::new));
        return transferInfo;
    }

    public ConfirmTransfer map(ConfirmTransferDtoIn info) throws TransferNotFoundException {
        var confirmTransfer = new ConfirmTransfer();
            confirmTransfer.setTransferInfo(transferRepository.findByIdOperation(Long.parseLong(info.operationId()))
                    .orElseThrow(TransferNotFoundException::new));
            confirmTransfer.setCodeConfirm(Long.valueOf(info.code()));
        return confirmTransfer;
    }

    public TransferInfoDtoOut map(TransferInfo info) {
        return new TransferInfoDtoOut(info.getOperationId(),
                info.getCardFrom().getCardNumber(),
                info.getCardTo().getCardNumber(),
                new AmountDtoOut(info.getCurrency(), info.getValue()),
                info.isStatus());
    }
}
