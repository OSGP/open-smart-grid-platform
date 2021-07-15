/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.core.application.services;

import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.shared.validation.Identification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "domainCoreAdHocManagementService")
@Transactional(value = "transactionManager")
public class AdHocManagementService extends AbstractService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AdHocManagementService.class);

  /** Constructor */
  public AdHocManagementService() {
    // Parameterless constructor required for transactions...
  }

  // === SET REBOOT ===

  public void setReboot(
      @Identification final String organisationIdentification,
      @Identification final String deviceIdentification,
      final String correlationUid,
      final String messageType,
      final int messagePriority)
      throws FunctionalException {

    LOGGER.debug(
        "set reboot called for device {} with organisation {}",
        deviceIdentification,
        organisationIdentification);

    this.findOrganisation(organisationIdentification);

    final Device device = this.findActiveDevice(deviceIdentification);

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(correlationUid, organisationIdentification, deviceIdentification, null),
        messageType,
        messagePriority,
        device.getIpAddress());
  }
}
