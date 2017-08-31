/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.reporting;

import java.util.Collection;

import org.openmuc.openiec61850.Rcb;
import org.openmuc.openiec61850.ServerModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.adapter.protocol.iec61850.exceptions.NodeWriteException;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.NodeContainer;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.SubDataAttribute;

public class Iec61850RtuDeviceReportingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850RtuDeviceReportingService.class);

    public void enableReportingOnDevice(final DeviceConnection connection, final String deviceIdentification) {
        final ServerModel serverModel = connection.getConnection().getServerModel();

        this.enableReports(connection, serverModel.getBrcbs());
        this.enableReports(connection, serverModel.getUrcbs());

    }

    private void enableReports(final DeviceConnection connection, final Collection<? extends Rcb> reports) {
        for (final Rcb report : reports) {
            try {
                final NodeContainer node = new NodeContainer(connection, report);
                node.writeBoolean(SubDataAttribute.ENABLE_REPORTING, true);
            } catch (final NullPointerException e) {
                LOGGER.debug("NullPointerException", e);
                LOGGER.warn("Skip enable reporting for report {}.", report.getReference().getName());
            } catch (final NodeWriteException e) {
                LOGGER.debug("NodeWriteException", e);
                LOGGER.error("Enable reporting for report {}, failed with exception: {}",
                        report.getReference().getName(), e.getMessage());
            }
        }
    }
}
