// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SmartMeteringDevice;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SmartMeteringDeviceDto;

public class InstallationMapperTest {

  private final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

  @Test
  public void mapsSmartMeteringDeviceToSmartMeter() {
    final SmartMeteringDevice source = new SmartMeteringDevice();
    source.setDeviceIdentification("device1");
    source.setDeviceType("typeA");
    source.setCommunicationMethod("skype");
    source.setCommunicationProvider("theInternet");
    source.setIccId("value");
    source.setProtocolName("protocolName");
    source.setProtocolVersion("latestVersion");
    source.setMasterKey("masterKey".getBytes());
    source.setGlobalEncryptionUnicastKey("globalEncryptionUnicastKey".getBytes());
    source.setAuthenticationKey("authenticationKey".getBytes());
    source.setSupplier("supplier");
    source.setHls3Active(true);
    source.setHls4Active(true);
    source.setHls3Active(true);
    source.setDeliveryDate(new Date());
    source.setMbusIdentificationNumber("12345678");
    source.setMbusManufacturerIdentification("XYZ");
    source.setMbusVersion((short) 66);
    source.setMbusDeviceTypeIdentification((short) 3);
    source.setMbusDefaultKey("mbusDefaultKey".getBytes());

    final SmartMeter result = this.mapperFactory.getMapperFacade().map(source, SmartMeter.class);

    final SmartMeter expected = this.toSmartMeter(source);
    assertThat(result)
        .isEqualToIgnoringGivenFields(
            expected, "id", "creationTime", "modificationTime", "version");
  }

  private SmartMeter toSmartMeter(final SmartMeteringDevice source) {
    final SmartMeter expected = new SmartMeter();
    expected.setDeviceIdentification(source.getDeviceIdentification());
    expected.setDeviceType(source.getDeviceType());
    expected.setSupplier(source.getSupplier());
    expected.setMbusIdentificationNumber(source.getMbusIdentificationNumber());
    expected.setMbusManufacturerIdentification(source.getMbusManufacturerIdentification());
    expected.setMbusVersion(source.getMbusVersion());
    expected.setMbusDeviceTypeIdentification(source.getMbusDeviceTypeIdentification());
    return expected;
  }

  @Test
  public void testSmartMeteringDeviceToSmartMeteringDeviceDtoMapping() {
    final SmartMeteringDevice smartMeteringDevice = new SmartMeteringDevice();
    smartMeteringDevice.setDeviceIdentification("device1");
    smartMeteringDevice.setDeviceType("typeA");
    smartMeteringDevice.setCommunicationMethod("skype");
    smartMeteringDevice.setCommunicationProvider("theInternet");
    smartMeteringDevice.setIccId("value");
    smartMeteringDevice.setProtocolName("protocolName");
    smartMeteringDevice.setProtocolVersion("latestVersion");
    smartMeteringDevice.setMasterKey("masterKey".getBytes());
    smartMeteringDevice.setGlobalEncryptionUnicastKey("globalEncryptionUnicastKey".getBytes());
    smartMeteringDevice.setAuthenticationKey("authenticationKey".getBytes());
    smartMeteringDevice.setSupplier("supplier");
    smartMeteringDevice.setHls3Active(true);
    smartMeteringDevice.setHls4Active(true);
    smartMeteringDevice.setHls5Active(true);
    smartMeteringDevice.setDeliveryDate(new Date());
    smartMeteringDevice.setMbusIdentificationNumber("12345678");
    smartMeteringDevice.setMbusManufacturerIdentification("XYZ");
    smartMeteringDevice.setMbusVersion((short) 112);
    smartMeteringDevice.setMbusDeviceTypeIdentification((short) 3);
    smartMeteringDevice.setMbusDefaultKey("mbusDefaultKey".getBytes());

    final SmartMeteringDeviceDto smartMeteringDeviceDto =
        this.mapperFactory.getMapperFacade().map(smartMeteringDevice, SmartMeteringDeviceDto.class);

    assertThat(smartMeteringDevice).isNotNull();
    assertThat(smartMeteringDeviceDto).isNotNull();
    assertThat(smartMeteringDeviceDto)
        .isEqualToIgnoringGivenFields(
            smartMeteringDevice, "hls3Active", "hls4Active", "hls5Active");
    assertThat(smartMeteringDeviceDto.isHls3Active()).isEqualTo(smartMeteringDevice.isHls3Active());
    assertThat(smartMeteringDeviceDto.isHls4Active()).isEqualTo(smartMeteringDevice.isHls4Active());
    assertThat(smartMeteringDeviceDto.isHls5Active()).isEqualTo(smartMeteringDevice.isHls5Active());
    assertThat(smartMeteringDeviceDto)
        .isNotNull()
        .isEqualToIgnoringGivenFields(smartMeteringDevice, "ipAddress", "btsId", "cellId");
  }
}
