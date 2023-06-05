// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.application.services;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.entities.Iec61850DeviceReportGroup;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.repositories.Iec61850DeviceReportGroupRepository;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.DeviceResponseMessageSender;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.OsgpRequestMessageSender;
import org.opensmartgridplatform.core.db.api.iec61850.entities.DeviceOutputSetting;
import org.opensmartgridplatform.core.db.api.iec61850.entities.LightMeasurementDevice;
import org.opensmartgridplatform.core.db.api.iec61850.entities.ProtocolInfo;
import org.opensmartgridplatform.core.db.api.iec61850.entities.Ssld;
import org.opensmartgridplatform.core.db.api.iec61850.repositories.LmdDataRepository;
import org.opensmartgridplatform.core.db.api.iec61850.repositories.ProtocolInfoRepository;
import org.opensmartgridplatform.core.db.api.iec61850.repositories.SsldDataRepository;
import org.opensmartgridplatform.dto.da.GetPQValuesResponseDto;
import org.opensmartgridplatform.dto.valueobjects.EventNotificationDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.GetDataResponseDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "iec61850DeviceManagementService")
public class DeviceManagementService {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeviceManagementService.class);
  private static final String NO_ORGANISATION = "no-organisation";
  private static final String NO_CORRELATION_UID = "no-correlationUid";

  @Autowired private SsldDataRepository ssldDataRepository;

  @Autowired private LmdDataRepository lmdDataRepository;

  @Autowired private ProtocolInfoRepository protocolInfoRepository;

  @Autowired private Iec61850DeviceReportGroupRepository deviceReportGroupRepository;

  @Autowired private OsgpRequestMessageSender osgpRequestMessageSender;

  @Autowired private DeviceResponseMessageSender responseSender;

  private ProtocolInfo protocolInfo;

  public DeviceManagementService() {
    // Parameterless constructor required for transactions...
  }

  @PostConstruct
  public void init() {
    this.protocolInfo =
        this.protocolInfoRepository.findByProtocolAndProtocolVersion("IEC61850", "1.0");
  }

  @Transactional(value = "iec61850OsgpCoreDbApiTransactionManager", readOnly = true)
  public List<LightMeasurementDevice> findAllLightMeasurementDevices() {
    return this.lmdDataRepository.findAll();
  }

  /**
   * Find the 4 real IEC61850 light measurement devices. These devices are using digital input 1, 2,
   * 3 and 4.
   *
   * @return List of 4 {@link LightMeasurementDevice}.
   */
  @Transactional(value = "iec61850OsgpCoreDbApiTransactionManager", readOnly = true)
  public List<LightMeasurementDevice> findRealLightMeasurementDevices() {
    final short start = 1;
    final short end = 4;

    return this.lmdDataRepository.findByProtocolInfoAndDigitalInputBetween(
        this.protocolInfo, start, end);
  }

  public LightMeasurementDevice findLightMeasurementDevice(final String deviceIdentification) {
    return this.lmdDataRepository.findByDeviceIdentification(deviceIdentification);
  }

  /**
   * Send an event notification to OSGP Core.
   *
   * @param deviceIdentification The identification of the device.
   * @param eventNotifications The event notifications.
   * @throws ProtocolAdapterException In case the device can not be found in the database.
   */
  @Transactional(value = "iec61850OsgpCoreDbApiTransactionManager", readOnly = true)
  public void addEventNotifications(
      final String deviceIdentification, final List<EventNotificationDto> eventNotifications)
      throws ProtocolAdapterException {

    final Ssld ssldDevice =
        this.ssldDataRepository.findByDeviceIdentification(deviceIdentification);
    if (ssldDevice == null) {
      final LightMeasurementDevice lmd =
          this.lmdDataRepository.findByDeviceIdentification(deviceIdentification);
      if (lmd == null) {

        throw new ProtocolAdapterException(
            "Unable to find device using deviceIdentification: " + deviceIdentification);
      }
    }

    LOGGER.info(
        "addEventNotifications called for device {}: {}", deviceIdentification, eventNotifications);

    final RequestMessage requestMessage =
        new RequestMessage(
            NO_CORRELATION_UID,
            NO_ORGANISATION,
            deviceIdentification,
            new ArrayList<>(eventNotifications));

    this.osgpRequestMessageSender.send(requestMessage, MessageType.EVENT_NOTIFICATION.name());
  }

  /**
   * Get the device output setting (relay configuration) for a given device.
   *
   * @param deviceIdentification The device identification.
   * @return The {@link DeviceOutputSetting} for the device.
   * @throws ProtocolAdapterException In case the device can not be found in the database.
   */
  @Transactional(value = "iec61850OsgpCoreDbApiTransactionManager", readOnly = true)
  public List<DeviceOutputSetting> getDeviceOutputSettings(final String deviceIdentification)
      throws ProtocolAdapterException {

    final Ssld ssldDevice =
        this.ssldDataRepository.findByDeviceIdentification(deviceIdentification);
    if (ssldDevice == null) {
      throw new ProtocolAdapterException(
          "Unable to find device using deviceIdentification: " + deviceIdentification);
    }

    return ssldDevice.getOutputSettings();
  }

  public void sendMeasurements(
      final String deviceIdentification, final GetDataResponseDto response) {
    // Correlation ID is generated @ WS adapter, domain+version is hard-coded for now
    final ProtocolResponseMessage responseMessage =
        ProtocolResponseMessage.newBuilder()
            .messageMetadata(
                MessageMetadata.newBuilder()
                    .withDeviceIdentification(deviceIdentification)
                    .withOrganisationIdentification(NO_ORGANISATION)
                    .withCorrelationUid(NO_CORRELATION_UID)
                    .withMessageType(MessageType.GET_DATA.name())
                    .withDomain("MICROGRIDS")
                    .withDomainVersion("1.0")
                    .withMessagePriority(0)
                    .withScheduled(false)
                    .build())
            .result(ResponseMessageResultType.OK)
            .dataObject(response)
            .build();
    this.responseSender.send(responseMessage);
  }

  public void sendPqValues(
      final String deviceIdentification,
      final String reportDataSet,
      final GetPQValuesResponseDto response) {
    final Iec61850DeviceReportGroup deviceReportGroup =
        this.deviceReportGroupRepository.findByDeviceIdentificationAndReportDataSet(
            deviceIdentification, reportDataSet);
    final ProtocolResponseMessage responseMessage =
        ProtocolResponseMessage.newBuilder()
            .messageMetadata(
                MessageMetadata.newBuilder()
                    .withDeviceIdentification(deviceIdentification)
                    .withOrganisationIdentification(NO_ORGANISATION)
                    .withCorrelationUid(NO_CORRELATION_UID)
                    .withMessageType(MessageType.GET_POWER_QUALITY_VALUES.name())
                    .withDomain(deviceReportGroup.getDomain())
                    .withDomainVersion(deviceReportGroup.getDomainVersion())
                    .withMessagePriority(0)
                    .withScheduled(false)
                    .build())
            .result(ResponseMessageResultType.OK)
            .dataObject(response)
            .build();
    this.responseSender.send(responseMessage);
  }
}
