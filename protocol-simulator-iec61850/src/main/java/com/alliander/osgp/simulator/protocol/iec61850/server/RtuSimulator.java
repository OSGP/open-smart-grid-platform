/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.simulator.protocol.iec61850.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PreDestroy;

import org.openmuc.openiec61850.BasicDataAttribute;
import org.openmuc.openiec61850.SclParseException;
import org.openmuc.openiec61850.ServerEventListener;
import org.openmuc.openiec61850.ServerModel;
import org.openmuc.openiec61850.ServerSap;
import org.openmuc.openiec61850.ServiceError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import com.alliander.osgp.simulator.protocol.iec61850.server.logicaldevices.Battery;
import com.alliander.osgp.simulator.protocol.iec61850.server.logicaldevices.Engine;
import com.alliander.osgp.simulator.protocol.iec61850.server.logicaldevices.Load;
import com.alliander.osgp.simulator.protocol.iec61850.server.logicaldevices.LogicalDevice;
import com.alliander.osgp.simulator.protocol.iec61850.server.logicaldevices.Pv;
import com.alliander.osgp.simulator.protocol.iec61850.server.logicaldevices.Rtu;

public class RtuSimulator implements ServerEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(RtuSimulator.class);

    private static final String PHYSICAL_DEVICE = "WAGO61850Server";

    private final List<LogicalDevice> logicalDevices = new ArrayList<>();

    private final ServerSap server;

    private final ServerModel serverModel;

    private boolean isStarted = false;

    public RtuSimulator(final int port, final InputStream sclFile) throws SclParseException {
        final List<ServerSap> serverSaps = ServerSap.getSapsFromSclFile(sclFile);
        this.server = serverSaps.get(0);
        this.server.setPort(port);
        this.serverModel = this.server.getModelCopy();

        this.logicalDevices.add(new Rtu(PHYSICAL_DEVICE, "RTU1", this.serverModel));
        this.logicalDevices.add(new Pv(PHYSICAL_DEVICE, "PV1", this.serverModel));
        this.logicalDevices.add(new Pv(PHYSICAL_DEVICE, "PV2", this.serverModel));
        this.logicalDevices.add(new Pv(PHYSICAL_DEVICE, "PV3", this.serverModel));
        this.logicalDevices.add(new Battery(PHYSICAL_DEVICE, "BATTERY1", this.serverModel));
        this.logicalDevices.add(new Battery(PHYSICAL_DEVICE, "BATTERY2", this.serverModel));
        this.logicalDevices.add(new Engine(PHYSICAL_DEVICE, "ENGINE1", this.serverModel));
        this.logicalDevices.add(new Engine(PHYSICAL_DEVICE, "ENGINE2", this.serverModel));
        this.logicalDevices.add(new Engine(PHYSICAL_DEVICE, "ENGINE3", this.serverModel));
        this.logicalDevices.add(new Load(PHYSICAL_DEVICE, "LOAD1", this.serverModel));
    }

    public void start() throws IOException {
        if (this.isStarted) {
            throw new IOException("Server is already started");
        }

        this.server.startListening(this);
        this.isStarted = true;
    }

    public void stop() {
        this.server.stop();
        LOGGER.info("Server was stopped.");
    }

    @PreDestroy
    private void destroy() {
        this.stop();
    }

    @Override
    public List<ServiceError> write(final List<BasicDataAttribute> bdas) {
        for (final BasicDataAttribute bda : bdas) {
            LOGGER.info("got a write request: " + bda);
        }

        return null;
    }

    @Override
    public void serverStoppedListening(final ServerSap serverSAP) {
        LOGGER.error("The SAP stopped listening");
    }

    @Scheduled(fixedDelay = 60000)
    public void generateData() {
        final Date timestamp = new Date();

        final List<BasicDataAttribute> values = new ArrayList<>();

        for (final LogicalDevice ld : this.logicalDevices) {
            values.addAll(ld.getValues(timestamp));
        }

        this.server.setValues(values);
        LOGGER.info("Generated values");
    }
}
