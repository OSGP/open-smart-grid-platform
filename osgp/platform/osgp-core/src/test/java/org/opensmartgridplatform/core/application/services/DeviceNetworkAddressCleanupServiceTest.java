// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.core.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;

@ExtendWith(MockitoExtension.class)
public class DeviceNetworkAddressCleanupServiceTest {

  private static final String DEVICE_IDENTIFICATION = "test-device";

  private static final String LOCALHOST = "127.0.0.1";
  private static final String IP_ADDRESS = "192.168.0.13";

  private DeviceRepository deviceRepository;
  private DeviceNetworkAddressCleanupService deviceNetworkAddressCleanupService;
  private String host;
  private String networkAddress;

  private void withHostAndInetAddress(final String host) {
    this.host = host;
    this.networkAddress = host;
  }

  private void setUpDeviceNetworkAddressCleanupService(
      final boolean allowMultipleDevicesPerNetworkAddress,
      final List<String> ipRangesAllowingMultipleDevicesPerAddress) {
    this.deviceRepository = Mockito.mock(DeviceRepository.class);
    this.deviceNetworkAddressCleanupService =
        new DeviceNetworkAddressCleanupService(
            this.deviceRepository,
            allowMultipleDevicesPerNetworkAddress,
            ipRangesAllowingMultipleDevicesPerAddress);
    MockitoAnnotations.initMocks(this);
  }

  private void withConfigurationNotAllowingDuplicates() {
    this.setUpDeviceNetworkAddressCleanupService(false, Collections.emptyList());
  }

  private void withConfigurationAllowingAllDuplicates() {
    this.setUpDeviceNetworkAddressCleanupService(true, Collections.emptyList());
  }

  private void withConfigurationAllowingDuplicatesFor(final String... configuredRanges) {
    this.setUpDeviceNetworkAddressCleanupService(false, Arrays.asList(configuredRanges));
  }

  @Test
  public void noDevicesAreCleanedWhenTheNetworkAddressIsNotUsed() throws Exception {
    this.withConfigurationNotAllowingDuplicates();
    this.withHostAndInetAddress(IP_ADDRESS);
    this.theNetworkAddressIsNotUsed(this.networkAddress);

    this.deviceNetworkAddressCleanupService.clearDuplicateAddresses(
        DEVICE_IDENTIFICATION, this.host);

    verify(this.deviceRepository, times(1)).findByNetworkAddress(this.networkAddress);
    verify(this.deviceRepository, never()).save(any(Device.class));
  }

  private void theNetworkAddressIsNotUsed(final String networkAddress) {
    when(this.deviceRepository.findByNetworkAddress(networkAddress))
        .thenReturn(Collections.emptyList());
  }

  @Test
  public void noDevicesAreCleanedWhenDuplicateAddressesAreAllowed() throws Exception {
    this.withConfigurationAllowingAllDuplicates();
    this.withHostAndInetAddress(IP_ADDRESS);

    this.deviceNetworkAddressCleanupService.clearDuplicateAddresses(
        DEVICE_IDENTIFICATION, this.host);

    verify(this.deviceRepository, never()).findByNetworkAddress(this.networkAddress);
    verify(this.deviceRepository, never()).save(any(Device.class));
  }

  @Test
  public void noDevicesAreCleanedWhenTheNetworkAddressIsExplicitlyAllowedWithDuplicates()
      throws Exception {
    this.withHostAndInetAddress(IP_ADDRESS);
    this.withConfigurationAllowingDuplicatesFor(this.host);

    this.deviceNetworkAddressCleanupService.clearDuplicateAddresses(
        DEVICE_IDENTIFICATION, this.host);

    verify(this.deviceRepository, never()).findByNetworkAddress(this.networkAddress);
    verify(this.deviceRepository, never()).save(any(Device.class));
  }

  @Test
  public void noDevicesAreCleanedWhenTheNetworkAddressIsPartOfARangeAllowingDuplicates()
      throws Exception {
    final String ipAddress = "172.16.138.27";
    this.withHostAndInetAddress(ipAddress);
    final String rangeContainingIpAddress = "172.16.130.0-172.16.139.255";
    this.withConfigurationAllowingDuplicatesFor(
        "10.1.1.111", rangeContainingIpAddress, "192.168.0.0-192.168.255.255");

    this.deviceNetworkAddressCleanupService.clearDuplicateAddresses(
        DEVICE_IDENTIFICATION, this.host);

    verify(this.deviceRepository, never()).findByNetworkAddress(this.networkAddress);
    verify(this.deviceRepository, never()).save(any(Device.class));
  }

  @Test
  public void localhostIsAlwaysAllowedToHaveDuplicates() throws Exception {
    this.withConfigurationNotAllowingDuplicates();
    this.withHostAndInetAddress(LOCALHOST);

    this.deviceNetworkAddressCleanupService.clearDuplicateAddresses(
        DEVICE_IDENTIFICATION, this.host);

    verify(this.deviceRepository, never()).findByNetworkAddress(this.networkAddress);
    verify(this.deviceRepository, never()).save(any(Device.class));
  }

  @Test
  public void devicesAreCleanedWhenTheNetworkAddressIsNotAllowedToHaveDuplicates()
      throws Exception {
    this.withConfigurationNotAllowingDuplicates();
    this.withHostAndInetAddress(IP_ADDRESS);
    this.theNetworkAddressIsUsedBy(this.networkAddress, "device1", "device2");

    this.deviceNetworkAddressCleanupService.clearDuplicateAddresses(
        DEVICE_IDENTIFICATION, this.host);

    verify(this.deviceRepository, times(1)).findByNetworkAddress(this.networkAddress);
    final ArgumentCaptor<Device> deviceCaptor = ArgumentCaptor.forClass(Device.class);
    verify(this.deviceRepository, times(2)).save(deviceCaptor.capture());
    final List<Device> savedDevices = deviceCaptor.getAllValues();
    this.deviceIsSavedWithCleanedNetworkAddress("device1", savedDevices);
    this.deviceIsSavedWithCleanedNetworkAddress("device2", savedDevices);
  }

  private void theNetworkAddressIsUsedBy(
      final String networkAddress, final String... deviceIdentifications) {
    final List<Device> devicesWithSameNetworkAddress =
        Arrays.stream(deviceIdentifications)
            .map(
                deviceIdentification -> {
                  final Device device = new Device(deviceIdentification);
                  device.updateRegistrationData(networkAddress, "deviceType");
                  return device;
                })
            .toList();
    when(this.deviceRepository.findByNetworkAddress(networkAddress))
        .thenReturn(devicesWithSameNetworkAddress);
  }

  private void deviceIsSavedWithCleanedNetworkAddress(
      final String deviceIdentification, final List<Device> savedDevices) {

    final boolean deviceIsSavedWithCleanedNetworkAddress =
        savedDevices.stream()
            .anyMatch(
                device ->
                    deviceIdentification.equals(device.getDeviceIdentification())
                        && device.getNetworkAddress() == null);
    assertThat(deviceIsSavedWithCleanedNetworkAddress)
        .as(deviceIdentification + " is saved without network address")
        .isTrue();
  }
}
