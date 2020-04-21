/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.integrationtests.steps;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.opensmartgridplatform.adapter.protocol.iec60870.testutils.TestDefaults.DEFAULT_DEVICE_IDENTIFICATION;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.opensmartgridplatform.adapter.protocol.iec60870.domain.entities.Iec60870Device;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.repositories.Iec60870DeviceRepository;
import org.opensmartgridplatform.adapter.protocol.iec60870.testutils.factories.Iec60870DeviceFactory;
import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;

public class Iec60870DeviceSteps {

    @Autowired
    private Iec60870DeviceRepository repositoryMock;

    @Given("an IEC60870 device")
    public void givenIec60870Device() {
        final Iec60870Device device = Iec60870DeviceFactory.createDefaultWith(DEFAULT_DEVICE_IDENTIFICATION);
        when(this.repositoryMock.findByDeviceIdentification(DEFAULT_DEVICE_IDENTIFICATION))
                .thenReturn(Optional.of(device));
    }

    @Given("IEC60870 devices")
    public void givenIec60870Devices(final DataTable devicesTable) {

        final List<Iec60870Device> devices = new ArrayList<>();

        for (final Map<String, String> colums : devicesTable.asMaps()) {
            final Iec60870Device device = Iec60870DeviceFactory.fromSettings(colums);
            when(this.repositoryMock.findByDeviceIdentification(device.getDeviceIdentification()))
                    .thenReturn(Optional.of(device));
            devices.add(device);
        }

        when(this.repositoryMock.findByGatewayDeviceIdentification(anyString()))
                .thenAnswer(invocation -> this.getDevicesForGateway(invocation.getArgument(0), devices));
    }

    private List<Iec60870Device> getDevicesForGateway(final String gatewayDeviceIdentification,
            final List<Iec60870Device> devices) {
        return devices.stream()
                .filter(d -> gatewayDeviceIdentification.equals(d.getGatewayDeviceIdentification()))
                .collect(Collectors.toList());

    }

    public Optional<Iec60870Device> getDevice(final String deviceIdentification) {
        return this.repositoryMock.findByDeviceIdentification(deviceIdentification);
    }
}
