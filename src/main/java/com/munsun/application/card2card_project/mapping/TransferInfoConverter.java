package com.munsun.application.card2card_project.mapping;

import com.munsun.application.card2card_project.dto.in.TransferInfoDtoIn;
import com.munsun.application.card2card_project.exception.CardNotFoundException;
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

    @Override
    public TransferInfo convert(MappingContext<TransferInfoDtoIn, TransferInfo> mappingContext) {
        var source = mappingContext.getSource();
        TransferInfo result = new TransferInfo();
        try {
            result.setCardFrom(cardRepository.findCardByNumber(source.getCardFromNumber())
                    .orElseThrow(CardNotFoundException::new));
            result.setCardTo(cardRepository.findCardByNumber(source.getCardToNumber())
                    .orElseThrow(CardNotFoundException::new));
        } catch (CardNotFoundException e) {
            e.printStackTrace();
        }
        result.setCurrency(source.getAmountDtoIn().getCurrency());
        result.setValue(source.getAmountDtoIn().getValue());
        return result;
    }
}
