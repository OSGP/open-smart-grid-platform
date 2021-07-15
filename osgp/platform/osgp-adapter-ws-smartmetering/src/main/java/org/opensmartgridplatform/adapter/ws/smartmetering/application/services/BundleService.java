/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.services;

import java.util.List;
import org.opensmartgridplatform.adapter.ws.smartmetering.endpoints.RequestMessageMetadata;
import org.opensmartgridplatform.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessage;
import org.opensmartgridplatform.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessageSender;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActionRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.BundleMessageRequest;
import org.opensmartgridplatform.shared.domain.services.CorrelationIdProviderService;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service(value = "wsSmartMeteringBundleService")
@Transactional(value = "transactionManager")
@Validated
public class BundleService {

  @Autowired private DomainHelperService domainHelperService;

  @Autowired private CorrelationIdProviderService correlationIdProviderService;

  @Autowired private SmartMeteringRequestMessageSender smartMeteringRequestMessageSender;

  public BundleService() {
    // Parameterless constructor required for transactions
  }

  public String enqueueBundleRequest(
      final RequestMessageMetadata requestMessageMetadata, final List<ActionRequest> actionList)
      throws FunctionalException {
    final String organisationIdentification =
        requestMessageMetadata.getOrganisationIdentification();
    final String deviceIdentification = requestMessageMetadata.getDeviceIdentification();

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

    this.checkIfBundleIsAllowed(actionList, organisation, device);

    final String correlationUid =
        this.correlationIdProviderService.getCorrelationId(
            organisationIdentification, deviceIdentification);

    final DeviceMessageMetadata deviceMessageMetadata =
        requestMessageMetadata.newDeviceMessageMetadata(correlationUid);

    final SmartMeteringRequestMessage message =
        new SmartMeteringRequestMessage.Builder()
            .deviceMessageMetadata(deviceMessageMetadata)
            .request(new BundleMessageRequest(actionList))
            .build();

    this.smartMeteringRequestMessageSender.send(message);

    return correlationUid;
  }

  /**
   * checks if the bundle and the {@link ActionRequest}s in the bundle are allowed and valid
   *
   * @param actionList the {@link List} of {@link ActionRequest}s in the bundle
   * @param organisation the organisation to check
   * @param device the device to check
   * @throws FunctionalException when either the bundle or the actions in the bundle are not
   *     allowed, or when the action is not valid
   */
  private void checkIfBundleIsAllowed(
      final List<ActionRequest> actionList, final Organisation organisation, final Device device)
      throws FunctionalException {
    this.domainHelperService.checkAllowed(
        organisation, device, DeviceFunction.HANDLE_BUNDLED_ACTIONS);
    for (final ActionRequest action : actionList) {
      this.domainHelperService.checkAllowed(organisation, device, action.getDeviceFunction());
      action.validate();
    }
  }
}
