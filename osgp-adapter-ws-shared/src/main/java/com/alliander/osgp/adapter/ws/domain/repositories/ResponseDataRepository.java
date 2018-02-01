/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.domain.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alliander.osgp.adapter.ws.domain.entities.ResponseData;

@Repository
public interface ResponseDataRepository extends JpaRepository<ResponseData, Long> {

    List<ResponseData> findByOrganisationIdentification(String organisationIdentification);

    List<ResponseData> findByMessageType(String messageType);

    List<ResponseData> findByDeviceIdentification(String deviceIdentification);

    List<ResponseData> findByNumberOfNotificationsSentAndCreationTimeBefore(Short numberOfNotificationsSent,
            Date createdBefore, Pageable pageable);

    ResponseData findByCorrelationUid(String correlationUid);

    void removeByCreationTimeBefore(Date date);
}
