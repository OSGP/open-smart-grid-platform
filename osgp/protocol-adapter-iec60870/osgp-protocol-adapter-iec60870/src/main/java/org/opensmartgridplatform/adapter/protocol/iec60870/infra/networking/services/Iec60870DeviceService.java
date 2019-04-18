/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.infra.networking.services;

import org.openmuc.j60870.ConnectionEventListener;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.entities.Iec60870Device;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.repositories.Iec60870DeviceRepository;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceConnection;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceConnectionParameters;
import org.opensmartgridplatform.shared.exceptionhandling.ConnectionFailureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Iec60870DeviceService {

    @Autowired
    private Iec60870DeviceConnectionService iec60870DeviceConnectionService;

    @Autowired
    private Iec60870DeviceRepository deviceRepository;

    public DeviceConnection connectToDevice(final String deviceIdentification, final String ipAddress,
            final ConnectionEventListener asduListener) throws ConnectionFailureException {

        final Iec60870Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);

        final DeviceConnectionParameters deviceConnectionParameters = DeviceConnectionParameters.newBuilder()
                .ipAddress(ipAddress).deviceIdentification(deviceIdentification)
                .commonAddress(device.getCommonAddress()).port(device.getPort()).build();

        return this.iec60870DeviceConnectionService.connect(deviceConnectionParameters, asduListener);
    }

}
