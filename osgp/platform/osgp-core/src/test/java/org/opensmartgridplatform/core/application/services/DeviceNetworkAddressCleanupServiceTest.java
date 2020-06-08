/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
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

    private DeviceRepository deviceRepository;
    private DeviceNetworkAddressCleanupService deviceNetworkAddressCleanupService;

    @BeforeEach
    public void setUp() {
        this.deviceRepository = Mockito.mock(DeviceRepository.class);
        final boolean allowMultipleDevicesPerNetworkAddress = false;
        final List<String> ipRangesAllowingMultipleDevicesPerAddress = Collections.emptyList();
        this.deviceNetworkAddressCleanupService = new DeviceNetworkAddressCleanupService(this.deviceRepository,
                allowMultipleDevicesPerNetworkAddress, ipRangesAllowingMultipleDevicesPerAddress);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void duplicateAddressesAreAllowedForLoopbackAddresses() throws Exception {
        assertThat(this.deviceNetworkAddressCleanupService.allowDuplicateEntries(InetAddress.getLoopbackAddress()))
                .isTrue();
    }

    @Test
    public void noDevicesAreUpdatedWhenTheNetworkAddressIsNotUsed() throws Exception {
        final String host = "192.168.0.13";
        final InetAddress inetAddress = InetAddress.getByName(host);
        this.theNetworkAddressIsNotUsed(inetAddress);

        this.deviceNetworkAddressCleanupService.clearDuplicateAddresses("test-device", host);

        verify(this.deviceRepository, times(1)).findByNetworkAddress(inetAddress);
        verify(this.deviceRepository, never()).save(any(Device.class));
    }

    private void theNetworkAddressIsNotUsed(final InetAddress inetAddress) {
        when(this.deviceRepository.findByNetworkAddress(inetAddress)).thenReturn(Collections.emptyList());
    }

    @Test
    public void noDevicesAreUpdatedWhenDuplicateAddressesAreAllowed() throws Exception {
        final String host = "192.168.0.13";
        final InetAddress inetAddress = InetAddress.getByName(host);
        final boolean allowMultipleDevicesPerNetworkAddress = true;
        final List<String> ipRangesAllowingMultipleDevicesPerAddress = Collections.emptyList();
        this.deviceNetworkAddressCleanupService = new DeviceNetworkAddressCleanupService(this.deviceRepository,
                allowMultipleDevicesPerNetworkAddress, ipRangesAllowingMultipleDevicesPerAddress);

        this.deviceNetworkAddressCleanupService.clearDuplicateAddresses("test-device", host);

        verify(this.deviceRepository, never()).findByNetworkAddress(inetAddress);
        verify(this.deviceRepository, never()).save(any(Device.class));
    }

    @Test
    public void noDevicesAreUpdatedWhenTheNetworkAddressIsExplicitlyAllowedWithDuplicates() throws Exception {
        final String host = "192.168.0.13";
        final InetAddress inetAddress = InetAddress.getByName(host);
        final boolean allowMultipleDevicesPerNetworkAddress = false;
        final List<String> ipRangesAllowingMultipleDevicesPerAddress = Collections.singletonList(host);
        this.deviceNetworkAddressCleanupService = new DeviceNetworkAddressCleanupService(this.deviceRepository,
                allowMultipleDevicesPerNetworkAddress, ipRangesAllowingMultipleDevicesPerAddress);

        this.deviceNetworkAddressCleanupService.clearDuplicateAddresses("test-device", host);

        verify(this.deviceRepository, never()).findByNetworkAddress(inetAddress);
        verify(this.deviceRepository, never()).save(any(Device.class));
    }

    @Test
    public void noDevicesAreUpdatedWhenTheNetworkAddressIsPartOfARangeAllowingDuplicates() throws Exception {
        final String host = "192.168.0.13";
        final InetAddress inetAddress = InetAddress.getByName(host);
        final boolean allowMultipleDevicesPerNetworkAddress = false;
        final List<String> ipRangesAllowingMultipleDevicesPerAddress = Arrays.asList("10.123.18.131",
                "192.168.0.1-192.168.0.20", "172.16.0.0-172.31.255.255");
        this.deviceNetworkAddressCleanupService = new DeviceNetworkAddressCleanupService(this.deviceRepository,
                allowMultipleDevicesPerNetworkAddress, ipRangesAllowingMultipleDevicesPerAddress);

        this.deviceNetworkAddressCleanupService.clearDuplicateAddresses("test-device", host);

        verify(this.deviceRepository, never()).findByNetworkAddress(inetAddress);
        verify(this.deviceRepository, never()).save(any(Device.class));
    }

    @Test
    public void loopbackAddressesAreAlwaysAllowedToHaveDuplicates() throws Exception {
        final InetAddress loopbackAddress = InetAddress.getLoopbackAddress();
        final String host = loopbackAddress.getHostAddress();
        final boolean allowMultipleDevicesPerNetworkAddress = false;
        final List<String> ipRangesAllowingMultipleDevicesPerAddress = Collections.emptyList();
        this.deviceNetworkAddressCleanupService = new DeviceNetworkAddressCleanupService(this.deviceRepository,
                allowMultipleDevicesPerNetworkAddress, ipRangesAllowingMultipleDevicesPerAddress);

        this.deviceNetworkAddressCleanupService.clearDuplicateAddresses("test-device", host);

        verify(this.deviceRepository, never()).findByNetworkAddress(loopbackAddress);
        verify(this.deviceRepository, never()).save(any(Device.class));
    }

    @Test
    public void devicesAreUpdatedWhenTheNetworkAddressIsNotAllowedToHaveDuplicates() throws Exception {
        final String host = "192.168.0.13";
        final InetAddress inetAddress = InetAddress.getByName(host);
        this.theNetworkAddressIsUsedBy(inetAddress, "device1", "device2");
        final boolean allowMultipleDevicesPerNetworkAddress = false;
        final List<String> ipRangesAllowingMultipleDevicesPerAddress = Collections.emptyList();
        this.deviceNetworkAddressCleanupService = new DeviceNetworkAddressCleanupService(this.deviceRepository,
                allowMultipleDevicesPerNetworkAddress, ipRangesAllowingMultipleDevicesPerAddress);

        this.deviceNetworkAddressCleanupService.clearDuplicateAddresses("test-device", host);

        verify(this.deviceRepository, times(1)).findByNetworkAddress(inetAddress);
        final ArgumentCaptor<Device> deviceCaptor = ArgumentCaptor.forClass(Device.class);
        verify(this.deviceRepository, times(2)).save(deviceCaptor.capture());
        final List<Device> savedDevices = deviceCaptor.getAllValues();
        final boolean device1IsSavedWithClearedNetworkAddress = savedDevices.stream()
                .anyMatch(device -> "device1".equals(device.getDeviceIdentification())
                        && device.getNetworkAddress() == null);
        assertThat(device1IsSavedWithClearedNetworkAddress).as("device1 is saved without network address").isTrue();
        final boolean device2IsSavedWithClearedNetworkAddress = savedDevices.stream()
                .anyMatch(device -> "device2".equals(device.getDeviceIdentification())
                        && device.getNetworkAddress() == null);
        assertThat(device2IsSavedWithClearedNetworkAddress).as("device2 is saved without network address").isTrue();
    }

    private void theNetworkAddressIsUsedBy(final InetAddress inetAddress, final String... deviceIdentifications) {
        final List<Device> devicesWithSameNetworkAddress = Arrays.stream(deviceIdentifications)
                .map(deviceIdentification -> {
                    final Device device = new Device(deviceIdentification);
                    device.updateRegistrationData(inetAddress, "deviceType");
                    return device;
                })
                .collect(Collectors.toList());
        when(this.deviceRepository.findByNetworkAddress(inetAddress)).thenReturn(devicesWithSameNetworkAddress);
    }
}
