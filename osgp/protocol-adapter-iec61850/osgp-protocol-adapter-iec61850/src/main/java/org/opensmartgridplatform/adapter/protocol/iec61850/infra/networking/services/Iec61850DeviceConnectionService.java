// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services;

import com.beanit.openiec61850.ClientAssociation;
import com.beanit.openiec61850.Fc;
import com.beanit.openiec61850.FcModelNode;
import com.beanit.openiec61850.ServerModel;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.entities.Iec61850Device;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.repositories.Iec61850DeviceRepository;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects.DeviceConnectionParameters;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.ConnectionFailureException;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.NodeReadException;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.Iec61850ClientAssociation;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.Iec61850Connection;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.Function;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.IED;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.LogicalNode;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.reporting.Iec61850ClientBaseEventListener;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.reporting.Iec61850ClientEventListenerFactory;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.reporting.Iec61850RtuDeviceReportingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@DependsOn({
  "iec61850SsldPortServer",
  "iec61850RtuPortServer",
  "responseTimeout",
  "icdFilesFolder",
  "icdFilePath",
  "isIcdFileUsed"
})
public class Iec61850DeviceConnectionService {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(Iec61850DeviceConnectionService.class);

  private static ConcurrentHashMap<String, Iec61850Connection> cache = new ConcurrentHashMap<>();

  @Autowired private Iec61850DeviceRepository iec61850DeviceRepository;

  @Autowired private Iec61850RtuDeviceReportingService iec61850RtuDeviceReportingService;

  @Autowired private Iec61850ClientEventListenerFactory iec61850ClientEventListenerFactory;

  @Autowired private Iec61850Client iec61850Client;

  @Autowired private int iec61850DefaultPort;

  @Autowired private int iec61850SsldPortServer;

  @Autowired private int iec61850RtuPortServer;

  @Autowired private int responseTimeout;

  @Autowired private String icdFilesFolder;

  @Autowired private String icdFilePath;

  @Autowired private boolean isIcdFileUsed;

  public DeviceConnection connectWithoutConnectionCaching(
      final DeviceConnectionParameters deviceConnectionParameters,
      final String organisationIdentification)
      throws ConnectionFailureException {
    return this.connect(deviceConnectionParameters, organisationIdentification, false);
  }

  public DeviceConnection connect(
      final DeviceConnectionParameters deviceConnectionParameters,
      final String organisationIdentification)
      throws ConnectionFailureException {
    return this.connect(deviceConnectionParameters, organisationIdentification, true);
  }

  public DeviceConnection connect(
      final DeviceConnectionParameters deviceConnectionParameters,
      final String organisationIdentification,
      final boolean cacheConnection)
      throws ConnectionFailureException {

    final String deviceIdentification = deviceConnectionParameters.getDeviceIdentification();
    final String serverName = deviceConnectionParameters.getServerName();
    final IED ied = deviceConnectionParameters.getIed();
    // When connection-caching is used, check if a connection is available
    // an usable for the given deviceIdentification.
    try {
      if (cacheConnection
          && this.testIfConnectionIsCachedAndAlive(
              deviceIdentification,
              ied,
              serverName,
              deviceConnectionParameters.getLogicalDevice())) {
        return new DeviceConnection(
            this.fetchIec61850Connection(deviceIdentification),
            deviceIdentification,
            organisationIdentification,
            serverName);
      }
    } catch (final ProtocolAdapterException e) {
      this.logProtocolAdapterException(deviceIdentification, e);
    }

    final InetAddress inetAddress =
        this.convertIpAddress(deviceConnectionParameters.getIpAddress());

    // Connect to obtain ClientAssociation and ServerModel.
    LOGGER.info(
        "Trying to connect to deviceIdentification: {} at IP address {} using response time-out: {}",
        deviceIdentification,
        deviceConnectionParameters.getIpAddress(),
        this.responseTimeout);
    final DateTime startTime = DateTime.now();

    // Create instance of appropriate event listener.
    Iec61850ClientBaseEventListener eventListener = null;
    try {
      eventListener =
          this.iec61850ClientEventListenerFactory.getEventListener(
              ied, deviceIdentification, organisationIdentification);
    } catch (final ProtocolAdapterException e) {
      this.logProtocolAdapterException(deviceIdentification, e);
    }

    final Iec61850Device iec61850Device =
        this.iec61850DeviceRepository.findByDeviceIdentification(deviceIdentification);

    final int port = this.determinePortForIec61850Device(ied, iec61850Device);

    // Try to connect and receive the ClientAssociation.
    final Iec61850ClientAssociation iec61850ClientAssociation =
        this.iec61850Client.connect(deviceIdentification, inetAddress, eventListener, port);
    final ClientAssociation clientAssociation = iec61850ClientAssociation.getClientAssociation();
    // Set response time-out.
    clientAssociation.setResponseTimeout(this.responseTimeout);
    // Read the ServerModel, either from the device or from a SCL file.
    ServerModel serverModel;
    try {
      serverModel = this.readServerModel(clientAssociation, deviceIdentification, iec61850Device);
    } catch (final ProtocolAdapterException e) {
      LOGGER.error(
          "ProtocolAdapterException: unable to read ServerModel for deviceIdentification {}",
          deviceIdentification,
          e);
      throw new ConnectionFailureException(e.getMessage(), e);
    }

    // Cache the connection.
    final Iec61850Connection iec61850Connection =
        new Iec61850Connection(iec61850ClientAssociation, serverModel, startTime, ied);
    if (cacheConnection) {
      this.cacheIec61850Connection(deviceIdentification, iec61850Connection);
    }

    final DeviceConnection connection =
        new DeviceConnection(
            iec61850Connection, deviceIdentification, organisationIdentification, serverName);

    final DateTime endTime = DateTime.now();
    LOGGER.info(
        "Connected to device: {}, fetched server model. Start time: {}, end time: {}, total time in milliseconds: {}",
        deviceIdentification,
        startTime,
        endTime,
        endTime.minus(startTime.getMillis()).getMillis());

    this.iec61850RtuDeviceReportingService.enableReportingForDevice(
        connection, deviceIdentification, serverName);

    return connection;
  }

