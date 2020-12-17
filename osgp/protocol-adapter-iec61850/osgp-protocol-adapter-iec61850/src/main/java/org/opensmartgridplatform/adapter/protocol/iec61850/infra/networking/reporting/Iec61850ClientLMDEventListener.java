/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.reporting;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.opensmartgridplatform.adapter.protocol.iec61850.application.services.DeviceManagementService;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.LogicalNode;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.SubDataAttribute;
import org.opensmartgridplatform.core.db.api.iec61850.entities.LightMeasurementDevice;
import org.opensmartgridplatform.dto.valueobjects.EventNotificationDto;
import org.opensmartgridplatform.dto.valueobjects.EventTypeDto;
import org.springframework.util.CollectionUtils;

import com.beanit.openiec61850.BdaBoolean;
import com.beanit.openiec61850.FcModelNode;
import com.beanit.openiec61850.Report;

public class Iec61850ClientLMDEventListener extends Iec61850ClientBaseEventListener {

    public Iec61850ClientLMDEventListener(final String deviceIdentification,
            final DeviceManagementService deviceManagementService) throws ProtocolAdapterException {
        super(deviceIdentification, deviceManagementService, Iec61850ClientLMDEventListener.class);
    }

    @Override
    public void newReport(final Report report) {

        final DateTime timeOfEntry = this.getTimeOfEntry(report);

        final String reportDescription = this.getReportDescription(report, timeOfEntry);

        this.logger.info("newReport for {}", reportDescription);

        if (Boolean.TRUE.equals(report.getBufOvfl())) {
            this.logger.warn("Buffer Overflow reported for {} - entries within the buffer may have been lost.",
                    reportDescription);
        }

        if (this.firstNewSqNum != null && report.getSqNum() != null && report.getSqNum() < this.firstNewSqNum) {
            this.logger.warn("report.getSqNum() < this.firstNewSqNum, report.getSqNum() = {}, this.firstNewSqNum = {}",
                    report.getSqNum(), this.firstNewSqNum);
        }
        this.logReportDetails(report);

        if (CollectionUtils.isEmpty(report.getValues())) {
            this.logger.warn("No dataSet members available for {}", reportDescription);
            return;
        }

        final Map<LightMeasurementDevice, FcModelNode> reportMemberPerDevice = this
                .processReportedDataForLightMeasurementDevices(report.getValues());

        for (final LightMeasurementDevice lmd : reportMemberPerDevice.keySet()) {
            final String deviceIdentification = lmd.getDeviceIdentification();
            final Short index = lmd.getDigitalInput();
            final FcModelNode member = reportMemberPerDevice.get(lmd);
            final EventNotificationDto eventNotification = this.getEventNotificationForReportedData(member, timeOfEntry,
                    reportDescription, deviceIdentification, index.intValue());

            try {
                this.deviceManagementService.addEventNotifications(deviceIdentification,
                        Arrays.asList(eventNotification));
            } catch (final ProtocolAdapterException pae) {
                this.logger.error("Error adding device notifications for device: " + deviceIdentification, pae);
            }
        }

    }

    private Map<LightMeasurementDevice, FcModelNode> processReportedDataForLightMeasurementDevices(
            final List<FcModelNode> dataSetMembers) {
        final Map<LightMeasurementDevice, FcModelNode> result = new HashMap<>();

        this.logger.info("Trying to find light measurement devices...");
        final List<LightMeasurementDevice> lmds = this.deviceManagementService.findRealLightMeasurementDevices();
        this.logger.info("Found {} light measurement devices.", lmds == null ? "null" : lmds.size());

        for (final LightMeasurementDevice lmd : lmds) {
            final String nodeName = LogicalNode.getSpggioByIndex(lmd.getDigitalInput()).getDescription().concat(".");

            for (final FcModelNode member : dataSetMembers) {
                if (member.getReference().toString().contains(nodeName)) {
                    result.put(lmd, member);
                }
            }
        }

        this.logger.info("Returning {} results.", result.size());

        return result;
    }

    private DateTime getTimeOfEntry(final Report report) {
        return report.getTimeOfEntry() == null ? DateTime.now(DateTimeZone.UTC)
                : new DateTime(report.getTimeOfEntry().getTimestampValue());
    }

