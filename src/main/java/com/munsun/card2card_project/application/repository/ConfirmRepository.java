package com.munsun.card2card_project.application.repository;

import com.munsun.card2card_project.application.model.ConfirmTransfer;

import java.util.Optional;

public interface ConfirmRepository extends CrudRepository<ConfirmTransfer> {
    Optional<ConfirmTransfer> findByTransferInfo_operationId(Long operationId);
}
