package com.vaadin.starter.bakery.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.vaadin.starter.bakery.backend.data.entity.PickupLocation;
import com.vaadin.starter.bakery.backend.data.entity.User;
import com.vaadin.starter.bakery.backend.repositories.PickupLocationRepository;

@Service
public class PickupLocationService implements FilterableCrudService<PickupLocation>{

	private final PickupLocationRepository pickupLocationRepository;

	@Autowired
	public PickupLocationService(PickupLocationRepository pickupLocationRepository) {
		this.pickupLocationRepository = pickupLocationRepository;
	}

	public Page<PickupLocation> findAnyMatching(String filter, Pageable pageable) {
		if (filter != null) {
			String repositoryFilter = "%" + filter + "%";
			return pickupLocationRepository.findByNameLikeIgnoreCase(repositoryFilter, pageable);
		} else {
			return pickupLocationRepository.findAll(pageable);
		}
	}

	public long countAnyMatching(String filter) {
		if (filter != null) {
			String repositoryFilter = "%" + filter + "%";
			return pickupLocationRepository.countByNameLikeIgnoreCase(repositoryFilter);
		} else {
			return pickupLocationRepository.count();
		}
	}

	public PickupLocation getDefault() {
		return findAnyMatching(null, PageRequest.of(0, 1)).iterator().next();
	}

	@Override
	public JpaRepository<PickupLocation, Long> getRepository() {
		return pickupLocationRepository;
	}

	@Override
	public PickupLocation createNew(User currentUser) {
		return new PickupLocation();
	}
}
