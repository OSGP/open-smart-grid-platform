//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.microgrids.application.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.opensmartgridplatform.domain.core.entities.RtuDevice;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.dto.valueobjects.microgrids.GetDataRequestDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.GetDataResponseDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.GetDataSystemIdentifierDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.MeasurementDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.MeasurementFilterDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.SystemFilterDto;
import org.opensmartgridplatform.shared.domain.services.CorrelationIdProviderService;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "domainMicrogridsCommunicationRecoveryService")
@Transactional(value = "transactionManager")
public class CommunicationRecoveryService extends BaseService {

  private static final Logger LOGGER = LoggerFactory.getLogger(CommunicationRecoveryService.class);

  private static final int SYSTEM_ID = 1;
  private static final String SYSTEM_TYPE = "RTU";
  private static final int MEASUREMENT_ID = 1;
  private static final String MEASUREMENT_NODE = "Alm1";
  private static final double MEASUREMENT_VALUE_ALARM_ON = 1.0;

  @Autowired private CorrelationIdProviderService correlationIdProviderService;

  @Autowired
  @Qualifier("domainMicrogridsAdHocManagementService")
  private AdHocManagementService adHocManagementService;

  /**
   * Send a signal that the connection with the device has been lost. This is done by putting a
   * GetDataResponse on the queue with an alarm value. When this response is received by the
   * webservice adapter, it can send a notification to the client.
   */
  public void signalConnectionLost(final RtuDevice rtu) {
    LOGGER.info("Sending connection lost signal for device {}.", rtu.getDeviceIdentification());

    final GetDataResponseDto dataResponse =
        new GetDataResponseDto(
            Arrays.asList(
                new GetDataSystemIdentifierDto(
                    SYSTEM_ID,
                    SYSTEM_TYPE,
                    Arrays.asList(
                        new MeasurementDto(
                            MEASUREMENT_ID,
                            MEASUREMENT_NODE,
                            0,
                            new DateTime(DateTimeZone.UTC),
                            MEASUREMENT_VALUE_ALARM_ON)))),
            null);

    final String correlationUid = this.createCorrelationUid(rtu);
    final String organisationIdentification = rtu.getOwner().getOrganisationIdentification();
    final String deviceIdentification = rtu.getDeviceIdentification();

    final CorrelationIds ids =
        new CorrelationIds(organisationIdentification, deviceIdentification, correlationUid);
    this.adHocManagementService.handleInternalDataResponse(
        dataResponse, ids, DeviceFunction.GET_DATA.toString());
  }

  public void restoreCommunication(final RtuDevice rtu) {
    LOGGER.info("Restoring communication for device {}.", rtu.getDeviceIdentification());

    if (rtu.getOwner() == null) {
      LOGGER.warn(
          "Device {} has no owner. Skipping communication recovery.",
          rtu.getDeviceIdentification());
      return;
    }

    final RequestMessage message = this.createMessage(rtu);
    this.osgpCoreRequestMessageSender.send(
        message, DeviceFunction.GET_DATA.toString(), rtu.getIpAddress());
  }

  private RequestMessage createMessage(final RtuDevice rtu) {
    LOGGER.debug("Creating message for device {}.", rtu.getDeviceIdentification());

    final String correlationUid = this.createCorrelationUid(rtu);
    final String organisationIdentification = rtu.getOwner().getOrganisationIdentification();
    final String deviceIdentification = rtu.getDeviceIdentification();
    final GetDataRequestDto request = this.createRequest(rtu);

    return new RequestMessage(
        correlationUid, organisationIdentification, deviceIdentification, request);
  }

  private String createCorrelationUid(final RtuDevice rtu) {
    LOGGER.debug(
        "Creating correlation uid for device {}, with owner {}",
        rtu.getDeviceIdentification(),
        rtu.getOwner().getOrganisationIdentification());

    final String correlationUid =
        this.correlationIdProviderService.getCorrelationId(
            rtu.getOwner().getOrganisationIdentification(), rtu.getDeviceIdentification());

    LOGGER.debug("Correlation uid {} created.", correlationUid);

    return correlationUid;
  }

  private GetDataRequestDto createRequest(final RtuDevice rtu) {
    LOGGER.debug("Creating data request for rtu {}.", rtu.getDeviceIdentification());

    final List<MeasurementFilterDto> measurementFilters = new ArrayList<>();
    measurementFilters.add(new MeasurementFilterDto(MEASUREMENT_ID, MEASUREMENT_NODE, false));

    final List<SystemFilterDto> systemFilters = new ArrayList<>();
    systemFilters.add(new SystemFilterDto(SYSTEM_ID, SYSTEM_TYPE, measurementFilters, false));

    return new GetDataRequestDto(systemFilters);
  }
}