  public void closeAllConnections() {
    LOGGER.warn("Closing connections for {} devices", cache.size());
    cache.values().forEach(c -> c.getClientAssociation().close());
    cache.clear();
  }

  private void logProtocolAdapterException(
      final String deviceIdentification, final ProtocolAdapterException e) {
    LOGGER.error(
        "ProtocolAdapterException: no Iec61850ClientBaseEventListener instance could be constructed, continue without event listener for deviceIdentification: {}",
        deviceIdentification,
        e);
  }

  private int determinePortForIec61850Device(final IED ied, final Iec61850Device iec61850Device) {
    final int port;
    if (iec61850Device != null && iec61850Device.getPort() != null) {
      /*
       * For devices with specific port configuration stored, use the
       * configuration from the database to override any protocol or type
       * related default configuration.
       */
      port = iec61850Device.getPort();
    } else if (IED.FLEX_OVL.equals(ied)) {
      port = this.iec61850SsldPortServer;
    } else if (IED.ZOWN_RTU.equals(ied) || IED.DA_RTU.equals(ied)) {
      port = this.iec61850RtuPortServer;
    } else {
      port = this.iec61850DefaultPort;
    }
    return port;
  }

  private boolean testIfConnectionIsCachedAndAlive(
      final String deviceIdentification,
      final IED ied,
      final String serverName,
      final String logicalDevice)
      throws ProtocolAdapterException {
    try {
      LOGGER.info(
          "Trying to find connection in cache for deviceIdentification: {}", deviceIdentification);
      final Iec61850Connection iec61850Connection =
          this.fetchIec61850Connection(deviceIdentification);
      if (iec61850Connection != null) {
        // Already connected, check if connection is still usable.
        LOGGER.info("Connection found for deviceIdentification: {}", deviceIdentification);
        // Read physical name node (only), which is much faster, but
        // requires manual reads of remote data.
        if (ied != null && logicalDevice != null) {
          final String description = this.getActualServerName(ied, serverName);
          LOGGER.info(
              "Testing if connection is alive using {}{}/{}.{} for deviceIdentification: {}",
              description,
              logicalDevice,
              LogicalNode.LOGICAL_NODE_ZERO.getDescription(),
              DataAttribute.NAME_PLATE.getDescription(),
              deviceIdentification);

          final FcModelNode modelNode =
              this.getModelNode(logicalDevice, iec61850Connection, description);
          this.iec61850Client.readNodeDataValues(
              iec61850Connection.getClientAssociation(), modelNode);
        } else {
          // Read all data values, which is much slower, but requires
          // no manual reads of remote data.
          LOGGER.info(
              "Testing if connection is alive using readAllDataValues() for deviceIdentification: {}",
              deviceIdentification);
          this.iec61850Client.readAllDataValues(iec61850Connection.getClientAssociation());
        }
        LOGGER.info(
            "Connection is still active for deviceIdentification: {}", deviceIdentification);
        return true;
      }
    } catch (final NodeReadException e) {
      LOGGER.error(
          "Connection is no longer active, removing connection from cache for deviceIdentification: "
              + deviceIdentification,
          e);
      this.disconnect(deviceIdentification);
    }
    return false;
  }

