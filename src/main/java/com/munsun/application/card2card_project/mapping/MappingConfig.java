package com.munsun.application.card2card_project.mapping;

import com.munsun.application.card2card_project.dto.in.ConfirmTransferDtoIn;
import com.munsun.application.card2card_project.dto.in.TransferInfoDtoIn;
import com.munsun.application.card2card_project.dto.out.TransferInfoDtoOut;
import com.munsun.application.card2card_project.model.ConfirmTransfer;
import com.munsun.application.card2card_project.model.TransferInfo;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MappingConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(TransferInfo.class, TransferInfoDtoOut.class)
                .addMappings(mapper -> mapper.map(TransferInfo::getCurrency, (dest, v) -> dest.getAmountDtoOut().setCurrency((String) v)))
                .addMappings(mapper -> mapper.map(TransferInfo::getValue, (dest, v) -> dest.getAmountDtoOut().setValue((Long) v)));

        var transferMap = modelMapper.createTypeMap(TransferInfoDtoIn.class, TransferInfo.class);
        transferMap.setConverter(ApplicationContextProvider.getApplicationContext().getBean(TransferInfoConverter.class));

        var confirmTransferMap = modelMapper.createTypeMap(ConfirmTransferDtoIn.class, ConfirmTransfer.class);
        confirmTransferMap.setConverter(ApplicationContextProvider.getApplicationContext().getBean(ConfirmTransferConverter.class));
        return modelMapper;
    }
}