    private String getReportDescription(final Report report, final DateTime timeOfEntry) {
        return String.format("reportId: %s, timeOfEntry: %s, sqNum: %s%s%s", report.getRptId(),
                timeOfEntry == null ? "-" : timeOfEntry, report.getSqNum(),
                report.getSubSqNum() == null ? "" : " subSqNum: " + report.getSubSqNum(),
                report.isMoreSegmentsFollow() ? " (more segments follow for this sqNum)" : "");
    }

    private EventNotificationDto getEventNotificationForReportedData(final FcModelNode evnRpn,
            final DateTime timeOfEntry, final String reportDescription, final String deviceIdentification,
            final Integer index) {
        EventTypeDto eventType;
        final boolean lightSensorValue = this.determineLightSensorValue(evnRpn, reportDescription);
        //@formatter:off
        /*
         * 0 -> false -> NIGHT_DAY --> LIGHT_SENSOR_REPORTS_LIGHT
         * 1 -> true -> DAY_NIGHT --> LIGHT_SENSOR_REPORTS_DARK
         */
        //@formatter:on
        if (lightSensorValue) {
            eventType = EventTypeDto.LIGHT_SENSOR_REPORTS_DARK;
        } else {
            eventType = EventTypeDto.LIGHT_SENSOR_REPORTS_LIGHT;
        }
        return new EventNotificationDto(deviceIdentification, timeOfEntry, eventType, reportDescription, index);
    }

    private boolean determineLightSensorValue(final FcModelNode evnRpn, final String reportDescription) {
        final String dataObjectName = SubDataAttribute.STATE.getDescription();
        final BdaBoolean stVal = (BdaBoolean) evnRpn.getChild(dataObjectName);
        if (stVal == null) {
            throw this.childNodeNotAvailableException(evnRpn, dataObjectName, reportDescription);
        }
        return stVal.getValue();
    }

    private IllegalArgumentException childNodeNotAvailableException(final FcModelNode evnRpn,
            final String childNodeName, final String reportDescription) {
        return new IllegalArgumentException("No '" + childNodeName + "' child in DataSet member "
                + evnRpn.getReference() + " from " + reportDescription);
    }

    private void logReportDetails(final Report report) {
        final StringBuilder sb = new StringBuilder("Report details for device ").append(this.deviceIdentification)
                .append(System.lineSeparator());
        this.logDefaultReportDetails(sb, report);

        final List<FcModelNode> dataSetMembers = report.getValues();
        this.logDataSetMembersDetails(report, dataSetMembers, sb);

        this.logger.info(sb.append(System.lineSeparator()).toString());
    }

    private void logDataSetMembersDetails(final Report report, final List<FcModelNode> dataSetMembers,
            final StringBuilder sb) {
        if (dataSetMembers == null) {
            sb.append("\t           DataSet members:\tnull").append(System.lineSeparator());
        } else {
            sb.append("\t           DataSet:\t").append(report.getDataSetRef()).append(System.lineSeparator());
            if (!dataSetMembers.isEmpty()) {
                sb.append("\t   DataSet members:\t").append(dataSetMembers.size()).append(System.lineSeparator());
                for (final FcModelNode member : dataSetMembers) {
                    sb.append("\t            member:\t").append(member).append(System.lineSeparator());
                    this.checkNodeType(member, sb, "CSLC.EvnRpn");
                }
            }
        }
    }

    private void checkNodeType(final FcModelNode member, final StringBuilder sb, final String nodeType) {
        if (member.getReference().toString().contains(nodeType)) {
            sb.append(this.evnRpnInfo("\t                   \t\t", member));
        }
    }

    private String evnRpnInfo(final String linePrefix, final FcModelNode evnRpn) {
        final String dataObjectName = SubDataAttribute.STATE.getDescription();

        final StringBuilder sb = new StringBuilder();
        final BdaBoolean stValNode = (BdaBoolean) evnRpn.getChild(dataObjectName);
        sb.append(linePrefix).append(dataObjectName).append(": ");
        if (stValNode == null) {
            sb.append("null");
        } else {
            final boolean stVal = stValNode.getValue();
            sb.append(stVal).append(" = ").append(stVal ? "true" : "false");
        }
        sb.append(System.lineSeparator());

        return sb.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.openmuc.openiec61850.ClientEventListener#associationClosed(java.io
     * .IOException)
     */
    @Override
    public void associationClosed(final IOException e) {
        this.logger.info("associationClosed() for device: {}, {}", this.deviceIdentification,
                e.getMessage() == null ? "no IOException" : "IOException: " + e.getMessage());
    }
}
