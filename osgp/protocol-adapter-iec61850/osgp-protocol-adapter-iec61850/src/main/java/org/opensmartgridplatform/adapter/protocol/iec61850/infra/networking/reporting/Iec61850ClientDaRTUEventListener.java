//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.reporting;

import com.beanit.openiec61850.BdaFloat32;
import com.beanit.openiec61850.BdaTimestamp;
import com.beanit.openiec61850.Fc;
import com.beanit.openiec61850.FcModelNode;
import com.beanit.openiec61850.ModelNode;
import com.beanit.openiec61850.Report;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;
import org.opensmartgridplatform.adapter.protocol.iec61850.application.services.DeviceManagementService;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.da.GetPQValuesResponseDto;
import org.opensmartgridplatform.dto.da.iec61850.DataSampleDto;
import org.opensmartgridplatform.dto.da.iec61850.LogicalDeviceDto;
import org.opensmartgridplatform.dto.da.iec61850.LogicalNodeDto;
import org.springframework.util.CollectionUtils;

public class Iec61850ClientDaRTUEventListener extends Iec61850ClientBaseEventListener {

  public Iec61850ClientDaRTUEventListener(
      final String deviceIdentification, final DeviceManagementService deviceManagementService)
      throws ProtocolAdapterException {
    super(deviceIdentification, deviceManagementService, Iec61850ClientDaRTUEventListener.class);
  }

  @Override
  public void newReport(final Report report) {
    final DateTime timeOfEntry =
        report.getTimeOfEntry() == null
            ? null
            : new DateTime(report.getTimeOfEntry().getTimestampValue());

    final String reportDescription = this.getReportDescription(report, timeOfEntry);

    this.logger.info("newReport for {}", reportDescription);
    this.logReportDetails(report);
    try {
      this.processReport(report, reportDescription);
    } catch (final ProtocolAdapterException e) {
      this.logger.warn("Unable to process report, discarding report", e);
    }
  }

  private void processReport(final Report report, final String reportDescription)
      throws ProtocolAdapterException {
    final List<FcModelNode> dataSetMembers = report.getValues();
    final List<LogicalDevice> logicalDevices = new ArrayList<>();

    if (CollectionUtils.isEmpty(dataSetMembers)) {
      this.logger.warn("No dataSet members available for {}", reportDescription);
      return;
    }

    for (final FcModelNode member : dataSetMembers) {
      // we are only interested in measurements
      if (member.getFc() == Fc.MX) {
        this.processMeasurementNode(logicalDevices, member);
      }
    }

    final List<LogicalDeviceDto> logicalDevicesDtos = new ArrayList<>();
    for (final LogicalDevice logicalDevice : logicalDevices) {
      final List<LogicalNodeDto> logicalNodeDtos = new ArrayList<>();
      for (final LogicalNode logicalNode : logicalDevice.getLogicalNodes()) {
        final LogicalNodeDto logicalNodeDto =
            new LogicalNodeDto(logicalNode.getName(), logicalNode.getDataSamples());
        logicalNodeDtos.add(logicalNodeDto);
      }
      final LogicalDeviceDto logicalDeviceDto =
          new LogicalDeviceDto(logicalDevice.getName(), logicalNodeDtos);
      logicalDevicesDtos.add(logicalDeviceDto);
    }
    final GetPQValuesResponseDto response = new GetPQValuesResponseDto(logicalDevicesDtos);

    this.deviceManagementService.sendPqValues(
        this.deviceIdentification, report.getRptId(), response);
  }

  private void processMeasurementNode(
      final List<LogicalDevice> logicalDevices, final FcModelNode member) {
    final String logicalDeviceName = member.getReference().get(0);
    final LogicalDevice logicalDevice =
        this.addLogicalDeviceIfNew(logicalDeviceName, logicalDevices);
    final String logicalNodeName = member.getReference().get(1);
    final LogicalNode logicalNode =
        this.addLogicalNodeIfNew(logicalNodeName, logicalDevice.getLogicalNodes());
    if (this.modelNodeIsTotalMeasurement(member)) {
      this.processTotalMeasurementNode(member, logicalNode);
    } else {
      this.processOtherMeasurementNode(member, logicalNode);
    }
  }

  private void processTotalMeasurementNode(
      final FcModelNode member, final LogicalNode logicalNode) {
    final BdaFloat32 totalMeasurement = this.getTotalMeasurementModelNode(member);
    final BdaTimestamp timestampMeasurement = this.getTimestampModelNode(member);
    String type = member.getName();
    type += "." + totalMeasurement.getParent().getName() + "." + totalMeasurement.getName();
    final BigDecimal value =
        new BigDecimal(totalMeasurement.getFloat(), new MathContext(3, RoundingMode.HALF_EVEN));
    final DataSampleDto sample = new DataSampleDto(type, timestampMeasurement.getDate(), value);
    logicalNode.getDataSamples().add(sample);
  }

  private void processOtherMeasurementNode(
      final FcModelNode member, final LogicalNode logicalNode) {
    for (final ModelNode childNode : member.getChildren()) {
      if (this.modelNodeIsSingleMeasurement(childNode)) {
        this.processSingleMeasurementNode(member, logicalNode, childNode);
      }
    }
  }

