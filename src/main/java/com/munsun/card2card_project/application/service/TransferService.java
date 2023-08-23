package com.munsun.card2card_project.application.service;

import com.munsun.card2card_project.application.dto.in.ConfirmTransferDtoIn;
import com.munsun.card2card_project.application.dto.in.TransferInfoDtoIn;
import com.munsun.card2card_project.application.dto.out.SuccessTransferDtoOut;
import com.munsun.card2card_project.application.dto.out.TransferInfoDtoOut;
import com.munsun.card2card_project.application.exception.TransferNotFoundException;

import java.util.List;

public interface TransferService {
    SuccessTransferDtoOut send(TransferInfoDtoIn info) throws Exception;
    SuccessTransferDtoOut confirm(ConfirmTransferDtoIn info) throws Exception;
    List<TransferInfoDtoOut> getAll();
    TransferInfoDtoOut findByIdOperation(long id) throws TransferNotFoundException;
}
