/**
 * Copyright 2021 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.application.services;

import org.opensmartgridplatform.adapter.kafka.da.application.repositories.FeederRepository;
import org.opensmartgridplatform.adapter.kafka.da.application.repositories.LocationRepository;
import org.springframework.stereotype.Service;

@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final FeederRepository feederRepository;

    public LocationService(final LocationRepository locationRepository, final FeederRepository feederRepository) {
        this.locationRepository = locationRepository;
        this.feederRepository = feederRepository;
    }

    public String getBayIdentification(final String substationIdentification, final String feeder) {
        return this.feederRepository.findOneByLocation_NameAndName(substationIdentification, feeder).getName();
    }

    public String getSubstationIdentification(final String substationIdentification) {
        return this.locationRepository.findOneBySubstationIdentification(substationIdentification).getName();
    }
}
