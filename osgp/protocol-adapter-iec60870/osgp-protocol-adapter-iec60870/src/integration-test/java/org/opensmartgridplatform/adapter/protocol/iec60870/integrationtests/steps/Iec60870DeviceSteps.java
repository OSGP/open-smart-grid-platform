// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec60870.integrationtests.steps;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.opensmartgridplatform.adapter.protocol.iec60870.testutils.TestDefaults.DEFAULT_DEVICE_IDENTIFICATION;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.entities.Iec60870Device;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.repositories.Iec60870DeviceRepository;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceType;
import org.opensmartgridplatform.adapter.protocol.iec60870.testutils.factories.Iec60870DeviceFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class Iec60870DeviceSteps {

  @Autowired private Iec60870DeviceRepository repositoryMock;

  @Given("an IEC60870 device")
  public void givenIec60870Device() {
    this.anIec60870DeviceWithIdentification(DEFAULT_DEVICE_IDENTIFICATION);
  }

  @Given("a controlled station {string}")
  public void aControlledStation(final String deviceIdentification) {
    this.anIec60870DeviceWithIdentification(deviceIdentification);
  }

  private void anIec60870DeviceWithIdentification(final String deviceIdentification) {
    final Iec60870Device device = Iec60870DeviceFactory.createDefaultWith(deviceIdentification);
    this.deviceIsKnownInTheRepository(device);
  }

  private void deviceIsKnownInTheRepository(final Iec60870Device device) {
    this.devicesAreKnownInTheRepository(Collections.singletonList(device));
  }

  private void devicesAreKnownInTheRepository(final List<Iec60870Device> devices) {
    devices.forEach(
        device ->
            when(this.repositoryMock.findByDeviceIdentification(device.getDeviceIdentification()))
                .thenReturn(Optional.of(device)));
    when(this.repositoryMock.findByGatewayDeviceIdentification(anyString()))
        .thenAnswer(invocation -> this.getDevicesForGateway(invocation.getArgument(0), devices));
  }

  private List<Iec60870Device> getDevicesForGateway(
      final String gatewayDeviceIdentification, final List<Iec60870Device> devices) {
    return devices.stream()
        .filter(d -> gatewayDeviceIdentification.equals(d.getGatewayDeviceIdentification()))
        .collect(Collectors.toList());
  }

  @Given("IEC60870 devices")
  public void givenIec60870Devices(final DataTable devicesTable) {
    this.devicesAreKnownInTheRepository(
        devicesTable.asMaps().stream()
            .map(Iec60870DeviceFactory::fromSettings)
            .collect(Collectors.toList()));
  }

  @Given("a light sensor")
  public void aLightMeasurementDevice(final Map<String, String> deviceData) {
    final Iec60870Device device =
        Iec60870DeviceFactory.fromSettings(
            this.settingsWithDeviceType(deviceData, DeviceType.LIGHT_SENSOR));
    this.deviceIsKnownInTheRepository(device);
  }

  private Map<String, String> settingsWithDeviceType(
      final Map<String, String> settings, final DeviceType deviceType) {
    final Map<String, String> settingsWithDeviceType = new LinkedHashMap<>(settings);
    settingsWithDeviceType.put(Iec60870DeviceFactory.KEY_DEVICE_TYPE, deviceType.name());
    return settingsWithDeviceType;
  }

  public Optional<Iec60870Device> getDevice(final String deviceIdentification) {
    return this.repositoryMock.findByDeviceIdentification(deviceIdentification);
  }
}
