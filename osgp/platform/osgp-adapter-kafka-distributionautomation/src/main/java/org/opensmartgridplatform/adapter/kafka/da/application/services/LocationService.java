/**
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.application.services;

import java.util.Optional;
import java.util.function.Predicate;

import org.opensmartgridplatform.adapter.kafka.da.domain.entities.Feeder;
import org.opensmartgridplatform.adapter.kafka.da.domain.entities.Location;
import org.opensmartgridplatform.adapter.kafka.da.domain.repositories.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    @Autowired
    public LocationService(final LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public Optional<Location> getLocation(final String substationIdentification) {
        return this.locationRepository.findOneBySubstationIdentification(substationIdentification);
    }

    public Optional<Feeder> getFeeder(final Location location, final int feederNumber) {
        if (location == null || location.getFeederList() == null) {
            return Optional.empty();
        }

        final Predicate<Feeder> byFeederNumber = f -> f.getFeederNumber()
                .intValue() == feederNumber;

        return location.getFeederList()
                .stream()
                .filter(byFeederNumber)
                .findFirst();
    }
}
