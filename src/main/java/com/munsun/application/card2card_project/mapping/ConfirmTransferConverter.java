package com.munsun.application.card2card_project.mapping;

import com.munsun.application.card2card_project.dto.in.ConfirmTransferDtoIn;
import com.munsun.application.card2card_project.model.ConfirmTransfer;
import com.munsun.application.card2card_project.model.TransferInfo;
import com.munsun.application.card2card_project.repository.CrudRepository;
import com.munsun.application.card2card_project.repository.impl.TransferRepositoryImpl;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConfirmTransferConverter implements Converter<ConfirmTransferDtoIn, ConfirmTransfer> {
    private final CrudRepository<TransferInfo> transferRepository;

    @Autowired
    public ConfirmTransferConverter(CrudRepository<TransferInfo> transferRepository) {
        this.transferRepository = transferRepository;
    }

    // ? обработка ошибки
    @Override
    public ConfirmTransfer convert(MappingContext<ConfirmTransferDtoIn, ConfirmTransfer> mappingContext) {
        var source = mappingContext.getSource();
        var target = new ConfirmTransfer();
            target.setCodeConfirm(Long.parseLong(source.getCode()));
        target.setTransferInfo(transferRepository.get(Long.valueOf(source.getOperationId())).get());
        return target;
    }
}
