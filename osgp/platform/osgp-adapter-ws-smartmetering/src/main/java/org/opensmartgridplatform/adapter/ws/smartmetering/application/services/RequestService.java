// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.services;

import java.io.Serializable;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.AsyncResponse;
import org.opensmartgridplatform.adapter.ws.smartmetering.endpoints.RequestMessageMetadata;
import org.opensmartgridplatform.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessage;
import org.opensmartgridplatform.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessageSender;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.domain.services.CorrelationIdProviderService;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Slf4j
@Validated
public class RequestService {

  private final DomainHelperService domainHelperService;

  private final SmartMeteringRequestMessageSender smartMeteringRequestMessageSender;

  private final CorrelationIdProviderService correlationIdProviderService;

  public RequestService(
      final DomainHelperService domainHelperService,
      final SmartMeteringRequestMessageSender smartMeteringRequestMessageSender,
      final CorrelationIdProviderService correlationIdProviderService) {
    this.domainHelperService = domainHelperService;
    this.smartMeteringRequestMessageSender = smartMeteringRequestMessageSender;
    this.correlationIdProviderService = correlationIdProviderService;
  }

  public AsyncResponse enqueueAndSendRequest(
      final RequestMessageMetadata requestMessageMetadata, final Serializable requestData)
      throws FunctionalException {

    log.debug(
        "{} called with organisation {} and device {}",
        requestMessageMetadata.getMessageType(),
        requestMessageMetadata.getOrganisationIdentification(),
        requestMessageMetadata.getDeviceIdentification());

    if (requestMessageMetadata.getDeviceFunction() != null) {
      this.checkAllowed(
          requestMessageMetadata.getOrganisationIdentification(),
          requestMessageMetadata.getDeviceIdentification(),
          requestMessageMetadata.getDeviceFunction());
    }

    final String correlationUid =
        this.correlationIdProviderService.getCorrelationId(
            requestMessageMetadata.getOrganisationIdentification(),
            requestMessageMetadata.getDeviceIdentification());

    final MessageMetadata messageMetadata =
        requestMessageMetadata.newMessageMetadata(correlationUid);

    final SmartMeteringRequestMessage message =
        SmartMeteringRequestMessage.newBuilder()
            .messageMetadata(messageMetadata)
            .request(requestData)
            .build();

    this.smartMeteringRequestMessageSender.send(message);

    return this.createAsyncResponse(correlationUid, messageMetadata.getDeviceIdentification());
  }

  AsyncResponse createAsyncResponse(
      final String correlationUid, final String deviceIdentification) {
    final AsyncResponse asyncResponse = new AsyncResponse();
    asyncResponse.setCorrelationUid(correlationUid);
    asyncResponse.setDeviceIdentification(deviceIdentification);
    return asyncResponse;
  }

  void checkAllowed(
      final String organisationIdentification,
      final String deviceIdentification,
      final DeviceFunction deviceFunction)
      throws FunctionalException {
    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

    this.domainHelperService.checkAllowed(organisation, device, deviceFunction);
  }
}
