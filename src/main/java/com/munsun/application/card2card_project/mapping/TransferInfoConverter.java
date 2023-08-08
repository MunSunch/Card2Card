package com.munsun.application.card2card_project.mapping;

import com.munsun.application.card2card_project.dto.in.TransferInfoDtoIn;
import com.munsun.application.card2card_project.model.TransferInfo;
import com.munsun.application.card2card_project.repository.impl.CardRepositoryImpl;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransferInfoConverter implements Converter<TransferInfoDtoIn, TransferInfo> {
    private final CardRepositoryImpl cardRepository;

    @Autowired
    public TransferInfoConverter(CardRepositoryImpl cardRepository) {
        this.cardRepository = cardRepository;
    }

    // ?
    @Override
    public TransferInfo convert(MappingContext<TransferInfoDtoIn, TransferInfo> mappingContext) {
        var source = mappingContext.getSource();
        TransferInfo result = new TransferInfo();
        result.setCardFrom(cardRepository
                .findCardByNumberAndValidTillAndCvv(source.getCardFromNumber(), source.getCardFromValidTill(), source.getCardFromCVV())
                .orElse(null));
        result.setCardTo(cardRepository.findCardByNumber(source.getCardToNumber())
                .orElse(null));
        result.setCurrency(source.getAmountDtoIn().getCurrency());
        result.setValue(source.getAmountDtoIn().getValue());
        return result;
    }
}