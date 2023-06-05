// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services;

import com.beanit.openiec61850.ClientAssociation;
import com.beanit.openiec61850.ServerModel;
import java.io.Serializable;
import javax.jms.JMSException;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceMessageStatus;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceResponseHandler;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.da.rtu.DaDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.da.rtu.DaDeviceResponse;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.da.rtu.DaRtuDeviceService;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.responses.EmptyDeviceResponse;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.entities.Iec61850Device;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.repositories.Iec61850DeviceRepository;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects.DeviceConnectionParameters;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.ConnectionFailureException;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.DaRtuDeviceRequestMessageProcessor;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.Iec61850ClientAssociation;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.Iec61850Connection;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.Function;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.IED;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Iec61850DaRtuDeviceService implements DaRtuDeviceService {

  private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850DaRtuDeviceService.class);

  @Autowired private Iec61850DeviceConnectionService iec61850DeviceConnectionService;

  @Autowired private Iec61850Client iec61850Client;

  @Autowired private Iec61850DeviceRepository iec61850DeviceRepository;

  @Override
  public void getData(
      final DaDeviceRequest deviceRequest,
      final DeviceResponseHandler deviceResponseHandler,
      final DaRtuDeviceRequestMessageProcessor messageProcessor)
      throws JMSException {
    try {
      final String serverName = this.getServerName(deviceRequest);
      final ServerModel serverModel = this.connectAndRetrieveServerModel(deviceRequest, serverName);

      final ClientAssociation clientAssociation =
          this.iec61850DeviceConnectionService.getClientAssociation(
              deviceRequest.getDeviceIdentification());

      final Serializable dataResponse =
          this.handleGetData(
              new DeviceConnection(
                  new Iec61850Connection(
                      new Iec61850ClientAssociation(clientAssociation, null), serverModel),
                  deviceRequest.getDeviceIdentification(),
                  deviceRequest.getOrganisationIdentification(),
                  serverName),
              deviceRequest,
              messageProcessor);

      final DaDeviceResponse deviceResponse =
          new DaDeviceResponse(deviceRequest, DeviceMessageStatus.OK, dataResponse);

      deviceResponseHandler.handleResponse(deviceResponse);
    } catch (final ConnectionFailureException se) {
      LOGGER.error("Could not connect to device after all retries", se);

      final EmptyDeviceResponse deviceResponse =
          new EmptyDeviceResponse(deviceRequest, DeviceMessageStatus.FAILURE);

      deviceResponseHandler.handleConnectionFailure(se, deviceResponse);
    } catch (final Exception e) {
      LOGGER.error("Unexpected exception during Get Data", e);

      final EmptyDeviceResponse deviceResponse =
          new EmptyDeviceResponse(deviceRequest, DeviceMessageStatus.FAILURE);

      deviceResponseHandler.handleException(e, deviceResponse);
    }
  }

  // ======================================
  // PRIVATE DEVICE COMMUNICATION METHODS =
  // ======================================

  private ServerModel connectAndRetrieveServerModel(
      final DeviceRequest deviceRequest, final String serverName) throws ProtocolAdapterException {

    final DeviceConnectionParameters deviceConnectionParameters =
        DeviceConnectionParameters.newBuilder()
            .ipAddress(deviceRequest.getIpAddress())
            .deviceIdentification(deviceRequest.getDeviceIdentification())
            .ied(IED.DA_RTU)
            .serverName(serverName)
            .logicalDevice(LogicalDevice.RTU.getDescription() + 1)
            .build();

    this.iec61850DeviceConnectionService.connect(
        deviceConnectionParameters, deviceRequest.getOrganisationIdentification());
    return this.iec61850DeviceConnectionService.getServerModel(
        deviceRequest.getDeviceIdentification());
  }

  // ========================
  // PRIVATE HELPER METHODS =
  // ========================

  private <T> T handleGetData(
      final DeviceConnection connection,
      final DaDeviceRequest deviceRequest,
      final DaRtuDeviceRequestMessageProcessor messageProcessor)
      throws ProtocolAdapterException {
    final Function<T> function =
        messageProcessor.getDataFunction(this.iec61850Client, connection, deviceRequest);
    return this.iec61850Client.sendCommandWithRetry(
        function, deviceRequest.getDeviceIdentification());
  }

  private String getServerName(final DeviceRequest deviceRequest) {
    final Iec61850Device iec61850Device =
        this.iec61850DeviceRepository.findByDeviceIdentification(
            deviceRequest.getDeviceIdentification());
    if (iec61850Device != null && iec61850Device.getServerName() != null) {
      return iec61850Device.getServerName();
    } else {
      return IED.DA_RTU.getDescription();
    }
  }
}
