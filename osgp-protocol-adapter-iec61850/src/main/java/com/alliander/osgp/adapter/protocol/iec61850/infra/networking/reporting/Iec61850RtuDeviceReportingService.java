/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.reporting;

import org.openmuc.openiec61850.Fc;
import org.openmuc.openiec61850.ModelNode;
import org.openmuc.openiec61850.ServerModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.adapter.protocol.iec61850.exceptions.NodeWriteException;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.IED;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.LogicalNode;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.NodeContainer;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.SubDataAttribute;

public class Iec61850RtuDeviceReportingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850RtuDeviceReportingService.class);

    private static final String IED_NAME = IED.ZOWN_RTU.getDescription();

    public void enableReportingOnDevice(final DeviceConnection connection, final String deviceIdentification) {

        this.enableRtuReportingOnDevice(connection, deviceIdentification);
        this.enablePvReportingOnDevice(connection, deviceIdentification);
        this.enableBatteryReportingOnDevice(connection, deviceIdentification);
        this.enableEngineReportingOnDevice(connection, deviceIdentification);
        this.enableLoadReportingOnDevice(connection, deviceIdentification);
        this.enableChpReportingOnDevice(connection, deviceIdentification);
        this.enableHeatBufferReportingOnDevice(connection, deviceIdentification);
        this.enableGasFurnaceReportingOnDevice(connection, deviceIdentification);
    }

    private void enableRtuReportingOnDevice(final DeviceConnection connection, final String deviceIdentification) {

        final ServerModel serverModel = connection.getConnection().getServerModel();
        final String rtuPrefix = LogicalDevice.RTU.getDescription();
        int i = 1;
        String logicalDeviceName = rtuPrefix + i;
        ModelNode rtuNode = serverModel.getChild(IED_NAME + logicalDeviceName);
        while (rtuNode != null) {
            this.enableStatusReportingOnDevice(connection, deviceIdentification, LogicalDevice.RTU, i,
                    DataAttribute.REPORT_STATUS_ONE);
            i += 1;
            logicalDeviceName = rtuPrefix + i;
            rtuNode = serverModel.getChild(IED_NAME + logicalDeviceName);
        }
    }

    private void enablePvReportingOnDevice(final DeviceConnection connection, final String deviceIdentification) {

        final ServerModel serverModel = connection.getConnection().getServerModel();
        final String pvPrefix = LogicalDevice.PV.getDescription();
        int i = 1;
        String logicalDeviceName = pvPrefix + i;
        ModelNode pvNode = serverModel.getChild(IED_NAME + logicalDeviceName);
        while (pvNode != null) {
            this.enableStatusReportingOnDevice(connection, deviceIdentification, LogicalDevice.PV, i,
                    DataAttribute.REPORT_STATUS_ONE);
            this.enableMeasurementReportingOnDevice(connection, deviceIdentification, LogicalDevice.PV, i,
                    DataAttribute.REPORT_MEASUREMENTS_ONE);
            i += 1;
            logicalDeviceName = pvPrefix + i;
            pvNode = serverModel.getChild(IED_NAME + logicalDeviceName);
        }
    }

    private void enableBatteryReportingOnDevice(final DeviceConnection connection, final String deviceIdentification) {

        final ServerModel serverModel = connection.getConnection().getServerModel();
        final String batteryPrefix = LogicalDevice.BATTERY.getDescription();
        int i = 1;
        String logicalDeviceName = batteryPrefix + i;
        ModelNode batteryNode = serverModel.getChild(IED_NAME + logicalDeviceName);
        while (batteryNode != null) {
            this.enableStatusReportingOnDevice(connection, deviceIdentification, LogicalDevice.BATTERY, i,
                    DataAttribute.REPORT_STATUS_ONE);
            this.enableMeasurementReportingOnDevice(connection, deviceIdentification, LogicalDevice.BATTERY, i,
                    DataAttribute.REPORT_MEASUREMENTS_ONE);
            i += 1;
            logicalDeviceName = batteryPrefix + i;
            batteryNode = serverModel.getChild(IED_NAME + logicalDeviceName);
        }
    }

    private void enableEngineReportingOnDevice(final DeviceConnection connection, final String deviceIdentification) {

        final ServerModel serverModel = connection.getConnection().getServerModel();
        final String enginePrefix = LogicalDevice.ENGINE.getDescription();
        int i = 1;
        String logicalDeviceName = enginePrefix + i;
        ModelNode engineNode = serverModel.getChild(IED_NAME + logicalDeviceName);
        while (engineNode != null) {
            this.enableStatusReportingOnDevice(connection, deviceIdentification, LogicalDevice.ENGINE, i,
                    DataAttribute.REPORT_STATUS_ONE);
            this.enableMeasurementReportingOnDevice(connection, deviceIdentification, LogicalDevice.ENGINE, i,
                    DataAttribute.REPORT_MEASUREMENTS_ONE);
            i += 1;
            logicalDeviceName = enginePrefix + i;
            engineNode = serverModel.getChild(IED_NAME + logicalDeviceName);
        }
    }

    private void enableLoadReportingOnDevice(final DeviceConnection connection, final String deviceIdentification) {

        final ServerModel serverModel = connection.getConnection().getServerModel();
        final String loadPrefix = LogicalDevice.LOAD.getDescription();
        int i = 1;
        String logicalDeviceName = loadPrefix + i;
        ModelNode loadNode = serverModel.getChild(IED_NAME + logicalDeviceName);
        while (loadNode != null) {
            this.enableStatusReportingOnDevice(connection, deviceIdentification, LogicalDevice.LOAD, i,
                    DataAttribute.REPORT_STATUS_ONE);
            this.enableMeasurementReportingOnDevice(connection, deviceIdentification, LogicalDevice.LOAD, i,
                    DataAttribute.REPORT_MEASUREMENTS_ONE);
            i += 1;
            logicalDeviceName = loadPrefix + i;
            loadNode = serverModel.getChild(IED_NAME + logicalDeviceName);
        }
    }

    private void enableHeatBufferReportingOnDevice(final DeviceConnection connection,
            final String deviceIdentification) {

        final ServerModel serverModel = connection.getConnection().getServerModel();
        final String heatBufferPrefix = LogicalDevice.HEAT_BUFFER.getDescription();
        int i = 1;
        String logicalDeviceName = heatBufferPrefix + i;
        ModelNode heatBufferNode = serverModel.getChild(IED_NAME + logicalDeviceName);
        while (heatBufferNode != null) {
            this.enableStatusReportingOnDevice(connection, deviceIdentification, LogicalDevice.HEAT_BUFFER, i,
                    DataAttribute.REPORT_STATUS_ONE);
            this.enableMeasurementReportingOnDevice(connection, deviceIdentification, LogicalDevice.HEAT_BUFFER, i,
                    DataAttribute.REPORT_MEASUREMENTS_ONE);
            i += 1;
            logicalDeviceName = heatBufferPrefix + i;
            heatBufferNode = serverModel.getChild(IED_NAME + logicalDeviceName);
        }
    }

    private void enableChpReportingOnDevice(final DeviceConnection connection, final String deviceIdentification) {

        final ServerModel serverModel = connection.getConnection().getServerModel();
        final String chpPrefix = LogicalDevice.CHP.getDescription();
        int i = 1;
        String logicalDeviceName = chpPrefix + i;
        ModelNode chpNode = serverModel.getChild(IED_NAME + logicalDeviceName);
        while (chpNode != null) {
            this.enableStatusReportingOnDevice(connection, deviceIdentification, LogicalDevice.CHP, i,
                    DataAttribute.REPORT_STATUS_ONE);
            this.enableMeasurementReportingOnDevice(connection, deviceIdentification, LogicalDevice.CHP, i,
                    DataAttribute.REPORT_MEASUREMENTS_ONE);
            i += 1;
            logicalDeviceName = chpPrefix + i;
            chpNode = serverModel.getChild(IED_NAME + logicalDeviceName);
        }
    }

    private void enableGasFurnaceReportingOnDevice(final DeviceConnection connection,
            final String deviceIdentification) {

        final ServerModel serverModel = connection.getConnection().getServerModel();
        final String gasFurnacePrefix = LogicalDevice.GAS_FURNACE.getDescription();
        int i = 1;
        String logicalDeviceName = gasFurnacePrefix + i;
        ModelNode gasFurnaceNode = serverModel.getChild(IED_NAME + logicalDeviceName);
        while (gasFurnaceNode != null) {
            this.enableStatusReportingOnDevice(connection, deviceIdentification, LogicalDevice.GAS_FURNACE, i,
                    DataAttribute.REPORT_STATUS_ONE);
            this.enableMeasurementReportingOnDevice(connection, deviceIdentification, LogicalDevice.GAS_FURNACE, i,
                    DataAttribute.REPORT_MEASUREMENTS_ONE);
            i += 1;
            logicalDeviceName = gasFurnacePrefix + i;
            gasFurnaceNode = serverModel.getChild(IED_NAME + logicalDeviceName);
        }
    }

    private void enableStatusReportingOnDevice(final DeviceConnection deviceConnection,
            final String deviceIdentification, final LogicalDevice logicalDevice, final int logicalDeviceIndex,
            final DataAttribute reportName) {

        LOGGER.info("Allowing device {} to send events", deviceIdentification);

        try {
            final NodeContainer reportingNode = deviceConnection.getFcModelNode(logicalDevice, logicalDeviceIndex,
                    LogicalNode.LOGICAL_NODE_ZERO, reportName, Fc.BR);
            reportingNode.writeBoolean(SubDataAttribute.ENABLE_REPORTING, true);
        } catch (final NullPointerException e) {
            LOGGER.debug("NullPointerException", e);
            LOGGER.warn("Skip enable reporting for device {}{}, report {}.", logicalDevice, logicalDeviceIndex,
                    reportName.getDescription());
        } catch (final NodeWriteException e) {
            LOGGER.debug("NodeWriteException", e);
            LOGGER.error("Enable reporting for device {}{}, report {}, failed with exception: {}", logicalDevice,
                    logicalDeviceIndex, reportName.getDescription(), e.getMessage());
        }

    }

    private void enableMeasurementReportingOnDevice(final DeviceConnection deviceConnection,
            final String deviceIdentification, final LogicalDevice logicalDevice, final int logicalDeviceIndex,
            final DataAttribute reportName) {

        LOGGER.info("Allowing device {} to send events", deviceIdentification);

        try {
            final NodeContainer reportingNode = deviceConnection.getFcModelNode(logicalDevice, logicalDeviceIndex,
                    LogicalNode.LOGICAL_NODE_ZERO, reportName, Fc.RP);
            reportingNode.writeBoolean(SubDataAttribute.ENABLE_REPORTING, true);
        } catch (final NullPointerException e) {
            LOGGER.debug("NullPointerException", e);
            LOGGER.warn("Skip enable reporting for device {}{}, report {}.", logicalDevice, logicalDeviceIndex,
                    reportName.getDescription());
        } catch (final NodeWriteException e) {
            LOGGER.debug("NodeWriteException", e);
            LOGGER.error("Enable reporting for device {}{}, report {}, failed with exception: {}", logicalDevice,
                    logicalDeviceIndex, reportName.getDescription(), e.getMessage());
        }

    }

}
