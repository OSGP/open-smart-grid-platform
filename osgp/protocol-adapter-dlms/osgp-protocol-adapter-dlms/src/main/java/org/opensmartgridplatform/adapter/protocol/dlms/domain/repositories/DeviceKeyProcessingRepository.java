/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories;

import java.time.Instant;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DeviceKeyProcessing;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class DeviceKeyProcessingRepository {

  @PersistenceContext private EntityManager em;

  @Transactional
  public int updateStartTime(final String deviceIdentification, final Instant delayTime) {
    final Query query =
        this.em.createQuery(
            "UPDATE DeviceKeyProcessing d SET d.startTime = CURRENT_TIMESTAMP"
                + " WHERE d.deviceIdentification = :deviceIdentification"
                + "   AND d.startTime < :delayTime");
    query.setParameter("deviceIdentification", deviceIdentification);
    query.setParameter("delayTime", delayTime);
    return query.executeUpdate();
  }

  @Transactional
  public boolean insert(final String deviceIdentification) {
    try {
      final DeviceKeyProcessing deviceKeyProcessing = new DeviceKeyProcessing();
      deviceKeyProcessing.setDeviceIdentification(deviceIdentification);
      deviceKeyProcessing.setStartTime(Instant.now());
      this.em.persist(deviceKeyProcessing);
      return true;
    } catch (final Exception e) {
      return false;
    }
  };

  @Transactional
  public int remove(final String deviceIdentification) {
    final Query query =
        this.em.createQuery(
            "DELETE DeviceKeyProcessing d "
                + " WHERE d.deviceIdentification = :deviceIdentification");
    query.setParameter("deviceIdentification", deviceIdentification);
    return query.executeUpdate();
  };
}
