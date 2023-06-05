// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.iec61850.server.logicaldevices;

import com.beanit.openiec61850.BasicDataAttribute;
import com.beanit.openiec61850.BdaBoolean;
import com.beanit.openiec61850.BdaFloat32;
import com.beanit.openiec61850.BdaInt32;
import com.beanit.openiec61850.BdaInt64;
import com.beanit.openiec61850.BdaInt8;
import com.beanit.openiec61850.BdaQuality;
import com.beanit.openiec61850.BdaTimestamp;
import com.beanit.openiec61850.BdaType;
import com.beanit.openiec61850.BdaVisibleString;
import com.beanit.openiec61850.ServerModel;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.opensmartgridplatform.simulator.protocol.iec61850.server.BasicDataAttributesHelper;
import org.opensmartgridplatform.simulator.protocol.iec61850.server.QualityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public abstract class LogicalDevice {

  private static final Logger LOGGER = LoggerFactory.getLogger(LogicalDevice.class);

  private final String physicalDeviceName;
  private final String logicalDeviceName;
  private ServerModel serverModel;

  public LogicalDevice(
      final String physicalDeviceName,
      final String logicalDeviceName,
      final ServerModel serverModel) {
    this.physicalDeviceName = physicalDeviceName;
    this.logicalDeviceName = logicalDeviceName;
    this.serverModel = serverModel;
  }

  public void refreshServerModel(final ServerModel serverModel) {
    this.serverModel = serverModel;
  }

  public abstract List<BasicDataAttribute> getAttributesAndSetValues(Date timestamp);

  public BasicDataAttribute getAttributeAndSetValue(
      final LogicalDeviceNode node, final String value) {

    if (node.getType().equals(BdaType.BOOLEAN)) {
      return this.setBoolean(node, Boolean.parseBoolean(value));
    }

    if (node.getType().equals(BdaType.FLOAT32)) {
      return this.setFixedFloat(node, Float.parseFloat(value));
    }

    if (node.getType().equals(BdaType.INT8)) {
      return this.setByte(node, Byte.parseByte(value));
    }

    if (node.getType().equals(BdaType.INT32)) {
      return this.setInt(node, Integer.parseInt(value));
    }

    if (node.getType().equals(BdaType.QUALITY)) {
      return this.setQuality(node, QualityType.valueOf(value));
    }

    if (node.getType().equals(BdaType.TIMESTAMP)) {
      return this.setTime(node, this.parseDate(value));
    }

    throw this.nodeTypeNotConfiguredException(node);
  }

  public BasicDataAttribute getBasicDataAttribute(final LogicalDeviceNode node) {
    return (BasicDataAttribute)
        this.serverModel.findModelNode(this.createNodeName(node), node.getFc());
  }

  public String getPhysicalDeviceName() {
    return this.physicalDeviceName;
  }

  public String getLogicalDeviceName() {
    return this.logicalDeviceName;
  }

  public String getCombinedName() {
    return this.physicalDeviceName + this.getLogicalDeviceName();
  }

  private String createNodeName(final LogicalDeviceNode node) {
    return this.getCombinedName() + "/" + node.getDescription();
  }

  protected BasicDataAttribute incrementInt(final LogicalDeviceNode node) {
    final BdaInt32 value =
        (BdaInt32) this.serverModel.findModelNode(this.createNodeName(node), node.getFc());
    value.setValue(value.getValue() + 1);
    return value;
  }

  protected BasicDataAttribute setTime(final LogicalDeviceNode node, final Date date) {
    final BdaTimestamp value =
        (BdaTimestamp) this.serverModel.findModelNode(this.createNodeName(node), node.getFc());
    value.setDate(date);
    return value;
  }

  protected BasicDataAttribute setRandomFloat(
      final LogicalDeviceNode node, final int min, final int max) {
    final BdaFloat32 value =
        (BdaFloat32) this.serverModel.findModelNode(this.createNodeName(node), node.getFc());
    value.setFloat((float) ThreadLocalRandom.current().nextInt(min, max));
    return value;
  }

  protected BasicDataAttribute setFixedFloat(final LogicalDeviceNode node, final float val) {
    final BdaFloat32 value =
        (BdaFloat32) this.serverModel.findModelNode(this.createNodeName(node), node.getFc());
    value.setFloat(val);
    return value;
  }

  protected BasicDataAttribute setRandomByte(
      final LogicalDeviceNode node, final int min, final int max) {
    final BdaInt8 value =
        (BdaInt8) this.serverModel.findModelNode(this.createNodeName(node), node.getFc());
    value.setValue((byte) ThreadLocalRandom.current().nextInt(min, max));
    return value;
  }

  protected BasicDataAttribute setByte(final LogicalDeviceNode node, final byte val) {
    final BdaInt8 value =
        (BdaInt8) this.serverModel.findModelNode(this.createNodeName(node), node.getFc());
    value.setValue(val);
    return value;
  }

  protected BasicDataAttribute setFixedInt(final LogicalDeviceNode node, final int val) {
    final BdaInt64 value =
        (BdaInt64) this.serverModel.findModelNode(this.createNodeName(node), node.getFc());
    value.setValue((byte) val);
    return value;
  }

  protected BasicDataAttribute setRandomInt(
      final LogicalDeviceNode node, final int min, final int max) {
    final BdaInt32 value =
        (BdaInt32) this.serverModel.findModelNode(this.createNodeName(node), node.getFc());
    value.setValue(ThreadLocalRandom.current().nextInt(min, max));
    return value;
  }

  protected BasicDataAttribute setInt(final LogicalDeviceNode node, final int val) {
    final BdaInt32 value =
        (BdaInt32) this.serverModel.findModelNode(this.createNodeName(node), node.getFc());
    value.setValue(val);
    return value;
  }

  protected BasicDataAttribute setBoolean(final LogicalDeviceNode node, final boolean b) {
    final BdaBoolean value =
        (BdaBoolean) this.serverModel.findModelNode(this.createNodeName(node), node.getFc());
    value.setValue(b);
    return value;
  }

  protected BasicDataAttribute setVisibleString(final LogicalDeviceNode node, final byte[] d) {
    final BdaVisibleString value =
        (BdaVisibleString) this.serverModel.findModelNode(this.createNodeName(node), node.getFc());
    value.setValue(d);
    return value;
  }

  protected BasicDataAttribute setQuality(final LogicalDeviceNode node, final QualityType q) {
    final BdaQuality value =
        (BdaQuality) this.serverModel.findModelNode(this.createNodeName(node), node.getFc());
    value.setValue(this.shortToByteArray(q.getValue()));
    return value;
  }

  private byte[] shortToByteArray(final short value) {
    return ByteBuffer.allocate(2).putShort(value).array();
  }

  protected Date parseDate(final String date) {
    if (StringUtils.isEmpty(date)) {
      return null;
    }
    return BasicDataAttributesHelper.parseDate(date);
  }

  protected IllegalArgumentException illegalNodeException(final LogicalDeviceNode node) {
    return new IllegalArgumentException(
        "Node \""
            + node.getDescription()
            + "\" is not registered with logical device \""
            + this.getLogicalDeviceName()
            + "\" on simulated RTU device \""
            + this.getPhysicalDeviceName()
            + "\".");
  }

  protected IllegalArgumentException nodeTypeNotConfiguredException(final LogicalDeviceNode node) {
    return new IllegalArgumentException(
        "The data type of node \""
            + node.getDescription()
            + "\" is not configured with logical device \""
            + this.getLogicalDeviceName()
            + "\" on simulated RTU device \""
            + this.getPhysicalDeviceName()
            + "\".");
  }

  /**
   * Writes an updated value for a node to the server model. This attribute update can also trigger
   * updates to other attributes. Those updates are also handled.
   *
   * @param node The externally updated node.
   * @param value The new value for the node.
   * @return The externally updated node and the related updated nodes.
   */
  public List<BasicDataAttribute> writeValueAndUpdateRelatedAttributes(
      final String node, final BasicDataAttribute value) {
    LOGGER.info(
        "No special update action needed for changing node \""
            + node
            + "\" in "
            + this.logicalDeviceName
            + " to "
            + value);

    return new ArrayList<>();
  }
}