  private FcModelNode getModelNode(
      final String logicalDevice,
      final Iec61850Connection iec61850Connection,
      final String description)
      throws ProtocolAdapterException {
    final ServerModel serverModel = iec61850Connection.getServerModel();
    if (serverModel == null) {
      final String msg = String.format("ServerModel is null for logicalDevice {%s}", logicalDevice);
      throw new ProtocolAdapterException(msg);
    }
    final String objRef =
        description
            + logicalDevice
            + "/"
            + LogicalNode.LOGICAL_NODE_ZERO.getDescription()
            + "."
            + DataAttribute.NAME_PLATE.getDescription();
    final FcModelNode modelNode = (FcModelNode) serverModel.findModelNode(objRef, Fc.DC);
    if (modelNode == null) {
      final String msg = String.format("ModelNode is null for {%s}", objRef);
      throw new ProtocolAdapterException(msg);
    }
    return modelNode;
  }

  private String getActualServerName(final IED ied, final String serverName) {
    if (serverName != null && !serverName.isEmpty()) {
      return serverName;
    } else {
      // this method is only called after null-check on IED
      return ied.getDescription();
    }
  }

  private ServerModel readServerModel(
      final ClientAssociation clientAssociation,
      final String deviceIdentification,
      final Iec61850Device iec61850Device)
      throws ProtocolAdapterException {

    ServerModel serverModel;
    try {
      serverModel =
          this.readServerModelConfiguredForDevice(
              clientAssociation, deviceIdentification, iec61850Device);
      if (serverModel != null) {
        return serverModel;
      }
    } catch (final ProtocolAdapterException e) {
      LOGGER.warn(
          "Ignore exception reading server model based on per device configuration for device: {}.",
          deviceIdentification,
          e);
    }
    try {
      serverModel = this.readServerModelFromConfiguredIcdFile(clientAssociation);
      if (serverModel != null) {
        return serverModel;
      }
    } catch (final ProtocolAdapterException e) {
      LOGGER.warn("Ignore exception reading server model based on configured ICD file.", e);
    }
    LOGGER.info(
        "Reading ServerModel from device: {} using readServerModelFromDevice()",
        deviceIdentification);
    return this.iec61850Client.readServerModelFromDevice(clientAssociation);
  }

  private ServerModel readServerModelConfiguredForDevice(
      final ClientAssociation clientAssociation,
      final String deviceIdentification,
      final Iec61850Device iec61850Device)
      throws ProtocolAdapterException {

    if (iec61850Device == null || StringUtils.isBlank(iec61850Device.getIcdFilename())) {
      /*
       * No server model is configured for the device in the IEC61850
       * protocol adapter database.
       */
      return null;
    }

    if (StringUtils.isBlank(this.icdFilesFolder)) {
      throw new ProtocolAdapterException(
          "ICD files folder is not configured, unable to locate file: "
              + iec61850Device.getIcdFilename()
              + " for device: "
              + deviceIdentification);
    }

    final String filePath =
        Paths.get(this.icdFilesFolder, iec61850Device.getIcdFilename()).toString();
    LOGGER.info(
        "Reading ServerModel from SCL / ICD file: {} configured for device: {}",
        filePath,
        deviceIdentification);
    return this.iec61850Client.readServerModelFromSclFile(clientAssociation, filePath);
  }

  private ServerModel readServerModelFromConfiguredIcdFile(
      final ClientAssociation clientAssociation) throws ProtocolAdapterException {

    if (!this.isIcdFileUsed || StringUtils.isBlank(this.icdFilePath)) {
      return null;
    }

    LOGGER.info("Reading ServerModel from SCL / ICD file: {}", this.icdFilePath);
    return this.iec61850Client.readServerModelFromSclFile(clientAssociation, this.icdFilePath);
  }

  /** Closes the {@link ClientAssociation}, send a disconnect request and close the socket. */
  public void disconnect(final String deviceIdentification) {
    LOGGER.info("Trying to disconnect from deviceIdentification: {}", deviceIdentification);
    final Iec61850Connection iec61850Connection =
        this.fetchIec61850Connection(deviceIdentification);
    if (iec61850Connection != null) {
      iec61850Connection.getClientAssociation().disconnect();
      this.removeIec61850Connection(deviceIdentification);
      LOGGER.info("Disconnected from deviceIdentification: {}", deviceIdentification);
    } else {
      LOGGER.info(
          "Unable to disconnect from deviceIdentification: {}, no cached connection was found",
          deviceIdentification);
    }
  }

