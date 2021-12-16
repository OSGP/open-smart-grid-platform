package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DeviceKeyProcessingRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.DeviceKeyProcessAlreadyRunningException;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class DeviceKeyProcessingServiceTest {

  @Mock DeviceKeyProcessingRepository deviceKeyProcessingRepository;

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
    when(this.deviceKeyProcessingRepository.insert(DEVICE_IDENTIFICATION)).thenReturn(true);
    assertDoesNotThrow(
        () -> this.deviceKeyProcessingService.startProcessing(DEVICE_IDENTIFICATION));
  }

  @Test
  public void testKeyProcessRunning() throws DeviceKeyProcessAlreadyRunningException {

    when(this.deviceKeyProcessingRepository.insert(DEVICE_IDENTIFICATION)).thenReturn(false);
    when(this.deviceKeyProcessingRepository.updateStartTime(same(DEVICE_IDENTIFICATION), any()))
        .thenReturn(0);
    assertThrows(
        DeviceKeyProcessAlreadyRunningException.class,
        () -> {
          this.deviceKeyProcessingService.startProcessing(DEVICE_IDENTIFICATION);
        });
  }

  @Test
  public void testPreviousKeyProcessTimedOut() {

    when(this.deviceKeyProcessingRepository.insert(DEVICE_IDENTIFICATION)).thenReturn(false);
    when(this.deviceKeyProcessingRepository.updateStartTime(eq(DEVICE_IDENTIFICATION), any()))
        .thenReturn(1);
    assertDoesNotThrow(
        () -> this.deviceKeyProcessingService.startProcessing(DEVICE_IDENTIFICATION));
  }
}
