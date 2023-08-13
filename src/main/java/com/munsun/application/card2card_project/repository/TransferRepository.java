package com.munsun.application.card2card_project.repository;

import com.munsun.application.card2card_project.model.TransferInfo;

import java.util.Optional;

public interface TransferRepository extends CrudRepository<TransferInfo> {
    Optional<TransferInfo> findByIdOperation(long idOperation);
}
