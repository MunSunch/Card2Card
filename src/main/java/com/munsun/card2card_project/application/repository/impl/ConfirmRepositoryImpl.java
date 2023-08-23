package com.munsun.card2card_project.application.repository.impl;

import com.munsun.card2card_project.application.model.ConfirmTransfer;
import com.munsun.card2card_project.application.repository.ConfirmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ConfirmRepositoryImpl implements ConfirmRepository {
    private final ConcurrentHashMap<Long, ConfirmTransfer> confirms;
    private final ConcurrentHashMap<Long, ConfirmTransfer> confirmsIndexByOperationIdTransfer;
    private final AtomicLong generatorId;

    @Autowired
    public ConfirmRepositoryImpl() {
        this.confirms = new ConcurrentHashMap<>();
        this.generatorId = new AtomicLong();
        this.confirmsIndexByOperationIdTransfer = new ConcurrentHashMap<>();
    }

    @Override
    public ConfirmTransfer add(ConfirmTransfer newObj) {
        var temp = new ConfirmTransfer(newObj);
        confirmsIndexByOperationIdTransfer.put(temp.getTransferInfo().getOperationId(), temp);
        return confirms.put(generatorId.incrementAndGet(), temp);
    }

    @Override
    public List<ConfirmTransfer> getAll() {
        return confirms.values().stream().toList();
    }

    @Override
    public Optional<ConfirmTransfer> findByTransferInfo_operationId(Long operationId) {
        return Optional.ofNullable(confirmsIndexByOperationIdTransfer.get(operationId));
    }

    @Override
    public void clear() {
        confirms.clear();
        confirmsIndexByOperationIdTransfer.clear();
    }
}