  private void processSingleMeasurementNode(
      final FcModelNode member, final LogicalNode logicalNode, final ModelNode childNode) {
    final BdaFloat32 singleMeasurement = this.getSingleMeasurementModelNode(childNode);
    final BdaTimestamp timestampMeasurement = this.getTimestampModelNode(childNode);
    String type = member.getName() + "." + childNode.getName();
    type +=
        "."
            + singleMeasurement.getParent().getParent().getName()
            + "."
            + singleMeasurement.getParent().getName()
            + "."
            + singleMeasurement.getName();
    final BigDecimal value =
        new BigDecimal(singleMeasurement.getFloat(), new MathContext(3, RoundingMode.HALF_EVEN));
    final DataSampleDto sample = new DataSampleDto(type, timestampMeasurement.getDate(), value);
    logicalNode.getDataSamples().add(sample);
  }

  private boolean modelNodeIsTotalMeasurement(final ModelNode modelNode) {
    boolean totalMeasurement = false;
    if (modelNode != null
        && modelNode.getChild("mag") != null
        && modelNode.getChild("mag").getChild("f") != null) {
      totalMeasurement = true;
    }
    return totalMeasurement;
  }

  private boolean modelNodeIsSingleMeasurement(final ModelNode modelNode) {
    boolean totalMeasurement = false;
    if (modelNode != null
        && modelNode.getChild("cVal") != null
        && modelNode.getChild("cVal").getChild("mag") != null
        && modelNode.getChild("cVal").getChild("mag").getChild("f") != null) {
      totalMeasurement = true;
    }
    return totalMeasurement;
  }

  private BdaFloat32 getTotalMeasurementModelNode(final ModelNode modelNode) {
    return (BdaFloat32) modelNode.getChild("mag").getChild("f");
  }

  private BdaFloat32 getSingleMeasurementModelNode(final ModelNode modelNode) {
    return (BdaFloat32) modelNode.getChild("cVal").getChild("mag").getChild("f");
  }

  private BdaTimestamp getTimestampModelNode(final ModelNode modelNode) {
    return (BdaTimestamp) modelNode.getChild("t");
  }

  private LogicalDevice addLogicalDeviceIfNew(
      final String logicalDeviceName, final List<LogicalDevice> logicalDevices) {
    for (final LogicalDevice logicalDevice : logicalDevices) {
      if (logicalDevice.getName().equals(logicalDeviceName)) {
        return logicalDevice;
      }
    }
    final LogicalDevice newLogicalDevice = new LogicalDevice(logicalDeviceName);
    logicalDevices.add(newLogicalDevice);
    return newLogicalDevice;
  }

  private LogicalNode addLogicalNodeIfNew(
      final String logicalNodeName, final List<LogicalNode> logicalNodes) {
    for (final LogicalNode logicalNode : logicalNodes) {
      if (logicalNode.getName().equals(logicalNodeName)) {
        return logicalNode;
      }
    }
    final LogicalNode newLogicalNode = new LogicalNode(logicalNodeName);
    logicalNodes.add(newLogicalNode);
    return newLogicalNode;
  }

  private class LogicalNode {
    private String name;
    private List<DataSampleDto> dataSamples = new ArrayList<>();

    public LogicalNode(final String name) {
      this.name = name;
    }

    public String getName() {
      return this.name;
    }

    public List<DataSampleDto> getDataSamples() {
      return this.dataSamples;
    }
  }

  private class LogicalDevice {
    private String name;
    private List<LogicalNode> logicalNodes = new ArrayList<>();

    public LogicalDevice(final String name) {
      this.name = name;
    }

    public String getName() {
      return this.name;
    }

    public List<LogicalNode> getLogicalNodes() {
      return this.logicalNodes;
    }
  }

  private String getReportDescription(final Report report, final DateTime timeOfEntry) {
    return String.format(
        "device: %s, reportId: %s, timeOfEntry: %s, sqNum: %s%s%s",
        this.deviceIdentification,
        report.getRptId(),
        timeOfEntry == null ? "-" : timeOfEntry,
        report.getSqNum(),
        report.getSubSqNum() == null ? "" : " subSqNum: " + report.getSubSqNum(),
        report.isMoreSegmentsFollow() ? " (more segments follow for this sqNum)" : "");
  }

  private void logReportDetails(final Report report) {
    final StringBuilder sb =
        new StringBuilder("Report details for device ")
            .append(this.deviceIdentification)
            .append(System.lineSeparator());
    this.logDefaultReportDetails(sb, report);

    final List<FcModelNode> dataSetMembers = report.getValues();
    this.logDataSetMembersDetails(report, dataSetMembers, sb);

    this.logger.info(sb.append(System.lineSeparator()).toString());
  }

  private void logDataSetMembersDetails(
      final Report report, final List<FcModelNode> dataSetMembers, final StringBuilder sb) {
    if (dataSetMembers == null) {
      sb.append("\t           DataSet:\tnull").append(System.lineSeparator());
    } else {
      sb.append("\t           DataSet:\t")
          .append(report.getDataSetRef())
          .append(System.lineSeparator());
      if (!dataSetMembers.isEmpty()) {
        sb.append("\t   DataSet members:\t")
            .append(dataSetMembers.size())
            .append(System.lineSeparator());
        for (final FcModelNode member : dataSetMembers) {
          sb.append("\t            member:\t")
              .append(member.getReference())
              .append(System.lineSeparator());
        }
      }
    }
  }

  @Override
  public void associationClosed(final IOException e) {
    this.logger.info(
        "associationClosed for device: {}, {}",
        this.deviceIdentification,
        e == null ? "no IOException" : "IOException: " + e.getMessage());
  }
}
