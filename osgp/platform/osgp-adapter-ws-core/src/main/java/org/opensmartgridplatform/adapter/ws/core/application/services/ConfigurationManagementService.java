//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.core.application.services;

import javax.validation.Valid;
import org.joda.time.DateTime;
import org.opensmartgridplatform.adapter.ws.core.infra.jms.CommonRequestMessage;
import org.opensmartgridplatform.adapter.ws.core.infra.jms.CommonRequestMessageSender;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.valueobjects.Configuration;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.domain.services.CorrelationIdProviderService;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.validation.Identification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service(value = "wsCoreConfigurationManagementService")
@Transactional("transactionManager")
@Validated
public class ConfigurationManagementService {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(ConfigurationManagementService.class);

  @Autowired private DomainHelperService domainHelperService;

  @Autowired private CorrelationIdProviderService correlationIdProviderService;

  @Autowired private CommonRequestMessageSender commonRequestMessageSender;

  public ConfigurationManagementService() {
    // Parameterless constructor required for transactions
  }

  public String enqueueSetConfigurationRequest(
      @Identification final String organisationIdentification,
      @Identification final String deviceIdentification,
      @Valid final Configuration configuration,
      final DateTime scheduledTime,
      final int messagePriority)
      throws FunctionalException {

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

    this.domainHelperService.isAllowed(organisation, device, DeviceFunction.SET_CONFIGURATION);
    this.domainHelperService.isInMaintenance(device);

    LOGGER.debug(
        "enqueueSetConfigurationRequest called with organisation {} and device {}",
        organisationIdentification,
        deviceIdentification);

    final String correlationUid =
        this.correlationIdProviderService.getCorrelationId(
            organisationIdentification, deviceIdentification);

    final MessageMetadata messageMetadata =
        new MessageMetadata.Builder()
            .withDeviceIdentification(deviceIdentification)
            .withOrganisationIdentification(organisationIdentification)
            .withCorrelationUid(correlationUid)
            .withMessageType(MessageType.SET_CONFIGURATION.name())
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduledTime == null ? null : scheduledTime.getMillis())
            .build();

    final CommonRequestMessage message =
        new CommonRequestMessage.Builder()
            .messageMetadata(messageMetadata)
            .request(configuration)
            .build();

    this.commonRequestMessageSender.send(message);

    return correlationUid;
  }

  public String enqueueGetConfigurationRequest(
      @Identification final String organisationIdentification,
      @Identification final String deviceIdentification,
      final int messagePriority)
      throws FunctionalException {

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

    this.domainHelperService.isAllowed(organisation, device, DeviceFunction.GET_CONFIGURATION);
    this.domainHelperService.isInMaintenance(device);

    LOGGER.debug(
        "enqueueGetConfigurationRequest called with organisation {} and device {}",
        organisationIdentification,
        deviceIdentification);

    final String correlationUid =
        this.correlationIdProviderService.getCorrelationId(
            organisationIdentification, deviceIdentification);

    final MessageMetadata messageMetadata =
        new MessageMetadata.Builder()
            .withDeviceIdentification(deviceIdentification)
            .withOrganisationIdentification(organisationIdentification)
            .withCorrelationUid(correlationUid)
            .withMessageType(MessageType.GET_CONFIGURATION.name())
            .withMessagePriority(messagePriority)
            .build();

    final CommonRequestMessage message =
        new CommonRequestMessage.Builder().messageMetadata(messageMetadata).build();

    this.commonRequestMessageSender.send(message);

    return correlationUid;
  }

  public String enqueueSwitchConfigurationRequest(
      final String organisationIdentification,
      final String deviceIdentification,
      final String configurationBank,
      final int messagePriority)
      throws FunctionalException {
    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

    this.domainHelperService.isAllowed(
        organisation, device, DeviceFunction.SWITCH_CONFIGURATION_BANK);
    this.domainHelperService.isInMaintenance(device);

    LOGGER.debug(
        "enqueueGetConfigurationRequest called with organisation {} and device {}",
        organisationIdentification,
        deviceIdentification);

    final String correlationUid =
        this.correlationIdProviderService.getCorrelationId(
            organisationIdentification, deviceIdentification);

    final MessageMetadata messageMetadata =
        new MessageMetadata.Builder()
            .withDeviceIdentification(deviceIdentification)
            .withOrganisationIdentification(organisationIdentification)
            .withCorrelationUid(correlationUid)
            .withMessageType(MessageType.SWITCH_CONFIGURATION_BANK.name())
            .withMessagePriority(messagePriority)
            .build();

    final CommonRequestMessage message =
        new CommonRequestMessage.Builder()
            .messageMetadata(messageMetadata)
            .request(configurationBank)
            .build();

    this.commonRequestMessageSender.send(message);

    return correlationUid;
  }
}
