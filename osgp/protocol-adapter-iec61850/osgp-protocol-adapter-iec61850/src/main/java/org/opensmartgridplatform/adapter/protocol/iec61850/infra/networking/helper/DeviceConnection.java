// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper;

import com.beanit.openiec61850.Fc;
import com.beanit.openiec61850.FcModelNode;
import com.beanit.openiec61850.ObjectReference;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.NodeNotFoundException;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.Iec61850Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeviceConnection {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeviceConnection.class);

  private final String serverName;
  private final Iec61850Connection connection;
  private final String deviceIdentification;
  private final String organisationIdentification;

  public static final String LOGICAL_NODE_SEPARATOR = "/";
  public static final String DATA_ATTRIBUTE_SEPARATOR = ".";

  public DeviceConnection(
      final Iec61850Connection connection,
      final String deviceIdentification,
      final String organisationIdentification,
      final String serverName) {
    this.connection = connection;
    this.deviceIdentification = deviceIdentification;
    this.organisationIdentification = organisationIdentification;
    this.serverName = serverName;
  }

  /**
   * Returns a {@link NodeContainer} for the given {@link ObjectReference} data and the Functional
   * constraint.
   *
   * @throws NodeNotFoundException
   */
  public NodeContainer getFcModelNode(
      final LogicalDevice logicalDevice,
      final LogicalNode logicalNode,
      final DataAttribute dataAttribute,
      final Fc fc)
      throws NodeNotFoundException {
    final ObjectReference objectReference =
        this.createObjectReference(logicalDevice, logicalNode, dataAttribute);
    final FcModelNode fcModelNode =
        (FcModelNode) this.connection.getServerModel().findModelNode(objectReference, fc);
    if (fcModelNode == null) {
      LOGGER.error(
          "FcModelNode is null, most likely the data attribute: {} does not exist",
          dataAttribute.getDescription());
      throw new NodeNotFoundException(
          String.format("FcModelNode with objectReference %s does not exist", objectReference));
    }

    return new NodeContainer(this, fcModelNode);
  }

  /**
   * Returns a {@link NodeContainer} for the given {@link ObjectReference} data and the Functional
   * constraint.
   *
   * @throws NodeNotFoundException
   */
  public NodeContainer getFcModelNode(
      final LogicalDevice logicalDevice,
      final int logicalDeviceIndex,
      final LogicalNode logicalNode,
      final DataAttribute dataAttribute,
      final Fc fc)
      throws NodeNotFoundException {
    final ObjectReference objectReference =
        this.createObjectReference(logicalDevice, logicalDeviceIndex, logicalNode, dataAttribute);
    final FcModelNode fcModelNode =
        (FcModelNode) this.connection.getServerModel().findModelNode(objectReference, fc);
    if (fcModelNode == null) {
      LOGGER.error(
          "FcModelNode is null, most likely the data attribute: {} does not exist",
          dataAttribute.getDescription());
      throw new NodeNotFoundException(
          String.format("FcModelNode with objectReference %s does not exist", objectReference));
    }

    return new NodeContainer(this, fcModelNode);
  }

  /** Creates a correct ObjectReference. */
  private ObjectReference createObjectReference(
      final LogicalDevice logicalDevice,
      final LogicalNode logicalNode,
      final DataAttribute dataAttribute) {
    final String logicalDevicePrefix = this.serverName + logicalDevice.getDescription();

    final String objectReference =
        logicalDevicePrefix
            .concat(LOGICAL_NODE_SEPARATOR)
            .concat(logicalNode.getDescription())
            .concat(DATA_ATTRIBUTE_SEPARATOR)
            .concat(dataAttribute.getDescription());

    LOGGER.info("Device: {}, ObjectReference: {}", this.deviceIdentification, objectReference);

    return new ObjectReference(objectReference);
  }

  /** Creates a correct ObjectReference. */
  private ObjectReference createObjectReference(
      final LogicalDevice logicalDevice,
      final int logicalDeviceIndex,
      final LogicalNode logicalNode,
      final DataAttribute dataAttribute) {
    final String logicalDevicePrefix =
        this.serverName + logicalDevice.getDescription() + logicalDeviceIndex;

    final String objectReference =
        logicalDevicePrefix
            .concat(LOGICAL_NODE_SEPARATOR)
            .concat(logicalNode.getDescription())
            .concat(DATA_ATTRIBUTE_SEPARATOR)
            .concat(dataAttribute.getDescription());

    LOGGER.info("Device: {}, ObjectReference: {}", this.deviceIdentification, objectReference);

    return new ObjectReference(objectReference);
  }

  // GETTERS AND SETTERS

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  public String getOrganisationIdentification() {
    return this.organisationIdentification;
  }

  public Iec61850Connection getConnection() {
    return this.connection;
  }
}
