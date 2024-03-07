// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.da.application.services;

import jakarta.persistence.OptimisticLockException;
import java.time.Duration;
import java.time.Instant;
import org.opensmartgridplatform.domain.core.entities.RtuDevice;
import org.opensmartgridplatform.domain.core.exceptions.UnknownEntityException;
import org.opensmartgridplatform.domain.core.repositories.RtuDeviceRepository;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.jpa.JpaOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RtuResponseService {

  private static final ComponentType COMPONENT_TYPE = ComponentType.DOMAIN_DISTRIBUTION_AUTOMATION;

  @Autowired protected RtuDeviceRepository rtuDeviceRepository;

  @Value(
      "#{T(java.time.Duration).parse('${communication.monitoring.minimum.duration.between.communication.time.updates:PT1M}')}")
  private Duration minimumDurationBetweenCommunicationTimeUpdates;

  /**
   * @return {@code true} if the device identified by {@code deviceIdentification} is known, {@code
   *     false} if the device is unknown, but not expected to be known.
   * @throws FunctionalException if the device identified by {@code deviceIdentification} is
   *     unknown, while it is expected to be known
   */
  @Transactional(value = "transactionManager")
  public boolean handleResponseMessageReceived(
      final Logger logger, final String deviceIdentification, final boolean expectDeviceToBeKnown)
      throws FunctionalException {
    try {
      final RtuDevice device =
          this.rtuDeviceRepository.findByDeviceIdentification(deviceIdentification).orElse(null);
      if (device == null && expectDeviceToBeKnown) {
        throw new FunctionalException(
            FunctionalExceptionType.UNKNOWN_DEVICE,
            COMPONENT_TYPE,
            new UnknownEntityException(RtuDevice.class, deviceIdentification));
      } else if (device == null) {
        logger.info(
            "No RTU device {} found to update communication time information for."
                + " This may be appropriate as the device could be expected to be unknown to GXF.",
            deviceIdentification);
        return false;
      }
      if (this.shouldUpdateCommunicationTime(
          device, this.minimumDurationBetweenCommunicationTimeUpdates)) {
        device.messageReceived();
        this.rtuDeviceRepository.save(device);
      } else {
        logger.info(
            "Last communication time within duration: {}. Skipping last communication date update.",
            this.minimumDurationBetweenCommunicationTimeUpdates);
      }
    } catch (final OptimisticLockException | JpaOptimisticLockingFailureException ex) {
      logger.warn("Last communication time not updated due to optimistic lock exception", ex);
    }
    return true;
  }

  private boolean shouldUpdateCommunicationTime(
      final RtuDevice device, final Duration minimumDurationBetweenCommunicationUpdates) {
    final Instant timeToCheck = Instant.now().minus(minimumDurationBetweenCommunicationUpdates);
    final Instant timeOfLastCommunication = device.getLastCommunicationTime();
    return timeOfLastCommunication.isBefore(timeToCheck);
  }
}
