// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.DeviceKeyProcessAlreadyRunningException;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class DeviceKeyProcessingServiceTest {

  @Mock DlmsDeviceRepository dlmsDeviceRepository;

  @InjectMocks private DeviceKeyProcessingService deviceKeyProcessingService;

  static final String DEVICE_IDENTIFICATION = "device-1";

  @BeforeEach
  public void setUp() {
    ReflectionTestUtils.setField(
        this.deviceKeyProcessingService,
        "deviceKeyProcessingTimeout",
        java.time.Duration.parse("PT5M"));
  }

  @Test
  public void testNoKeyProcessRunning() {
    final DlmsDevice dlmsDevice = new DlmsDevice();
    dlmsDevice.setDeviceIdentification(DEVICE_IDENTIFICATION);

    when(this.dlmsDeviceRepository.findByDeviceIdentification(DEVICE_IDENTIFICATION))
        .thenReturn(dlmsDevice);
    when(this.dlmsDeviceRepository.setProcessingStartTime(
            eq(DEVICE_IDENTIFICATION), any(Instant.class)))
        .thenReturn(1);
    assertDoesNotThrow(
        () -> this.deviceKeyProcessingService.startProcessing(DEVICE_IDENTIFICATION));
  }

  @Test
  public void testKeyProcessRunning() throws DeviceKeyProcessAlreadyRunningException {

    final DlmsDevice dlmsDevice = new DlmsDevice();
    dlmsDevice.setDeviceIdentification(DEVICE_IDENTIFICATION);
    dlmsDevice.setKeyProcessingStartTime(Instant.now());
    when(this.dlmsDeviceRepository.findByDeviceIdentification(DEVICE_IDENTIFICATION))
        .thenReturn(dlmsDevice);
    when(this.dlmsDeviceRepository.setProcessingStartTime(
            eq(DEVICE_IDENTIFICATION), any(Instant.class)))
        .thenReturn(0);
    assertThrows(
        DeviceKeyProcessAlreadyRunningException.class,
        () -> {
          this.deviceKeyProcessingService.startProcessing(DEVICE_IDENTIFICATION);
        });
  }

  @Test
  public void testPreviousKeyProcessTimedOut() {

    final DlmsDevice dlmsDevice = new DlmsDevice();
    dlmsDevice.setDeviceIdentification(DEVICE_IDENTIFICATION);
    dlmsDevice.setKeyProcessingStartTime(
        Instant.now().minus(this.deviceKeyProcessingService.getDeviceKeyProcessingTimeout()));
    when(this.dlmsDeviceRepository.findByDeviceIdentification(DEVICE_IDENTIFICATION))
        .thenReturn(dlmsDevice);
    when(this.dlmsDeviceRepository.setProcessingStartTime(
            eq(DEVICE_IDENTIFICATION), any(Instant.class)))
        .thenReturn(1);
    assertDoesNotThrow(
        () -> this.deviceKeyProcessingService.startProcessing(DEVICE_IDENTIFICATION));
  }
}