  public void disconnect(
      final DeviceConnection deviceConnection, final DeviceRequest deviceRequest) {
    try {
      deviceConnection
          .getConnection()
          .getIec61850ClientAssociation()
          .getClientAssociation()
          .disconnect();
      this.logDuration(deviceConnection, deviceRequest);
    } catch (final NullPointerException e) {
      LOGGER.debug("NullPointerException during disconnect()", e);
    }
  }

  private void logDuration(
      final DeviceConnection deviceConnection, final DeviceRequest deviceRequest) {
    if (deviceConnection == null) {
      return;
    }
    if (deviceRequest == null) {
      return;
    }
    final DateTime endTime = DateTime.now();
    final DateTime startTime = deviceConnection.getConnection().getConnectionStartTime();
    LOGGER.info(
        "Device: {}, messageType: {}, Start time: {}, end time: {}, total time in milliseconds: {}",
        deviceConnection.getDeviceIdentification(),
        deviceRequest.getMessageType(),
        startTime,
        endTime,
        endTime.minus(startTime.getMillis()).getMillis());
  }

  public Iec61850ClientAssociation getIec61850ClientAssociation(final String deviceIdentification) {
    final Iec61850Connection iec61850Connection =
        this.fetchIec61850Connection(deviceIdentification);
    return iec61850Connection != null ? iec61850Connection.getIec61850ClientAssociation() : null;
  }

  public ClientAssociation getClientAssociation(final String deviceIdentification) {
    final Iec61850Connection iec61850Connection =
        this.fetchIec61850Connection(deviceIdentification);
    return iec61850Connection != null ? iec61850Connection.getClientAssociation() : null;
  }

  public ServerModel getServerModel(final String deviceIdentification) {
    final Iec61850Connection iec61850Connection =
        this.fetchIec61850Connection(deviceIdentification);
    if (iec61850Connection == null) {
      return null;
    }

    return iec61850Connection.getServerModel();
  }

  public Iec61850Client getIec61850Client() {
    return this.iec61850Client;
  }

  public Iec61850Connection getIec61850Connection(final String deviceIdentification) {
    return this.fetchIec61850Connection(deviceIdentification);
  }

  public void readAllValues(final String deviceIdentification) throws NodeReadException {
    final Iec61850Connection iec61850Connection =
        this.fetchIec61850Connection(deviceIdentification);
    if (iec61850Connection == null) {
      return;
    }
    final ClientAssociation clientAssociation = iec61850Connection.getClientAssociation();
    this.iec61850Client.readAllDataValues(clientAssociation);
  }

  public void readNodeDataValues(final String deviceIdentification, final FcModelNode fcModelNode)
      throws NodeReadException {
    final Iec61850Connection iec61850Connection =
        this.fetchIec61850Connection(deviceIdentification);
    if (iec61850Connection == null) {
      return;
    }
    final ClientAssociation clientAssociation = iec61850Connection.getClientAssociation();
    this.iec61850Client.readNodeDataValues(clientAssociation, fcModelNode);
  }

  public <T> T sendCommandWithRetry(final Function<T> function, final String deviceIdentification)
      throws ProtocolAdapterException {
    return this.iec61850Client.sendCommandWithRetry(function, deviceIdentification);
  }

  private void cacheIec61850Connection(
      final String deviceIdentification, final Iec61850Connection iec61850Connection) {
    cache.put(deviceIdentification, iec61850Connection);
  }

  private Iec61850Connection fetchIec61850Connection(final String deviceIdentification) {
    final Iec61850Connection iec61850Connection = cache.get(deviceIdentification);
    if (iec61850Connection == null) {
      LOGGER.info("No connection found for device: {}", deviceIdentification);
    }
    return iec61850Connection;
  }

  private void removeIec61850Connection(final String deviceIdentification) {
    cache.remove(deviceIdentification);
  }

  private InetAddress convertIpAddress(final String ipAddress) throws ConnectionFailureException {
    try {
      if (StringUtils.isEmpty(ipAddress)) {
        throw new ConnectionFailureException("Ip address is null");
      }

      return InetAddress.getByName(ipAddress);
    } catch (final UnknownHostException e) {
      LOGGER.error("Unexpected exception during convertIpAddress", e);
      throw new ConnectionFailureException(e.getMessage(), e);
    }
  }
}
