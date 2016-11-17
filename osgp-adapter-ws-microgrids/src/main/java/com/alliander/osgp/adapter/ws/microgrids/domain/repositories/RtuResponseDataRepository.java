/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.microgrids.domain.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alliander.osgp.adapter.ws.microgrids.domain.entities.RtuResponseData;

@Repository
public interface RtuResponseDataRepository extends JpaRepository<RtuResponseData, Long> {

    List<RtuResponseData> findByOrganisationIdentification(String organisationIdentification);

    List<RtuResponseData> findByMessageType(String messageType);

    List<RtuResponseData> findByDeviceIdentification(String deviceIdentification);

    List<RtuResponseData> findByCorrelationUid(String correlationUid);

    RtuResponseData findSingleResultByCorrelationUid(String correlationUid);
}
