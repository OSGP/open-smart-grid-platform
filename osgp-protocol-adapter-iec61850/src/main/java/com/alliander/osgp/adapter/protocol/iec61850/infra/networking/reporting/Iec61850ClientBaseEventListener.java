/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.reporting;

import org.openmuc.openiec61850.ClientEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.adapter.protocol.iec61850.application.services.DeviceManagementService;
import com.alliander.osgp.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;

public abstract class Iec61850ClientBaseEventListener implements ClientEventListener {

    protected final Logger logger;

    /*
     * Node names of EvnRpn nodes that occur as members of the report dataset.
     */
    protected final String deviceIdentification;
    protected final DeviceManagementService deviceManagementService;
    protected Integer firstNewSqNum = null;

    public Iec61850ClientBaseEventListener(final String deviceIdentification,
            final DeviceManagementService deviceManagementService, final Class<?> loggerClass)
            throws ProtocolAdapterException {
        this.deviceManagementService = deviceManagementService;
        this.deviceIdentification = deviceIdentification;
        this.logger = LoggerFactory.getLogger(loggerClass);
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    /**
     * Before enabling reporting on the device, set the SqNum of the buffered
     * report data to be able to check if incoming reports have been received
     * already.
     *
     * @param value
     *            the value of SqNum of a BR node on the device.
     */
    public void setSqNum(final int value) {
        this.logger.info("First new SqNum for report listener for device: {} is: {}", this.deviceIdentification, value);
        this.firstNewSqNum = value;
    }
}
