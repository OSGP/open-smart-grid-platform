// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services;

import com.beanit.openiec61850.ClientAssociation;
import com.beanit.openiec61850.ServerModel;
import java.util.ArrayList;
import java.util.List;
import javax.jms.JMSException;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceMessageStatus;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceResponseHandler;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.rtu.RtuDeviceService;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.rtu.requests.GetDataDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.rtu.requests.SetDataDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.responses.EmptyDeviceResponse;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.responses.GetDataDeviceResponse;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.entities.Iec61850Device;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.repositories.Iec61850DeviceRepository;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects.DeviceConnectionParameters;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects.DeviceMessageLog;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.ConnectionFailureException;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.Iec61850ClientAssociation;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.Iec61850Connection;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.SystemService;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.Function;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.IED;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import org.opensmartgridplatform.dto.valueobjects.microgrids.GetDataRequestDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.GetDataResponseDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.GetDataSystemIdentifierDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.SetDataRequestDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.SetDataSystemIdentifierDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.SystemFilterDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Iec61850RtuDeviceService implements RtuDeviceService {

  private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850RtuDeviceService.class);

  @Autowired private Iec61850DeviceConnectionService iec61850DeviceConnectionService;

  @Autowired private Iec61850SystemServiceFactory systemServiceFactory;

  @Autowired private Iec61850Client iec61850Client;

  @Autowired private Iec61850DeviceRepository iec61850DeviceRepository;

  @Override
  public void getData(
      final GetDataDeviceRequest deviceRequest, final DeviceResponseHandler deviceResponseHandler)
      throws JMSException {
    try {
      final String serverName = this.getServerName(deviceRequest);
      final ServerModel serverModel = this.connectAndRetrieveServerModel(deviceRequest, serverName);

      final ClientAssociation clientAssociation =
          this.iec61850DeviceConnectionService.getClientAssociation(
              deviceRequest.getDeviceIdentification());

      final GetDataResponseDto getDataResponse =
          this.handleGetData(
              new DeviceConnection(
                  new Iec61850Connection(
                      new Iec61850ClientAssociation(clientAssociation, null), serverModel),
                  deviceRequest.getDeviceIdentification(),
                  deviceRequest.getOrganisationIdentification(),
                  serverName),
              deviceRequest);

      if (getDataResponse == null) {
        throw new ProtocolAdapterException("No valid response received during GetData");
      }

      final GetDataDeviceResponse deviceResponse =
          new GetDataDeviceResponse(deviceRequest, DeviceMessageStatus.OK, getDataResponse);

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

  @Override
  public void setData(
      final SetDataDeviceRequest deviceRequest, final DeviceResponseHandler deviceResponseHandler)
      throws JMSException {
    try {
      final String serverName = this.getServerName(deviceRequest);
      final ServerModel serverModel = this.connectAndRetrieveServerModel(deviceRequest, serverName);
      final ClientAssociation clientAssociation =
          this.iec61850DeviceConnectionService.getClientAssociation(
              deviceRequest.getDeviceIdentification());

      this.handleSetData(
          new DeviceConnection(
              new Iec61850Connection(
                  new Iec61850ClientAssociation(clientAssociation, null), serverModel),
              deviceRequest.getDeviceIdentification(),
              deviceRequest.getOrganisationIdentification(),
              serverName),
          deviceRequest);

      final EmptyDeviceResponse deviceResponse =
          new EmptyDeviceResponse(deviceRequest, DeviceMessageStatus.OK);

      deviceResponseHandler.handleResponse(deviceResponse);
    } catch (final ConnectionFailureException se) {
      LOGGER.error("Could not connect to device after all retries", se);

      final EmptyDeviceResponse deviceResponse =
          new EmptyDeviceResponse(deviceRequest, DeviceMessageStatus.FAILURE);

      deviceResponseHandler.handleConnectionFailure(se, deviceResponse);
    } catch (final Exception e) {
      LOGGER.error("Unexpected exception during Set Data", e);

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
            .networkAddress(deviceRequest.getNetworkAddress())
            .deviceIdentification(deviceRequest.getDeviceIdentification())
            .ied(IED.ZOWN_RTU)
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

  private GetDataResponseDto handleGetData(
      final DeviceConnection connection, final GetDataDeviceRequest deviceRequest)
      throws ProtocolAdapterException {

    final GetDataRequestDto requestedData = deviceRequest.getDataRequest();

    final Function<GetDataResponseDto> function =
        new Function<GetDataResponseDto>() {

          @Override
          public GetDataResponseDto apply(final DeviceMessageLog deviceMessageLog)
              throws ProtocolAdapterException {

            final List<GetDataSystemIdentifierDto> identifiers = new ArrayList<>();
            for (final SystemFilterDto systemFilter : requestedData.getSystemFilters()) {
              final SystemService systemService =
                  Iec61850RtuDeviceService.this.systemServiceFactory.getSystemService(systemFilter);
              final GetDataSystemIdentifierDto getDataSystemIdentifier =
                  systemService.getData(
                      systemFilter, Iec61850RtuDeviceService.this.iec61850Client, connection);
              identifiers.add(getDataSystemIdentifier);
            }

            return new GetDataResponseDto(identifiers, null);
          }
        };

    return this.iec61850Client.sendCommandWithRetry(
        function, deviceRequest.getDeviceIdentification());
  }

  private void handleSetData(
      final DeviceConnection connection, final SetDataDeviceRequest deviceRequest)
      throws ProtocolAdapterException {

    final SetDataRequestDto setDataRequest = deviceRequest.getSetDataRequest();

    final Function<Void> function =
        new Function<Void>() {
          @Override
          public Void apply(final DeviceMessageLog deviceMessageLog)
              throws ProtocolAdapterException {

            for (final SetDataSystemIdentifierDto identifier :
                setDataRequest.getSetDataSystemIdentifiers()) {

              final SystemService systemService =
                  Iec61850RtuDeviceService.this.systemServiceFactory.getSystemService(
                      identifier.getSystemType());

              systemService.setData(
                  identifier, Iec61850RtuDeviceService.this.iec61850Client, connection);
            }

            return null;
          }
        };

    this.iec61850Client.sendCommandWithRetry(function, deviceRequest.getDeviceIdentification());
  }

  private String getServerName(final DeviceRequest deviceRequest) {
    final Iec61850Device iec61850Device =
        this.iec61850DeviceRepository.findByDeviceIdentification(
            deviceRequest.getDeviceIdentification());
    if (iec61850Device != null && iec61850Device.getServerName() != null) {
      return iec61850Device.getServerName();
    } else {
      return IED.ZOWN_RTU.getDescription();
    }
  }
}
