package com.munsun.application.card2card_project.repository;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<T> {
    T add(T newObj);
    Optional<T> remove(Long id);
    Optional<T> update(Long id, T newObj);
    Optional<T> get(Long id);
    List<T> getAll();
}
