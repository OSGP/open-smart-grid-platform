// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking;

import com.beanit.openiec61850.ClientAssociation;
import com.beanit.openiec61850.ClientSap;
import com.beanit.openiec61850.FcModelNode;
import com.beanit.openiec61850.SclParseException;
import com.beanit.openiec61850.SclParser;
import com.beanit.openiec61850.ServerModel;
import com.beanit.openiec61850.ServiceError;
import java.io.IOException;
import java.net.InetAddress;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects.DeviceMessageLog;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.ConnectionFailureException;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.NodeReadException;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.NodeWriteException;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.ConnectionState;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.Function;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.IED;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.reporting.Iec61850ClientBaseEventListener;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.reporting.Iec61850ClientEventListenerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Iec61850Client {

  private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850Client.class);
  private static final String COULD_NOT_EXECUTE_COMMAND = "Could not execute command";

  @Autowired private int iec61850PortClient;

  @Autowired private int iec61850PortClientLocal;

  @Autowired private int iec61850SsldPortServer;

  @Autowired private int iec61850RtuPortServer;

  @Autowired private int maxRedeliveriesForIec61850Requests;

  @Autowired private int maxRetryCount;

  @Autowired private int connectionTimeout;

  @PostConstruct
  private void init() {
    LOGGER.info(
        "portClient: {}, portClientLocal: {}, iec61850SsldPortServer: {}, iec61850RtuPortServer: {}, maxRetryCount: {}, maxRedeliveriesForIec61850Requests: {}, connectionTimeout: {}",
        this.iec61850PortClient,
        this.iec61850PortClientLocal,
        this.iec61850SsldPortServer,
        this.iec61850RtuPortServer,
        this.maxRetryCount,
        this.maxRedeliveriesForIec61850Requests,
        this.connectionTimeout);
  }

  /**
   * Connect to a given device. This will try to establish the {@link ClientAssociation} between
   * client and IED.
   *
   * @param deviceIdentification The device identification.
   * @param ipAddress The IP address of the device.
   * @param reportListener The report listener instance which can be created using {@link
   *     Iec61850ClientEventListenerFactory}.
   * @param port The port number of the IED.
   * @return An {@link Iec61850ClientAssociation} instance.
   * @throws ConnectionFailureException In case the connection to the device could not be
   *     established.
   */
  public Iec61850ClientAssociation connect(
      final String deviceIdentification,
      final InetAddress ipAddress,
      final Iec61850ClientBaseEventListener reportListener,
      final int port)
      throws ConnectionFailureException {
    // Alternatively you could use ClientSap(SocketFactory factory) to e.g.
    // connect using SSL.
    final ClientSap clientSap = new ClientSap();
    final Iec61850ClientAssociation clientAssociation;
    LOGGER.info(
        "Attempting to connect to server: {} on port: {}, max redelivery count: {} and max retry count: {}",
        ipAddress.getHostAddress(),
        port,
        this.maxRedeliveriesForIec61850Requests,
        this.maxRetryCount);

    try {
      clientSap.setResponseTimeout(this.connectionTimeout);
      final ClientAssociation association =
          clientSap.associate(ipAddress, port, null, reportListener);
      clientAssociation = new Iec61850ClientAssociation(association, reportListener);
    } catch (final IOException e) {
      // An IOException will always indicate a fatal exception. It
      // indicates that the association was closed and
      // cannot be recovered. You will need to create a new association
      // using ClientSap.associate() in order to
      // reconnect.
      LOGGER.error("Error connecting to device: {}", deviceIdentification, e);
      throw new ConnectionFailureException(e.getMessage(), e);
    }

    LOGGER.info("Connected to device: {}", deviceIdentification);
    return clientAssociation;
  }

  /**
   * Disconnect from the device.
   *
   * @param clientAssociation The {@link ClientAssociation} instance.
   * @param deviceIdentification The device identification.
   */
  public void disconnect(
      final ClientAssociation clientAssociation, final String deviceIdentification) {
    LOGGER.info("disconnecting from device: {}...", deviceIdentification);
    clientAssociation.disconnect();
    LOGGER.info("disconnected from device: {}", deviceIdentification);
  }

  /**
   * Read the device model from the device.
   *
   * @param clientAssociation The {@link ClientAssociation} instance.
   * @return A {@link ServerModel} instance.
   * @throws ProtocolAdapterException
   */
  public ServerModel readServerModelFromDevice(final ClientAssociation clientAssociation)
      throws ProtocolAdapterException {
    try {
      LOGGER.debug("Start reading server model from device");
      // RetrieveModel() will call all GetDirectory and GetDefinition ACSI
      // services needed to get the complete server model.
      final ServerModel serverModel = clientAssociation.retrieveModel();
      LOGGER.debug("Completed reading server model from device");
      return serverModel;
    } catch (final ServiceError e) {
      clientAssociation.close();
      throw new ProtocolAdapterException("Service Error requesting model.", e);
    } catch (final IOException e) {
      throw new ProtocolAdapterException("Fatal IOException requesting model.", e);
    }
  }

  /**
   * Use an ICD file (model file) to read the device model.
   *
   * @param clientAssociation Instance of {@link ClientAssociation}
   * @param filePath "../sampleServer/sampleModel.icd"
   * @return Instance of {@link ServerModel}
   * @throws ProtocolAdapterException In case the file path is empty.
   */
  public ServerModel readServerModelFromSclFile(
      final ClientAssociation clientAssociation, final String filePath)
      throws ProtocolAdapterException {
    if (StringUtils.isEmpty(filePath)) {
      throw new ProtocolAdapterException("File path is empty");
    }

    try {
      final ServerModel serverModel = SclParser.parse(filePath).get(0);
      clientAssociation.setServerModel(serverModel);
      return serverModel;
    } catch (final SclParseException e) {
      throw new ProtocolAdapterException("Error parsing SCL file: " + filePath, e);
    }
  }

  /**
   * Read the values of all data attributes of all data objects of all Logical Nodes.
   *
   * @param clientAssociation An {@link ClientAssociation} instance.
   * @throws NodeReadException In case the read action fails.
   */
  public void readAllDataValues(final ClientAssociation clientAssociation)
      throws NodeReadException {
    try {
      LOGGER.debug("Start getAllDataValues from device");
      clientAssociation.getAllDataValues();
      LOGGER.debug("Completed getAllDataValues from device");
    } catch (final ServiceError e) {
      LOGGER.error("ServiceError during readAllDataValues", e);
      throw new NodeReadException(e.getMessage(), e, ConnectionState.OK);
    } catch (final IOException e) {
      LOGGER.error("IOException during readAllDataValues", e);
      throw new NodeReadException(e.getMessage(), e, ConnectionState.BROKEN);
    }
  }

  /**
   * Read the values of all data attributes of a data object of a Logical Node.
   *
   * @param clientAssociation An {@link ClientAssociation} instance.
   * @param modelNode The {@link FcModelNode} to read.
   * @throws NodeReadException In case the read action fails.
   */
  public void readNodeDataValues(
      final ClientAssociation clientAssociation, final FcModelNode modelNode)
      throws NodeReadException {
    try {
      clientAssociation.getDataValues(modelNode);
    } catch (final ServiceError e) {
      LOGGER.error("ServiceError during readNodeDataValues", e);
      throw new NodeReadException(e.getMessage(), e, ConnectionState.OK);
    } catch (final IOException e) {
      LOGGER.error("IOException during readNodeDataValues", e);
      throw new NodeReadException(e.getMessage(), e, ConnectionState.BROKEN);
    }
  }

  /**
   * Executes the apply method of the given {@link Function} with retries.
   *
   * @return The given T.
   */
  public <T> T sendCommandWithRetry(final Function<T> function, final String deviceIdentification)
      throws ProtocolAdapterException {
    T output = null;

    try {
      output = function.apply(null);
    } catch (final NodeWriteException | NodeReadException e) {
      if (ConnectionState.OK.equals(e.getConnectionState())) {
        // ServiceError means we have to retry.
        LOGGER.error("Caught ServiceError, retrying", e);
        this.sendCommandWithRetry(function, deviceIdentification, 1, null);
      } else {
        LOGGER.error("Caught IOException, connection with device is broken.", e);
      }
    } catch (final ProtocolAdapterException e) {
      throw e;
    } catch (final Exception e) {
      throw new ProtocolAdapterException(
          e.getMessage() == null ? COULD_NOT_EXECUTE_COMMAND : e.getMessage(), e);
    }

    return output;
  }

  /**
   * Executes the apply method of the given {@link Function} with retries and message logging.
   *
   * @return The given T.
   */
  public <T> T sendCommandWithRetry(
      final Function<T> function, final String functionName, final String deviceIdentification)
      throws ProtocolAdapterException {
    T output = null;
    final DeviceMessageLog deviceMessageLog =
        new DeviceMessageLog(IED.FLEX_OVL, LogicalDevice.LIGHTING, functionName);
    try {
      output = function.apply(deviceMessageLog);
    } catch (final NodeWriteException | NodeReadException e) {
      if (ConnectionState.OK.equals(e.getConnectionState())) {
        // ServiceError means we have to retry.
        LOGGER.error("Caught ServiceError, retrying", e);
        this.sendCommandWithRetry(function, deviceIdentification, 1, deviceMessageLog);
      } else {
        LOGGER.error("Caught IOException, connection with device is broken.", e);
      }
    } catch (final ProtocolAdapterException e) {
      throw e;
    } catch (final Exception e) {
      throw new ProtocolAdapterException(
          e.getMessage() == null ? COULD_NOT_EXECUTE_COMMAND : e.getMessage(), e);
    }

    return output;
  }

  /** Basically the same as sendCommandWithRetry, but with a retry parameter. */
  private <T> T sendCommandWithRetry(
      final Function<T> function,
      final String deviceIdentification,
      final int retryCount,
      final DeviceMessageLog deviceMessageLog)
      throws ProtocolAdapterException {

    T output = null;

    LOGGER.info(
        "retry: {} of {} for deviceIdentification: {}",
        retryCount,
        this.maxRetryCount,
        deviceIdentification);

    try {
      output = function.apply(deviceMessageLog);
    } catch (final ProtocolAdapterException e) {
      if (retryCount >= this.maxRetryCount) {
        throw e;
      } else {
        this.sendCommandWithRetry(function, deviceIdentification, retryCount + 1, deviceMessageLog);
      }
    } catch (final Exception e) {
      throw new ProtocolAdapterException(
          e.getMessage() == null ? COULD_NOT_EXECUTE_COMMAND : e.getMessage(), e);
    }

    return output;
  }
}
