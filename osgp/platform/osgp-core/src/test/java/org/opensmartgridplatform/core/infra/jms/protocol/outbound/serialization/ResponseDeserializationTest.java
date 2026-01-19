package org.opensmartgridplatform.core.infra.jms.protocol.outbound.serialization;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.dto.valueobjects.*;

public class ResponseDeserializationTest {

  /**
   * Verifies that a {@link DeviceStatusDto} object serialized by the
   * gxf-publiclighting-message-transformer project can be successfully deserialized and interpreted
   * in this project.
   *
   * <p>The serialized input file device-status-response.ser is produced in the transformer project
   * by running the CreateSerializedObjectsTest. Copy that file into this project's {@code
   * src/test/resources/} directory.
   */
  @Test
  void testDeserializationDeviceStatusTest() {
    DeviceStatusDto dto;
    try (InputStream inputStream = getClass().getResourceAsStream("/device-status-response.ser");
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
      dto = (DeviceStatusDto) objectInputStream.readObject();
    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
    assertThat(dto).isNotNull();
    assertThat(dto.getLightValues().size()).isEqualTo(1);
    LightValueDto lightValueDto = dto.getLightValues().get(0);
    assertThat(lightValueDto.getIndex()).isEqualTo(1);
    assertThat(lightValueDto.isOn()).isTrue();
    assertThat(dto.getPreferredLinkType()).isEqualTo(LinkTypeDto.ETHERNET);
    assertThat(dto.getActualLinkType()).isEqualTo(LinkTypeDto.ETHERNET);
    assertThat(dto.getLightType()).isEqualTo(LightTypeDto.RELAY);
    assertThat(dto.getEventNotificationsMask()).isEqualTo(1);
    assertThat(dto.getNumberOfOutputs()).isEqualTo(3);
    assertThat(dto.getDcOutputVoltageMaximum()).isEqualTo(24000);
    assertThat(dto.getDcOutputVoltageCurrent()).isEqualTo(44);
    assertThat(dto.getMaximumOutputPowerOnDcOutput()).isEqualTo(15000);
    assertThat(dto.getSerialNumber()).isEqualTo("AAA0002025");
    assertThat(dto.getMacAddress()).isEqualTo("AA:BB:CC:DD");
    assertThat(dto.getHardwareId()).isEqualTo("HARDWARE_ID");
    assertThat(dto.getInternalFlashMemSize()).isEqualTo(16);
    assertThat(dto.getExternalFlashMemSize()).isEqualTo(32);
    assertThat(dto.getLastInternalTestResultCode()).isEqualTo(0);
    assertThat(dto.getStartupCounter()).isEqualTo(1);
    assertThat(dto.getBootLoaderVersion()).isEqualTo("0.1.0");
    assertThat(dto.getFirmwareVersion()).isEqualTo("0.2.0");
    assertThat(dto.getCurrentConfigurationBackUsed()).isEqualTo("x");
    assertThat(dto.getName()).isEqualTo("NAME");
    assertThat(dto.getCurrentTime()).isEqualTo("202501011500");
    assertThat(dto.getCurrentIp()).isEqualTo("192.168.2.2");
  }
}
