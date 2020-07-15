package com.vaadin.starter.bakery.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.vaadin.starter.bakery.backend.data.entity.AbstractEntity;

public interface FilterableCrudService<T extends AbstractEntity> extends CrudService<T> {

	Page<T> findAnyMatching(String filter, Pageable pageable);

	long countAnyMatching(String filter);

}
