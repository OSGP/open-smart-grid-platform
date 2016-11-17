/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.reporting;

import org.openmuc.openiec61850.Fc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.adapter.protocol.iec61850.exceptions.NodeWriteException;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.LogicalNode;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.NodeContainer;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.SubDataAttribute;

public class Iec61850RtuDeviceReportingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850RtuDeviceReportingService.class);

    public void enableReportingOnDevice(final DeviceConnection connection, final String deviceIdentification) {

        // Refactor - make it more flexible for any kind of
        // devices (store number of devices in DB?)
        this.enableRtuReportingOnDevice(connection, deviceIdentification);
        this.enablePvReportingOnDevice(connection, deviceIdentification);
        this.enableBatteryReportingOnDevice(connection, deviceIdentification);
        this.enableEngineReportingOnDevice(connection, deviceIdentification);
        this.enableLoadReportingOnDevice(connection, deviceIdentification);
    }

    private void enableRtuReportingOnDevice(final DeviceConnection connection, final String deviceIdentification) {
        this.enableStatusReportingOnDevice(connection, deviceIdentification, LogicalDevice.RTU_ONE,
                DataAttribute.REPORT_STATUS_ONE);
    }

    private void enablePvReportingOnDevice(final DeviceConnection connection, final String deviceIdentification) {
        this.enableStatusReportingOnDevice(connection, deviceIdentification, LogicalDevice.PV_ONE,
                DataAttribute.REPORT_STATUS_ONE);
        this.enableStatusReportingOnDevice(connection, deviceIdentification, LogicalDevice.PV_TWO,
                DataAttribute.REPORT_STATUS_ONE);
        this.enableStatusReportingOnDevice(connection, deviceIdentification, LogicalDevice.PV_THREE,
                DataAttribute.REPORT_STATUS_ONE);

        this.enableMeasurementReportingOnDevice(connection, deviceIdentification, LogicalDevice.PV_ONE,
                DataAttribute.REPORT_MEASUREMENTS_ONE);
        this.enableMeasurementReportingOnDevice(connection, deviceIdentification, LogicalDevice.PV_TWO,
                DataAttribute.REPORT_MEASUREMENTS_ONE);
        this.enableMeasurementReportingOnDevice(connection, deviceIdentification, LogicalDevice.PV_THREE,
                DataAttribute.REPORT_MEASUREMENTS_ONE);
    }

    private void enableBatteryReportingOnDevice(final DeviceConnection connection, final String deviceIdentification) {
        this.enableStatusReportingOnDevice(connection, deviceIdentification, LogicalDevice.BATTERY_ONE,
                DataAttribute.REPORT_STATUS_ONE);
        this.enableStatusReportingOnDevice(connection, deviceIdentification, LogicalDevice.BATTERY_TWO,
                DataAttribute.REPORT_STATUS_ONE);

        this.enableMeasurementReportingOnDevice(connection, deviceIdentification, LogicalDevice.BATTERY_ONE,
                DataAttribute.REPORT_MEASUREMENTS_ONE);
        this.enableMeasurementReportingOnDevice(connection, deviceIdentification, LogicalDevice.BATTERY_TWO,
                DataAttribute.REPORT_MEASUREMENTS_ONE);
    }

    private void enableEngineReportingOnDevice(final DeviceConnection connection, final String deviceIdentification) {
        this.enableStatusReportingOnDevice(connection, deviceIdentification, LogicalDevice.ENGINE_ONE,
                DataAttribute.REPORT_STATUS_ONE);
        this.enableStatusReportingOnDevice(connection, deviceIdentification, LogicalDevice.ENGINE_TWO,
                DataAttribute.REPORT_STATUS_ONE);
        this.enableStatusReportingOnDevice(connection, deviceIdentification, LogicalDevice.ENGINE_THREE,
                DataAttribute.REPORT_STATUS_ONE);

        this.enableMeasurementReportingOnDevice(connection, deviceIdentification, LogicalDevice.ENGINE_ONE,
                DataAttribute.REPORT_MEASUREMENTS_ONE);
        this.enableMeasurementReportingOnDevice(connection, deviceIdentification, LogicalDevice.ENGINE_TWO,
                DataAttribute.REPORT_MEASUREMENTS_ONE);
        this.enableMeasurementReportingOnDevice(connection, deviceIdentification, LogicalDevice.ENGINE_THREE,
                DataAttribute.REPORT_MEASUREMENTS_ONE);
    }

    private void enableLoadReportingOnDevice(final DeviceConnection connection, final String deviceIdentification) {

        this.enableStatusReportingOnDevice(connection, deviceIdentification, LogicalDevice.LOAD_ONE,
                DataAttribute.REPORT_STATUS_ONE);

        this.enableMeasurementReportingOnDevice(connection, deviceIdentification, LogicalDevice.LOAD_ONE,
                DataAttribute.REPORT_MEASUREMENTS_ONE);
    }

    private void enableStatusReportingOnDevice(final DeviceConnection deviceConnection,
            final String deviceIdentification, final LogicalDevice logicalDevice, final DataAttribute reportName) {

        LOGGER.info("Allowing device {} to send events", deviceIdentification);

        try {
            final NodeContainer reportingNode = deviceConnection.getFcModelNode(logicalDevice,
                    LogicalNode.LOGICAL_NODE_ZERO, reportName, Fc.BR);
            reportingNode.writeBoolean(SubDataAttribute.ENABLE_REPORTING, true);
        } catch (final NullPointerException e) {
            LOGGER.debug("NullPointerException", e);
            LOGGER.warn("Skip enable reporting for device {}, report {}.", logicalDevice, reportName.getDescription());
        } catch (final NodeWriteException e) {
            LOGGER.debug("NodeWriteException", e);
            LOGGER.error("Enable reporting for device {}, report {}, failed with exception: {}", logicalDevice,
                    reportName.getDescription(), e.getMessage());
        }

    }

    private void enableMeasurementReportingOnDevice(final DeviceConnection deviceConnection,
            final String deviceIdentification, final LogicalDevice logicalDevice, final DataAttribute reportName) {

        LOGGER.info("Allowing device {} to send events", deviceIdentification);

        try {
            final NodeContainer reportingNode = deviceConnection.getFcModelNode(logicalDevice,
                    LogicalNode.LOGICAL_NODE_ZERO, reportName, Fc.RP);
            reportingNode.writeBoolean(SubDataAttribute.ENABLE_REPORTING, true);
        } catch (final NullPointerException e) {
            LOGGER.debug("NullPointerException", e);
            LOGGER.warn("Skip enable reporting for device {}, report {}.", logicalDevice, reportName.getDescription());
        } catch (final NodeWriteException e) {
            LOGGER.debug("NodeWriteException", e);
            LOGGER.error("Enable reporting for device {}, report {}, failed with exception: {}", logicalDevice,
                    reportName.getDescription(), e.getMessage());
        }

    }
}
