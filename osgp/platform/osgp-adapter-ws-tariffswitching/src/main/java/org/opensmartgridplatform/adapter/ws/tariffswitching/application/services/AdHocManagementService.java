//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.tariffswitching.application.services;

import org.opensmartgridplatform.adapter.ws.tariffswitching.infra.jms.TariffSwitchingRequestMessage;
import org.opensmartgridplatform.adapter.ws.tariffswitching.infra.jms.TariffSwitchingRequestMessageSender;
import org.opensmartgridplatform.adapter.ws.tariffswitching.infra.jms.TariffSwitchingResponseMessageFinder;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.domain.services.CorrelationIdProviderService;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.validation.Identification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service(value = "wsTariffSwitchingAdHocManagementService")
@Transactional(value = "transactionManager")
@Validated
public class AdHocManagementService {

  private static final int PAGE_SIZE = 30;

  private static final Logger LOGGER = LoggerFactory.getLogger(AdHocManagementService.class);

  @Autowired private DomainHelperService domainHelperService;

  @Autowired private DeviceRepository deviceRepository;

  @Autowired private CorrelationIdProviderService correlationIdProviderService;

  @Autowired private TariffSwitchingRequestMessageSender tariffSwitchingRequestMessageSender;

  @Autowired private TariffSwitchingResponseMessageFinder tariffSwitchingResponseMessageFinder;

  public AdHocManagementService() {
    // Parameterless constructor required for transactions
  }

  public Page<Device> findAllDevices(
      @Identification final String organisationIdentification, final int pageNumber)
      throws FunctionalException {

    LOGGER.debug(
        "findAllDevices called with organisation {} and pageNumber {}",
        organisationIdentification,
        pageNumber);

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);

    final PageRequest request =
        PageRequest.of(pageNumber, PAGE_SIZE, Sort.Direction.DESC, "deviceIdentification");
    return this.deviceRepository.findAllAuthorized(organisation, request);
  }

  public String enqueueGetTariffStatusRequest(
      @Identification final String organisationIdentification,
      @Identification final String deviceIdentification,
      final int messagePriority)
      throws FunctionalException {

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

    this.domainHelperService.isAllowed(organisation, device, DeviceFunction.GET_STATUS);

    LOGGER.debug(
        "enqueueGetTariffStatusRequest called with organisation {} and device {}",
        organisationIdentification,
        deviceIdentification);

    final String correlationUid =
        this.correlationIdProviderService.getCorrelationId(
            organisationIdentification, deviceIdentification);

    final MessageMetadata deviceMessageMetadata =
        new MessageMetadata.Builder()
            .withDeviceIdentification(deviceIdentification)
            .withOrganisationIdentification(organisationIdentification)
            .withCorrelationUid(correlationUid)
            .withMessageType(MessageType.GET_TARIFF_STATUS.name())
            .withMessagePriority(messagePriority)
            .build();

    final TariffSwitchingRequestMessage message =
        new TariffSwitchingRequestMessage.Builder().messageMetadata(deviceMessageMetadata).build();

    this.tariffSwitchingRequestMessageSender.send(message);

    return correlationUid;
  }

  public ResponseMessage dequeueGetTariffStatusResponse(final String correlationUid)
      throws OsgpException {

    return this.tariffSwitchingResponseMessageFinder.findMessage(correlationUid);
  }
}
