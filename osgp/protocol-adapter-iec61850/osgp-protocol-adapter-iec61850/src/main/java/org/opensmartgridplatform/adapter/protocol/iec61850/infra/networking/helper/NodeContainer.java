// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper;

import com.beanit.openiec61850.Array;
import com.beanit.openiec61850.BdaBoolean;
import com.beanit.openiec61850.BdaFloat32;
import com.beanit.openiec61850.BdaInt16;
import com.beanit.openiec61850.BdaInt16U;
import com.beanit.openiec61850.BdaInt32;
import com.beanit.openiec61850.BdaInt32U;
import com.beanit.openiec61850.BdaInt64;
import com.beanit.openiec61850.BdaInt8;
import com.beanit.openiec61850.BdaInt8U;
import com.beanit.openiec61850.BdaOctetString;
import com.beanit.openiec61850.BdaQuality;
import com.beanit.openiec61850.BdaTimestamp;
import com.beanit.openiec61850.BdaVisibleString;
import com.beanit.openiec61850.FcModelNode;
import com.beanit.openiec61850.ServiceError;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.NodeWriteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeContainer {

  private static final Logger LOGGER = LoggerFactory.getLogger(NodeContainer.class);

  private static final String DEVICE_READ_LOG_MESSAGE = "Device: {}, {} has value {}";
  private static final String DEVICE_WRITE_LOG_MESSAGE = "Device: {}, writing {} to {}";
  private static final String ATTRIBUTE_NULL_LOG_MESSAGE =
      "{} is null, most likely attribute: {} does not exist";
  private static final String NULL_POINTER_MESSAGE = "Attribute %s of type %s is null";

  protected final String deviceIdentification;
  protected final DeviceConnection connection;
  protected final FcModelNode parent;

  public NodeContainer(final DeviceConnection connection, final FcModelNode fcmodelNode) {
    Objects.requireNonNull(connection, "connection must not be null");
    Objects.requireNonNull(fcmodelNode, "fcmodelNode must not be null");
    this.deviceIdentification = connection.getDeviceIdentification();
    this.connection = connection;
    this.parent = fcmodelNode;
  }

  public NodeContainer(final String deviceIdentification, final FcModelNode fcmodelNode) {
    this.deviceIdentification = deviceIdentification;
    this.connection = null;
    this.parent = fcmodelNode;
  }

  /**
   * Write the {@link FcModelNode} which is stored in the {@link NodeContainer#parent} field.
   *
   * @throws NodeWriteException In case the write action fails {@link ServiceError} or {@link
   *     IOException} is thrown by OpenMUC OpenIEC61850. {@link NodeWriteException} wraps the thrown
   *     exception and indicates if the connection with the IED is still usable. See {@link
   *     ConnectionState}.
   */
  public void write() throws NodeWriteException {
    this.writeNode(this.parent);
  }

  /** Returns a String for {@link BdaVisibleString} values */
  public String getString(final SubDataAttribute child) {
    final BdaVisibleString bdaString =
        (BdaVisibleString) this.parent.getChild(child.getDescription());

    if (bdaString == null) {
      LOGGER.error(ATTRIBUTE_NULL_LOG_MESSAGE, "BdaVisibleString", child);
      throw new NullPointerException(
          String.format(NULL_POINTER_MESSAGE, child, "BdaVisibleString"));
    }

    LOGGER.info(
        DEVICE_READ_LOG_MESSAGE,
        this.deviceIdentification,
        child.getDescription(),
        bdaString.getStringValue());
    return bdaString.getStringValue();
  }

  /** Writes a String value to the given child on the device */
  public void writeString(final SubDataAttribute child, final String value)
      throws NodeWriteException {
    final BdaVisibleString stringNode =
        (BdaVisibleString) this.parent.getChild(child.getDescription());

    LOGGER.info(DEVICE_WRITE_LOG_MESSAGE, this.deviceIdentification, value, child.getDescription());

    stringNode.setValue(value);
    this.writeNode(stringNode);
  }

  /** Writes an OctetString value to the given child on the device */
  public void writeOctetString(final SubDataAttribute child, final byte[] value)
      throws NodeWriteException {
    final BdaOctetString bdaOctetString =
        (BdaOctetString) this.parent.getChild(child.getDescription());

    LOGGER.info(DEVICE_WRITE_LOG_MESSAGE, this.deviceIdentification, value, child.getDescription());

    bdaOctetString.setValue(value);
    this.writeNode(bdaOctetString);
  }

  /** Returns a {@link Date} for {@link BdaTimestamp} values */
  public Date getDate(final SubDataAttribute child) {
    final BdaTimestamp dBdaTimestamp = (BdaTimestamp) this.parent.getChild(child.getDescription());

    if (dBdaTimestamp == null) {
      LOGGER.error(ATTRIBUTE_NULL_LOG_MESSAGE, "BdaTimeStamp", child);
      throw new NullPointerException(String.format(NULL_POINTER_MESSAGE, child, "BdaTimeStamp"));
    }

    LOGGER.debug(
        DEVICE_READ_LOG_MESSAGE,
        this.deviceIdentification,
        child.getDescription(),
        dBdaTimestamp.getDate());
    return dBdaTimestamp.getDate();
  }

  /** Writes a Date value to the given child on the device */
  public void writeDate(final SubDataAttribute child, final Date value) throws NodeWriteException {
    final BdaTimestamp dBdaTimestamp = (BdaTimestamp) this.parent.getChild(child.getDescription());

    LOGGER.info(DEVICE_WRITE_LOG_MESSAGE, this.deviceIdentification, value, child.getDescription());
    dBdaTimestamp.setDate(value);
    this.writeNode(dBdaTimestamp);
  }

  public BdaBoolean getBoolean(final SubDataAttribute child) {
    return (BdaBoolean) this.parent.getChild(child.getDescription());
  }

  public void writeBoolean(final SubDataAttribute child, final boolean value)
      throws NodeWriteException {
    final BdaBoolean bdaBoolean = (BdaBoolean) this.parent.getChild(child.getDescription());
    bdaBoolean.setValue(value);
    this.writeNode(bdaBoolean);
  }

  public BdaInt8 getByte(final SubDataAttribute child) {
    return (BdaInt8) this.parent.getChild(child.getDescription());
  }

  public void writeByte(final SubDataAttribute child, final byte value) throws NodeWriteException {
    final BdaInt8 bdaByte = (BdaInt8) this.parent.getChild(child.getDescription());
    bdaByte.setValue(value);
    this.writeNode(bdaByte);
  }

  public BdaInt8U getUnsignedByte(final SubDataAttribute child) {
    return (BdaInt8U) this.parent.getChild(child.getDescription());
  }

  public BdaInt16 getShort(final SubDataAttribute child) {
    return (BdaInt16) this.parent.getChild(child.getDescription());
  }

  public void writeShort(final SubDataAttribute child, final Short value)
      throws NodeWriteException {
    final BdaInt16 bdaShort = (BdaInt16) this.parent.getChild(child.getDescription());
    bdaShort.setValue(value);
    this.writeNode(bdaShort);
  }

  public BdaInt16U getUnsignedShort(final SubDataAttribute child) {
    return (BdaInt16U) this.parent.getChild(child.getDescription());
  }

  public void writeUnsignedShort(final SubDataAttribute child, final Integer value)
      throws NodeWriteException {
    final BdaInt16U bdaUnsignedShort = (BdaInt16U) this.parent.getChild(child.getDescription());
    bdaUnsignedShort.setValue(value);
    this.writeNode(bdaUnsignedShort);
  }

  public BdaInt32 getInteger(final SubDataAttribute child) {
    return (BdaInt32) this.parent.getChild(child.getDescription());
  }

  public void writeInteger(final SubDataAttribute child, final Integer value)
      throws NodeWriteException {
    final BdaInt32 bdaInteger = (BdaInt32) this.parent.getChild(child.getDescription());
    bdaInteger.setValue(value);
    this.writeNode(bdaInteger);
  }

  public BdaInt32U getUnsignedInteger(final SubDataAttribute child) {
    return (BdaInt32U) this.parent.getChild(child.getDescription());
  }

  public void writeUnsignedInteger(final SubDataAttribute child, final Integer value)
      throws NodeWriteException {
    final BdaInt32U bdaInteger = (BdaInt32U) this.parent.getChild(child.getDescription());
    bdaInteger.setValue(value);
    this.writeNode(bdaInteger);
  }

  public BdaInt64 getLong(final SubDataAttribute child) {
    return (BdaInt64) this.parent.getChild(child.getDescription());
  }

  public void writeLong(final SubDataAttribute child, final Integer value)
      throws NodeWriteException {
    final BdaInt64 bdaInteger = (BdaInt64) this.parent.getChild(child.getDescription());
    bdaInteger.setValue(value);
    this.writeNode(bdaInteger);
  }

  public BdaFloat32 getFloat(final SubDataAttribute child) {
    return (BdaFloat32) this.parent.getChild(child.getDescription());
  }

  public void writeFloat(final SubDataAttribute child, final Float value)
      throws NodeWriteException {
    final BdaFloat32 bdaFloat = (BdaFloat32) this.parent.getChild(child.getDescription());
    bdaFloat.setFloat(value);
    this.writeNode(bdaFloat);
  }

  public Float[] getFloatArray(final SubDataAttribute child) {
    final Array array = (Array) this.parent.getChild(child.getDescription());
    final int size = array.size();

    final Float[] result = new Float[size];
    for (int i = 0; i < size; i++) {
      result[i] = ((BdaFloat32) array.getChild(i)).getFloat();
    }
    return result;
  }

  public void writeFloatArray(final SubDataAttribute child, final Float[] values)
      throws NodeWriteException {
    final Array array = (Array) this.parent.getChild(child.getDescription());
    if (array.size() != values.length) {
      throw new NodeWriteException(
          String.format(
              "Invalid array size %d. Size on device is %d", values.length, array.size()));
    }

    for (int i = 0; i < values.length; i++) {
      final BdaFloat32 bdaFloat = (BdaFloat32) array.getChild(i);
      bdaFloat.setFloat(values[i]);
    }
    this.writeNode(array);
  }

  public Date[] getDateArray(final SubDataAttribute child) {
    final Array array = (Array) this.parent.getChild(child.getDescription());
    final int size = array.size();

    final Date[] result = new Date[size];
    for (int i = 0; i < size; i++) {
      result[i] = ((BdaTimestamp) array.getChild(i)).getDate();
    }
    return result;
  }

  public void writeDateArray(final SubDataAttribute child, final Date[] values)
      throws NodeWriteException {
    final Array array = (Array) this.parent.getChild(child.getDescription());
    if (array.size() != values.length) {
      throw new NodeWriteException(
          String.format(
              "Invalid array size %d. Size on device is %d", values.length, array.size()));
    }

    for (int i = 0; i < values.length; i++) {
      final BdaTimestamp bdaTimestamp = (BdaTimestamp) array.getChild(i);
      bdaTimestamp.setDate(values[i]);
    }
    this.writeNode(array);
  }

  public BdaQuality getQuality(final SubDataAttribute child) {
    return (BdaQuality) this.parent.getChild(child.getDescription());
  }

  /** Writes the new data of the node to the device. */
  private void writeNode(final FcModelNode node) throws NodeWriteException {
    try {
      this.connection.getConnection().getClientAssociation().setDataValues(node);
    } catch (final ServiceError e) {
      LOGGER.error("ServiceError during writeNode()", e);
      throw new NodeWriteException(e.getMessage(), e, ConnectionState.OK);
    } catch (final IOException e) {
      // "if a fatal association error occurs. The association object will
      // be closed and can no longer be used after this exception is
      // thrown."
      LOGGER.error("IOException during writeNode()", e);
      throw new NodeWriteException(e.getMessage(), e, ConnectionState.BROKEN);
    }
  }

  /**
   * Get the {@link FcModelNode} instance.
   *
   * @return The {@link FcModelNode} instance.
   */
  public FcModelNode getFcmodelNode() {
    return this.parent;
  }

  /**
   * Return a child or sub-data-attribute for this node.
   *
   * @param child The name of the child to fetch.
   */
  public NodeContainer getChild(final SubDataAttribute child) {
    return new NodeContainer(
        this.connection, (FcModelNode) this.parent.getChild(child.getDescription()));
  }

  /**
   * Return a child or sub-data-attribute for this node.
   *
   * @param child The name of the child to fetch.
   */
  public NodeContainer getChild(final String child) {
    return new NodeContainer(this.connection, (FcModelNode) this.parent.getChild(child));
  }

  @Override
  public String toString() {
    if (this.parent == null) {
      return "";
    }

    return this.parent.toString();
  }
}
