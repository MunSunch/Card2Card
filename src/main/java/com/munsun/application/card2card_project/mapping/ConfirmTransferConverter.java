package com.munsun.application.card2card_project.mapping;

import com.munsun.application.card2card_project.dto.in.ConfirmTransferDtoIn;
import com.munsun.application.card2card_project.exception.TransferNotFoundException;
import com.munsun.application.card2card_project.model.ConfirmTransfer;
import com.munsun.application.card2card_project.repository.impl.ConfirmRepositoryImpl;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConfirmTransferConverter implements Converter<ConfirmTransferDtoIn, ConfirmTransfer> {
    private final ConfirmRepositoryImpl confirmRepository;

    @Autowired
    public ConfirmTransferConverter(ConfirmRepositoryImpl confirmRepository) {
        this.confirmRepository = confirmRepository;
    }

    @Override
    public ConfirmTransfer convert(MappingContext<ConfirmTransferDtoIn, ConfirmTransfer> mappingContext) {
        var source = mappingContext.getSource();
        var target = new ConfirmTransfer();
            target.setCodeConfirm(Long.parseLong(source.getCode()));
        try {
            target.setTransferInfo(confirmRepository.get(Long.valueOf(source.getOperationId()))
                    .orElseThrow(TransferNotFoundException::new)
                    .getTransferInfo());
        } catch (TransferNotFoundException e) {
            e.printStackTrace();
        }
        return target;
    }
}
