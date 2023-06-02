//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.publiclighting.application.services;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import org.opensmartgridplatform.adapter.ws.publiclighting.infra.jms.PublicLightingRequestMessage;
import org.opensmartgridplatform.adapter.ws.publiclighting.infra.jms.PublicLightingRequestMessageSender;
import org.opensmartgridplatform.adapter.ws.publiclighting.infra.jms.PublicLightingResponseMessageFinder;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.domain.core.valueobjects.LightValue;
import org.opensmartgridplatform.domain.core.valueobjects.LightValueMessageDataContainer;
import org.opensmartgridplatform.domain.core.valueobjects.ResumeScheduleData;
import org.opensmartgridplatform.domain.core.valueobjects.TransitionMessageDataContainer;
import org.opensmartgridplatform.shared.application.config.PageSpecifier;
import org.opensmartgridplatform.shared.application.config.PagingSettings;
import org.opensmartgridplatform.shared.domain.services.CorrelationIdProviderService;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata.Builder;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.validation.Identification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service(value = "wsPublicLightingAdHocManagementService")
@Transactional(value = "transactionManager")
@Validated
public class AdHocManagementService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AdHocManagementService.class);

  @Autowired private DomainHelperService domainHelperService;

  @Autowired private DeviceRepository deviceRepository;

  @Autowired private CorrelationIdProviderService correlationIdProviderService;

  @Autowired
  @Qualifier("wsPublicLightingOutboundDomainRequestsMessageSender")
  private PublicLightingRequestMessageSender messageSender;

  @Autowired
  @Qualifier("wsPublicLightingInboundDomainResponsesMessageFinder")
  private PublicLightingResponseMessageFinder messageFinder;

  @Autowired private PagingSettings pagingSettings;

  public AdHocManagementService() {
    // Parameterless constructor required for transactions
  }

  public Page<Device> findAllDevices(
      @Identification final String organisationIdentification, final PageSpecifier pageSpecifier)
      throws FunctionalException {
    LOGGER.debug(
        "findAllDevices called with organisation {}, pageSize {} and pageNumber {}",
        organisationIdentification,
        pageSpecifier.getPageSize(),
        pageSpecifier.getPageNumber());

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);

    this.pagingSettings.updatePagingSettings(pageSpecifier);

    final PageRequest request =
        PageRequest.of(
            this.pagingSettings.getPageNumber(),
            this.pagingSettings.getPageSize(),
            Sort.Direction.DESC,
            "deviceIdentification");
    return this.deviceRepository.findAllAuthorized(organisation, request);
  }

  public String enqueueSetLightRequest(
      @Identification final String organisationIdentification,
      @Identification final String deviceIdentification,
      @Size(min = 1, max = 6) @Valid final List<LightValue> lightValues,
      final int messagePriority)
      throws FunctionalException {

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

    this.domainHelperService.isAllowed(organisation, device, DeviceFunction.SET_LIGHT);
    this.domainHelperService.isInMaintenance(device);

    LOGGER.debug(
        "enqueueSetLightRequest called with organisation {} and device {}",
        organisationIdentification,
        deviceIdentification);

    final String correlationUid =
        this.correlationIdProviderService.getCorrelationId(
            organisationIdentification, deviceIdentification);

    final LightValueMessageDataContainer lightValueMessageDataContainer =
        new LightValueMessageDataContainer(lightValues);

    final MessageMetadata deviceMessageMetadata =
        new MessageMetadata.Builder()
            .withDeviceIdentification(deviceIdentification)
            .withOrganisationIdentification(organisationIdentification)
            .withCorrelationUid(correlationUid)
            .withMessageType(MessageType.SET_LIGHT.name())
            .withMessagePriority(messagePriority)
            .build();

    final PublicLightingRequestMessage message =
        new PublicLightingRequestMessage.Builder()
            .messageMetadata(deviceMessageMetadata)
            .request(lightValueMessageDataContainer)
            .build();
    this.messageSender.send(message);

    return correlationUid;
  }

  public ResponseMessage dequeueSetLightResponse(final String correlationUid) throws OsgpException {

    return this.messageFinder.findMessage(correlationUid);
  }

  public String enqueueGetStatusRequest(
      @Identification final String organisationIdentification,
      @Identification final String deviceIdentification,
      final int messagePriority)
      throws FunctionalException {

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

    this.domainHelperService.isAllowed(organisation, device, DeviceFunction.GET_STATUS);

    LOGGER.debug(
        "enqueueGetStatusRequest called with organisation {} and device {}",
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
            .withMessageType(MessageType.GET_LIGHT_STATUS.name())
            .withMessagePriority(messagePriority)
            .build();

    final PublicLightingRequestMessage message =
        new PublicLightingRequestMessage.Builder().messageMetadata(deviceMessageMetadata).build();

    this.messageSender.send(message);

    return correlationUid;
  }

  public ResponseMessage dequeueGetStatusResponse(final String correlationUid)
      throws OsgpException {

    return this.messageFinder.findMessage(correlationUid);
  }

  public String enqueueResumeScheduleRequest(
      @Identification final String organisationIdentification,
      @Identification final String deviceIdentification,
      @Valid final ResumeScheduleData resumeScheduleData,
      final int messagePriority)
      throws FunctionalException {

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

    this.domainHelperService.isAllowed(organisation, device, DeviceFunction.RESUME_SCHEDULE);
    this.domainHelperService.isInMaintenance(device);

    LOGGER.debug(
        "enqueueResumeScheduleRequest called with organisation {}, device {} and resumeScheduleData {} ",
        organisationIdentification,
        deviceIdentification,
        resumeScheduleData);

    final String correlationUid =
        this.correlationIdProviderService.getCorrelationId(
            organisationIdentification, deviceIdentification);

    final MessageMetadata deviceMessageMetadata =
        new Builder()
            .withDeviceIdentification(deviceIdentification)
            .withOrganisationIdentification(organisationIdentification)
            .withCorrelationUid(correlationUid)
            .withMessageType(MessageType.RESUME_SCHEDULE.name())
            .withMessagePriority(messagePriority)
            .build();

    final PublicLightingRequestMessage message =
        new PublicLightingRequestMessage.Builder()
            .messageMetadata(deviceMessageMetadata)
            .request(resumeScheduleData)
            .build();

    this.messageSender.send(message);

    return correlationUid;
  }

  public ResponseMessage dequeueResumeScheduleResponse(final String correlationUid)
      throws OsgpException {
    return this.messageFinder.findMessage(correlationUid);
  }

  public String enqueueTransitionRequest(
      @Identification final String organisationIdentification,
      @Identification final String deviceIdentification,
      final TransitionMessageDataContainer transitionMessageDataContainer,
      final int messagePriority)
      throws FunctionalException {

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

    this.domainHelperService.isAllowed(organisation, device, DeviceFunction.SET_TRANSITION);
    this.domainHelperService.isInMaintenance(device);

    LOGGER.debug(
        "enqueueTransitionRequest called with organisation {}, device {} ",
        organisationIdentification,
        deviceIdentification);

    final String correlationUid =
        this.correlationIdProviderService.getCorrelationId(
            organisationIdentification, deviceIdentification);

    final MessageMetadata deviceMessageMetadata =
        new Builder()
            .withDeviceIdentification(deviceIdentification)
            .withOrganisationIdentification(organisationIdentification)
            .withCorrelationUid(correlationUid)
            .withMessageType(MessageType.SET_TRANSITION.name())
            .withMessagePriority(messagePriority)
            .build();

    final PublicLightingRequestMessage message =
        new PublicLightingRequestMessage.Builder()
            .messageMetadata(deviceMessageMetadata)
            .request(transitionMessageDataContainer)
            .build();

    this.messageSender.send(message);

    return correlationUid;
  }

  public ResponseMessage dequeueSetTransitionResponse(final String correlationUid)
      throws OsgpException {
    return this.messageFinder.findMessage(correlationUid);
  }

  /**
   * Send request message to domain component to couple an SSLD with a light measurement device.
   *
   * @param organisationIdentification Organization issuing the request.
   * @param deviceIdentification The SSLD.
   * @param lightMeasurementDeviceIdentification The light measurement device.
   * @param messagePriority The priority of the message.
   * @return Correlation UID.
   * @throws FunctionalException In case the organization is not authorized or the SSLD or LMD can
   *     not be found.
   */
  public String coupleLightMeasurementDeviceForSsld(
      final String organisationIdentification,
      final String deviceIdentification,
      final String lightMeasurementDeviceIdentification,
      final int messagePriority)
      throws FunctionalException {

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    final Device device = this.domainHelperService.findDevice(deviceIdentification);
    this.domainHelperService.isAllowed(
        organisation, device, DeviceFunction.SET_LIGHT_MEASUREMENT_DEVICE);

    final Device lightMeasurementDevice =
        this.domainHelperService.findDevice(lightMeasurementDeviceIdentification);
    LOGGER.info(
        "Found lightMeasurementDevice: {}", lightMeasurementDevice.getDeviceIdentification());

    final String correlationUid =
        this.correlationIdProviderService.getCorrelationId(
            organisationIdentification, deviceIdentification);

    final MessageMetadata deviceMessageMetadata =
        new Builder()
            .withDeviceIdentification(deviceIdentification)
            .withOrganisationIdentification(organisationIdentification)
            .withCorrelationUid(correlationUid)
            .withMessageType(MessageType.SET_LIGHT_MEASUREMENT_DEVICE.name())
            .withMessagePriority(messagePriority)
            .build();

    final PublicLightingRequestMessage message =
        new PublicLightingRequestMessage.Builder()
            .messageMetadata(deviceMessageMetadata)
            .request(lightMeasurementDeviceIdentification)
            .build();

    this.messageSender.send(message);

    return correlationUid;
  }
}
