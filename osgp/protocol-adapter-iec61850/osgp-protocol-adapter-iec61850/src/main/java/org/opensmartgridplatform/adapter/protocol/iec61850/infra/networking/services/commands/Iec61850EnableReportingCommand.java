// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands;

import com.beanit.openiec61850.Fc;
import com.beanit.openiec61850.FcModelNode;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.NodeException;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.IED;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.LogicalNode;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.NodeContainer;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.SubDataAttribute;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.reporting.Iec61850ClientBaseEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Iec61850EnableReportingCommand {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(Iec61850EnableReportingCommand.class);

  /**
   * Enable reporting so the device can send reports.
   *
   * @throws NodeException In case writing or reading of data-attributes fails.
   */
  public void enableReportingOnDevice(
      final Iec61850Client iec61850Client, final DeviceConnection deviceConnection)
      throws NodeException {
    final NodeContainer reporting =
        deviceConnection.getFcModelNode(
            LogicalDevice.LIGHTING, LogicalNode.LOGICAL_NODE_ZERO, DataAttribute.REPORTING, Fc.BR);
    // Only reading the sequence number for the report node, as the report
    // node is not fully described by the ServerModel when using an ICD
    // file. Since the report node differs from the ServerModel, a full read
    // of the node and all data-attributes will fail. Therefore, only the
    // needed data-attributes are read.
    iec61850Client.readNodeDataValues(
        deviceConnection.getConnection().getClientAssociation(),
        (FcModelNode)
            reporting.getFcmodelNode().getChild(SubDataAttribute.SEQUENCE_NUMBER.getDescription()));

    final Iec61850ClientBaseEventListener reportListener =
        deviceConnection.getConnection().getIec61850ClientAssociation().getReportListener();

    final short sqNum = reporting.getUnsignedByte(SubDataAttribute.SEQUENCE_NUMBER).getValue();
    reportListener.setSqNum(sqNum);
    reporting.writeBoolean(SubDataAttribute.ENABLE_REPORTING, true);
    LOGGER.info(
        "Allowing device {} to send reports containing events",
        deviceConnection.getDeviceIdentification());
  }

  /**
   * Enable reporting so the SSLD can send reports. This version of the function does not use the
   * 'sequence number' to filter incoming reports. When using the {@link Iec61850ClearReportCommand}
   * the 'sequence number' will always be reset to 0.
   *
   * @throws NodeException In case writing of data-attributes fails.
   */
  public void enableBufferedReportingOnDeviceWithoutUsingSequenceNumber(
      final DeviceConnection deviceConnection) throws NodeException {
    final NodeContainer reporting =
        deviceConnection.getFcModelNode(
            LogicalDevice.LIGHTING, LogicalNode.LOGICAL_NODE_ZERO, DataAttribute.REPORTING, Fc.BR);

    final Iec61850ClientBaseEventListener reportListener =
        deviceConnection.getConnection().getIec61850ClientAssociation().getReportListener();

    reportListener.setSqNum(0);
    reporting.writeBoolean(SubDataAttribute.ENABLE_REPORTING, true);
    LOGGER.info(
        "Allowing device {} to send buffered reports containing events",
        deviceConnection.getDeviceIdentification());
  }

  public void enableUnbufferedReportingOnDeviceWithoutUsingSequenceNumber(
      final DeviceConnection deviceConnection) throws NodeException {
    final NodeContainer reporting =
        deviceConnection.getFcModelNode(
            LogicalDevice.LIGHTING, LogicalNode.LOGICAL_NODE_ZERO, DataAttribute.REPORTING, Fc.RP);

    final Iec61850ClientBaseEventListener reportListener =
        deviceConnection.getConnection().getIec61850ClientAssociation().getReportListener();

    reportListener.setSqNum(0);
    reporting.writeBoolean(SubDataAttribute.ENABLE_REPORTING, true);
    LOGGER.info(
        "Allowing device {} to send unbuffered reports containing events",
        deviceConnection.getDeviceIdentification());
  }

  public void enableBufferedReportingOnLightMeasurementDevice(
      final Iec61850Client iec61850Client, final DeviceConnection deviceConnection)
      throws NodeException {

    final NodeContainer reporting =
        deviceConnection.getFcModelNode(
            LogicalDevice.LD0, LogicalNode.LOGICAL_NODE_ZERO, DataAttribute.RCB_A, Fc.BR);

    // Read the reporting enabled boolean.
    iec61850Client.readNodeDataValues(
        deviceConnection.getConnection().getClientAssociation(),
        (FcModelNode)
            reporting
                .getFcmodelNode()
                .getChild(SubDataAttribute.ENABLE_REPORTING.getDescription()));

    final boolean reportingEnabled =
        reporting.getBoolean(SubDataAttribute.ENABLE_REPORTING).getValue();
    LOGGER.info("reportingEnabled for buffered reports: {}", reportingEnabled);

    if (reportingEnabled) {
      LOGGER.info(
          "Buffered reporting is already enabled for device: {}",
          deviceConnection.getDeviceIdentification());
      return;
    }

    // Only reading the sequence number for the report node, as the report
    // node is not fully described by the ServerModel when using an ICD
    // file. Since the report node differs from the ServerModel, a full read
    // of the node and all data-attributes will fail. Therefore, only the
    // needed data-attributes are read.
    iec61850Client.readNodeDataValues(
        deviceConnection.getConnection().getClientAssociation(),
        (FcModelNode)
            reporting.getFcmodelNode().getChild(SubDataAttribute.SEQUENCE_NUMBER.getDescription()));

    final Iec61850ClientBaseEventListener reportListener =
        deviceConnection.getConnection().getIec61850ClientAssociation().getReportListener();

    final short sqNum = reporting.getUnsignedByte(SubDataAttribute.SEQUENCE_NUMBER).getValue();
    reportListener.setSqNum(sqNum);

    final String dataSetReference = reporting.getString(SubDataAttribute.DATA_SET);
    LOGGER.info("dataSetReference for buffered reports: {}", dataSetReference);

    if (StringUtils.isEmpty(dataSetReference)) {
      // Data set reference should be something like:
      // "AA1TH01LD0/LLN0.StatNrmlA".
      // If not set, this will cause problems. Not possible to write to
      // this node.
      final String dataSet = this.getDataSetReferenceForLightMeasurementDevice();
      LOGGER.warn(
          "Expected value like [{}] to be present in {}. This will most likely cause trouble when buffered reports are received in the future!",
          dataSet,
          SubDataAttribute.DATA_SET.getDescription());
    }

    // Enable reporting.
    reporting.writeBoolean(SubDataAttribute.ENABLE_REPORTING, true);
    LOGGER.info(
        "Allowing light measurement device {} to send buffered reports containing events",
        deviceConnection.getDeviceIdentification());
  }

  public void enableUnbufferedReportingOnLightMeasurementDevice(
      final Iec61850Client iec61850Client, final DeviceConnection deviceConnection)
      throws NodeException {

    final NodeContainer reporting =
        deviceConnection.getFcModelNode(
            LogicalDevice.LD0, LogicalNode.LOGICAL_NODE_ZERO, DataAttribute.RCB_A, Fc.RP);

    // Read the reporting enabled boolean.
    iec61850Client.readNodeDataValues(
        deviceConnection.getConnection().getClientAssociation(),
        (FcModelNode)
            reporting
                .getFcmodelNode()
                .getChild(SubDataAttribute.ENABLE_REPORTING.getDescription()));

    final boolean reportingEnabled =
        reporting.getBoolean(SubDataAttribute.ENABLE_REPORTING).getValue();
    LOGGER.info("reportingEnabled for unbuffered reports: {}", reportingEnabled);

    if (reportingEnabled) {
      LOGGER.info(
          "Unbuffered reporting is already enabled for device: {}",
          deviceConnection.getDeviceIdentification());
      return;
    }

    // Only reading the sequence number for the report node, as the report
    // node is not fully described by the ServerModel when using an ICD
    // file. Since the report node differs from the ServerModel, a full read
    // of the node and all data-attributes will fail. Therefore, only the
    // needed data-attributes are read.
    iec61850Client.readNodeDataValues(
        deviceConnection.getConnection().getClientAssociation(),
        (FcModelNode)
            reporting.getFcmodelNode().getChild(SubDataAttribute.SEQUENCE_NUMBER.getDescription()));

    final Iec61850ClientBaseEventListener reportListener =
        deviceConnection.getConnection().getIec61850ClientAssociation().getReportListener();

    final short sqNum = reporting.getUnsignedByte(SubDataAttribute.SEQUENCE_NUMBER).getValue();
    reportListener.setSqNum(sqNum);

    final String dataSetReference = reporting.getString(SubDataAttribute.DATA_SET);
    LOGGER.info("dataSetReference for unbuffered reporting: {}", dataSetReference);

    if (StringUtils.isEmpty(dataSetReference)) {
      // Set data set reference.
      reporting.writeString(
          SubDataAttribute.DATA_SET, this.getDataSetReferenceForLightMeasurementDevice());
      reporting.writeString(SubDataAttribute.REPORT_ID, "A");
    }

    // Enable reporting.
    reporting.writeBoolean(SubDataAttribute.ENABLE_REPORTING, true);
    LOGGER.info(
        "Allowing light measurement device {} to send unbuffered reports containing events",
        deviceConnection.getDeviceIdentification());
  }

  private String getDataSetReferenceForLightMeasurementDevice() {
    return IED.ABB_RTU.getDescription()
        + LogicalDevice.LD0.getDescription()
        + "/"
        + LogicalNode.LOGICAL_NODE_ZERO.getDescription()
        + "."
        + "StatNrmlA";
  }
}
