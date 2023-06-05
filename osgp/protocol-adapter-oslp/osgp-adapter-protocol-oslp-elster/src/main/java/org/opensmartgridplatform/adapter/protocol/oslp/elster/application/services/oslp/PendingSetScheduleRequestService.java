// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.oslp.elster.application.services.oslp;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.domain.entities.PendingSetScheduleRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.domain.repositories.PendingSetScheduleRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(value = "transactionManager")
public class PendingSetScheduleRequestService {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(PendingSetScheduleRequestService.class);

  @Autowired private PendingSetScheduleRequestRepository pendingSetScheduleRequestRepository;

  @Autowired private Integer pendingSetScheduleRequestExpiresInMinutes;

  /** Constructor */
  public PendingSetScheduleRequestService() {
    // Parameterless constructor required for transactions...
  }

  public PendingSetScheduleRequest add(final PendingSetScheduleRequest pendingSetScheduleRequest) {
    LOGGER.info(
        "add PendingSetScheduleRequest for device : {}",
        pendingSetScheduleRequest.getDeviceIdentification());

    return this.pendingSetScheduleRequestRepository.save(pendingSetScheduleRequest);
  }

  public void remove(final PendingSetScheduleRequest pendingSetScheduleRequest) {
    LOGGER.info(
        "remove PendingSetScheduleRequest for device : {}",
        pendingSetScheduleRequest.getDeviceIdentification());

    this.pendingSetScheduleRequestRepository.delete(pendingSetScheduleRequest);
  }

  public List<PendingSetScheduleRequest> getAllByDeviceIdentificationNotExpired(
      final String deviceIdentification) {
    final Date currentDate = new Date();
    LOGGER.info(
        "get device by deviceIdentification {} and current time: {}",
        deviceIdentification,
        currentDate);

    return this.pendingSetScheduleRequestRepository
        .findAllByDeviceIdentificationAndExpiredAtIsAfter(deviceIdentification, currentDate);
  }

  public List<PendingSetScheduleRequest> getAllByDeviceUidNotExpired(final String deviceUid) {
    final Date currentDate = new Date();
    LOGGER.info("get device by deviceUid {} and current time: {}", deviceUid, currentDate);

    return this.pendingSetScheduleRequestRepository.findAllByDeviceUidAndExpiredAtIsAfter(
        deviceUid, currentDate);
  }

  public List<PendingSetScheduleRequest> getAll() {
    LOGGER.info("get all PendingSetScheduleRequests");

    return this.pendingSetScheduleRequestRepository.findAll();
  }

  public void removeExpiredPendingSetScheduleRequestRecords(final String deviceIdentification) {
    final Date expireDateTime =
        Date.from(
            ZonedDateTime.now()
                .minusMinutes(this.pendingSetScheduleRequestExpiresInMinutes)
                .toInstant());

    LOGGER.info(
        "remove PendingSetScheduleRequest(s) for device {} and older than time: {}",
        deviceIdentification,
        expireDateTime);

    this.pendingSetScheduleRequestRepository.deleteAllByDeviceIdentificationAndExpiredAtIsBefore(
        deviceIdentification, expireDateTime);
  }
}
