package com.munsun.application.card2card_project.repository.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.munsun.application.card2card_project.model.ConfirmTransfer;
import com.munsun.application.card2card_project.repository.CrudRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ConfirmRepositoryImpl implements CrudRepository<ConfirmTransfer> {
    private final ConcurrentHashMap<Long, ConfirmTransfer> confirms;
    private final ConcurrentHashMap<Long, ConfirmTransfer> confirmsIndexByOperationIdTransfer;
    private final AtomicLong generatorId;
    private final Gson gson;
    @Value("${confirms.file.location}")
    private String pathFile;

    @Autowired
    public ConfirmRepositoryImpl(Gson gson) {
        this.gson = gson;
        this.confirms = new ConcurrentHashMap<>();
        this.generatorId = new AtomicLong();
        this.confirmsIndexByOperationIdTransfer = new ConcurrentHashMap<>();
    }

    @PostConstruct
    protected void postConstruct() throws IOException {
        Path path = Path.of(pathFile);
        String in = "";
        try {
            in = Files.readString(path);
            if(in.isEmpty())
                return;
        } catch (NoSuchFileException e) {
            Files.createFile(path);
            return;
        }
        TypeToken<List<ConfirmTransfer>> collectionType = new TypeToken<>(){};
        var temp = gson.fromJson(in, collectionType);
        temp.forEach(x -> {
            confirms.put(generatorId.incrementAndGet(), x);
            confirmsIndexByOperationIdTransfer.put(x.getTransferInfo().getOperationId(), x);
        });
    }

    @PreDestroy
    protected void preDestroy() throws IOException {
        Path path = Path.of(pathFile);
        String out = gson.toJson(confirms.values());
        Files.writeString(path, out);
    }

    @Override
    public ConfirmTransfer add(ConfirmTransfer newObj) {
        confirmsIndexByOperationIdTransfer.put(newObj.getTransferInfo().getOperationId(), newObj);
        return confirms.put(generatorId.incrementAndGet(), newObj);
    }

    @Override
    public Optional<ConfirmTransfer> remove(Long id) {
        long idOperation = confirms.remove(id).getTransferInfo().getOperationId();
        return Optional.ofNullable(confirmsIndexByOperationIdTransfer.remove(idOperation));
    }

    @Override
    public Optional<ConfirmTransfer> update(Long id, ConfirmTransfer newObj) {
        confirmsIndexByOperationIdTransfer.replace(confirms.get(id).getTransferInfo().getOperationId(), newObj);
        return Optional.ofNullable(confirms.replace(id, newObj));
    }

    @Override
    public Optional<ConfirmTransfer> get(Long id) {
        return Optional.ofNullable(confirms.get(id));
    }
}
