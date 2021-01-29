/**
 * Copyright 2021 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.application.services;

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

    public String getBayIdentification(final String substationIdentification, final String feederNumber) {
        final Location location = this.locationRepository.findOneBySubstationIdentification(substationIdentification);
        return location.getFeederList()
                .stream()
                .filter(f -> f.getFeederNumber().equals(Long.valueOf(feederNumber)))
                .map(Feeder::getName)
                .findFirst()
                .orElse("");
    }

    public String getSubstationIdentification(final String substationIdentification) {
        return this.locationRepository.findOneBySubstationIdentification(substationIdentification).getName();
    }
}
