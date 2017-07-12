/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.reporting;

import com.alliander.osgp.adapter.protocol.iec61850.application.services.DeviceManagementService;
import com.alliander.osgp.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.Iec61850BdaOptFldsHelper;
import org.joda.time.DateTime;
import org.openmuc.openiec61850.BdaFloat32;
import org.openmuc.openiec61850.BdaReasonForInclusion;
import org.openmuc.openiec61850.BdaTimestamp;
import org.openmuc.openiec61850.DataSet;
import org.openmuc.openiec61850.Fc;
import org.openmuc.openiec61850.FcModelNode;
import org.openmuc.openiec61850.HexConverter;
import org.openmuc.openiec61850.ModelNode;
import org.openmuc.openiec61850.Report;
import org.osgpfoundation.osgp.dto.da.GetPQValuesResponseDto;
import org.osgpfoundation.osgp.dto.da.iec61850.DataSampleDto;
import org.osgpfoundation.osgp.dto.da.iec61850.LogicalDeviceDto;
import org.osgpfoundation.osgp.dto.da.iec61850.LogicalNodeDto;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Iec61850ClientDaRTUEventListener extends Iec61850ClientBaseEventListener {
    /**
     * The EntryTime from IEC61850 has timestamp values relative to 01-01-1984.
     * TimeStamp values and Java date time values have milliseconds since
     * 01-01-1970. The milliseconds between these representations are in the
     * following offset.
     */
    private static final long IEC61850_ENTRY_TIME_OFFSET = 441763200000L;
    public Iec61850ClientDaRTUEventListener(final String deviceIdentification,
                                            final DeviceManagementService deviceManagementService) throws ProtocolAdapterException {
        super(deviceIdentification, deviceManagementService, Iec61850ClientDaRTUEventListener.class);
    }

    @Override
    public void newReport(final Report report) {
        final DateTime timeOfEntry = report.getTimeOfEntry() == null ? null
                : new DateTime(report.getTimeOfEntry().getTimestampValue() + IEC61850_ENTRY_TIME_OFFSET);

        final String reportDescription = this.getReportDescription(report, timeOfEntry);

        this.logger.info("newReport for {}", reportDescription);
        this.logReportDetails(report);
        try {
            this.processReport(report, reportDescription);
        } catch (final ProtocolAdapterException e) {
            this.logger.warn("Unable to process report, discarding report", e);
        }
    }

    private void processReport(final Report report, final String reportDescription) throws ProtocolAdapterException {
        if (report.getDataSet() == null) {
            this.logger.warn("No DataSet available for {}", reportDescription);
            return;
        }

        List<LogicalDevice> logicalDevices = new ArrayList<>();
        final List<FcModelNode> members = report.getDataSet().getMembers();
        if ((members == null) || members.isEmpty()) {
            this.logger.warn("No members in DataSet available for {}", reportDescription);
            return;
        }
        for (final FcModelNode member : members) {
            // we are only interested in measurements
            if (member.getFc() == Fc.MX) {
                processMeasurementNode(logicalDevices, member);
            }
        }

        List<LogicalDeviceDto> logicalDevicesDtos = new ArrayList<>();
        for (LogicalDevice logicalDevice : logicalDevices) {
            List<LogicalNodeDto> logicalNodeDtos = new ArrayList<>();
            for (LogicalNode logicalNode : logicalDevice.getLogicalNodes()) {
                LogicalNodeDto logicalNodeDto = new LogicalNodeDto(logicalNode.getName(), logicalNode.getDataSamples());
                logicalNodeDtos.add(logicalNodeDto);
            }
            LogicalDeviceDto logicalDeviceDto = new LogicalDeviceDto(logicalDevice.getName(), logicalNodeDtos);
            logicalDevicesDtos.add(logicalDeviceDto);
        }
        GetPQValuesResponseDto response = new GetPQValuesResponseDto(logicalDevicesDtos);

        this.deviceManagementService.sendPqValues(this.deviceIdentification, report.getRptId(), response);
    }

    private void processMeasurementNode(final List<LogicalDevice> logicalDevices, final FcModelNode member) {
        String logicalDeviceName = member.getReference().get(0);
        LogicalDevice logicalDevice = this.addLogicalDeviceIfNew(logicalDeviceName, logicalDevices);
        String logicalNodeName = member.getReference().get(1);
        LogicalNode logicalNode = this.addLogicalNodeIfNew(logicalNodeName, logicalDevice.getLogicalNodes());
        if (this.modelNodeIsTotalMeasurement(member)) {
            processTotalMeasurementNode(member, logicalNode);
        } else {
            processOtherMeasurementNode(member, logicalNode);
        }
    }

    private void processTotalMeasurementNode(final FcModelNode member, final LogicalNode logicalNode) {
        BdaFloat32 totalMeasurement = this.getTotalMeasurementModelNode(member);
        BdaTimestamp timestampMeasurement = this.getTimestampModelNode(member);
        String type = member.getName();
        type += totalMeasurement.getParent().getName() + "." + totalMeasurement.getName();
        BigDecimal value = new BigDecimal(totalMeasurement.getFloat(), new MathContext(3, RoundingMode.HALF_EVEN));
        DataSampleDto sample = new DataSampleDto(type, timestampMeasurement.getDate(), value);
        logicalNode.getDataSamples().add(sample);
    }

    private void processOtherMeasurementNode(FcModelNode member, LogicalNode logicalNode) {
        for (final ModelNode childNode : member.getChildren()) {
            if (this.modelNodeIsSingleMeasurement(childNode)) {
                processSingleMeasurementNode(member, logicalNode, childNode);
            }
        }
    }

    private void processSingleMeasurementNode(final FcModelNode member, final LogicalNode logicalNode, final ModelNode childNode) {
        BdaFloat32 singleMeasurement = this.getSingleMeasurementModelNode(childNode);
        BdaTimestamp timestampMeasurement = this.getTimestampModelNode(childNode);
        String type = member.getName() + "." + childNode.getName();
        type += singleMeasurement.getParent().getParent().getName() + "." + singleMeasurement.getParent().getName() + "." + singleMeasurement.getName();
        BigDecimal value = new BigDecimal(singleMeasurement.getFloat(), new MathContext(3, RoundingMode.HALF_EVEN));
        DataSampleDto sample = new DataSampleDto(type, timestampMeasurement.getDate(), value);
        logicalNode.getDataSamples().add(sample);
    }

    private boolean modelNodeIsTotalMeasurement(final ModelNode modelNode) {
        boolean totalMeasurement = false;
        if (modelNode!=null && modelNode.getChild("mag")!=null && modelNode.getChild("mag").getChild("f")!=null) {
            totalMeasurement = true;
        }
        return totalMeasurement;
    }

    private boolean modelNodeIsSingleMeasurement(final ModelNode modelNode) {
        boolean totalMeasurement = false;
        if (modelNode!=null
                && modelNode.getChild("cVal")!=null
                && modelNode.getChild("cVal").getChild("mag")!=null
                && modelNode.getChild("cVal").getChild("mag").getChild("f")!=null) {
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

    private LogicalDevice addLogicalDeviceIfNew(final String logicalDeviceName, final List<LogicalDevice> logicalDevices) {
        for (LogicalDevice logicalDevice : logicalDevices) {
            if (logicalDevice.getName().equals(logicalDeviceName)) {
                return logicalDevice;
            }
        }
        LogicalDevice newLogicalDevice = new LogicalDevice(logicalDeviceName);
        logicalDevices.add(newLogicalDevice);
        return newLogicalDevice;
    }

    private LogicalNode addLogicalNodeIfNew(final String logicalNodeName, final List<LogicalNode> logicalNodes) {
        for (LogicalNode logicalNode : logicalNodes) {
            if (logicalNode.getName().equals(logicalNodeName)) {
                return logicalNode;
            }
        }
        LogicalNode newLogicalNode = new LogicalNode(logicalNodeName);
        logicalNodes.add(newLogicalNode);
        return newLogicalNode;
    }

    private class LogicalNode {
        private String name;
        private List<DataSampleDto> dataSamples = new ArrayList<>();

        public LogicalNode(final String name) {
            this.name = name;
        }

        public String getName() {return this.name;}

        public List<DataSampleDto> getDataSamples() {return this.dataSamples;}
    }

    private class LogicalDevice {
        private String name;
        private List<LogicalNode> logicalNodes = new ArrayList<>();

        public LogicalDevice(final String name) {
            this.name = name;
        }

        public String getName() {return this.name;}

        public List<LogicalNode> getLogicalNodes() {return this.logicalNodes;}
    }

    private String getReportDescription(final Report report, final DateTime timeOfEntry) {
        return String.format("device: %s, reportId: %s, timeOfEntry: %s, sqNum: %s%s%s", this.deviceIdentification,
                report.getRptId(), timeOfEntry == null ? "-" : timeOfEntry, report.getSqNum(),
                report.getSubSqNum() == null ? "" : " subSqNum: " + report.getSubSqNum(),
                report.isMoreSegmentsFollow() ? " (more segments follow for this sqNum)" : "");
    }

    private void logReportDetails(final Report report) {
        final StringBuilder sb = new StringBuilder("Report details for device ").append(this.deviceIdentification)
                .append(System.lineSeparator());
        sb.append("\t             RptId:\t").append(report.getRptId()).append(System.lineSeparator());
        sb.append("\t        DataSetRef:\t").append(report.getDataSetRef()).append(System.lineSeparator());
        sb.append("\t           ConfRev:\t").append(report.getConfRev()).append(System.lineSeparator());
        sb.append("\t           BufOvfl:\t").append(report.isBufOvfl()).append(System.lineSeparator());
        sb.append("\t           EntryId:\t").append(report.getEntryId()).append(System.lineSeparator());
        sb.append("\tInclusionBitString:\t").append(Arrays.toString(report.getInclusionBitString()))
        .append(System.lineSeparator());
        sb.append("\tMoreSegmentsFollow:\t").append(report.isMoreSegmentsFollow()).append(System.lineSeparator());
        sb.append("\t             SqNum:\t").append(report.getSqNum()).append(System.lineSeparator());
        sb.append("\t          SubSqNum:\t").append(report.getSubSqNum()).append(System.lineSeparator());
        sb.append("\t       TimeOfEntry:\t").append(report.getTimeOfEntry()).append(System.lineSeparator());
        if (report.getTimeOfEntry() != null) {
            sb.append("\t                   \t(")
            .append(new DateTime(report.getTimeOfEntry().getTimestampValue() + IEC61850_ENTRY_TIME_OFFSET))
            .append(')').append(System.lineSeparator());
        }
        final List<BdaReasonForInclusion> reasonCodes = report.getReasonCodes();
        if ((reasonCodes != null) && !reasonCodes.isEmpty()) {
            sb.append("\t       ReasonCodes:").append(System.lineSeparator());
            for (final BdaReasonForInclusion reasonCode : reasonCodes) {
                sb.append("\t                   \t")
                .append(reasonCode.getReference() == null ? HexConverter.toHexString(reasonCode.getValue())
                        : reasonCode)
                .append("\t(").append(new Iec61850BdaReasonForInclusionHelper(reasonCode).getInfo()).append(')')
                .append(System.lineSeparator());
            }
        }
        sb.append("\t           optFlds:").append(report.getOptFlds()).append("\t(")
        .append(new Iec61850BdaOptFldsHelper(report.getOptFlds()).getInfo()).append(')')
        .append(System.lineSeparator());
        final DataSet dataSet = report.getDataSet();
        if (dataSet == null) {
            sb.append("\t           DataSet:\tnull").append(System.lineSeparator());
        } else {
            sb.append("\t           DataSet:\t").append(dataSet.getReferenceStr()).append(System.lineSeparator());
            final List<FcModelNode> members = dataSet.getMembers();
            if ((members != null) && !members.isEmpty()) {
                sb.append("\t   DataSet members:\t").append(members.size()).append(System.lineSeparator());
                for (final FcModelNode member : members) {
                    sb.append("\t            member:\t").append(member).append(System.lineSeparator());
                    sb.append("\t                   \t\t").append(member);
                }
            }
        }
        this.logger.info(sb.append(System.lineSeparator()).toString());
    }

    @Override
    public void associationClosed(final IOException e) {
        this.logger.info("associationClosed for device: {}, {}", this.deviceIdentification,
                e == null ? "no IOException" : "IOException: " + e.getMessage());
    }

}
