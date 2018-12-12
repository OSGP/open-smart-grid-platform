package org.opensmartgridplatform.adapter.protocol.dlms.application.mapping;

import org.apache.commons.codec.binary.Hex;
import org.assertj.core.api.Assertions;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKey;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SmartMeteringDeviceDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SmartMeteringDeviceDtoBuilder;

public class DeviceConverterTest {
    private static final long CURRENT_MILLIS = 1234567890L;
    private DeviceConverter converter = new DeviceConverter();

    @Before
    public void setUp() {
        DateTimeUtils.setCurrentMillisFixed(CURRENT_MILLIS);
    }

    @After
    public void tearDown() {
        DateTimeUtils.setCurrentMillisOffset(0);
    }

    @Test
    public void convertsSmartMeteringDtoToDlmsDevice() {
        SmartMeteringDeviceDto dto = new SmartMeteringDeviceDtoBuilder().build();
        DlmsDevice result = converter.convertTo(dto, null, null);

        DlmsDevice expected = converted(dto);

        Assertions.assertThat(result).isEqualToComparingFieldByFieldRecursively(expected);
    }

    private DlmsDevice converted(SmartMeteringDeviceDto dto) {
        final DlmsDevice dlmsDevice = new DlmsDevice();
        dlmsDevice.setDeviceIdentification(dto.getDeviceIdentification());
        dlmsDevice.setCommunicationMethod(dto.getCommunicationMethod());
        dlmsDevice.setCommunicationProvider(dto.getCommunicationProvider());
        dlmsDevice.setIccId(dto.getICCId());
        dlmsDevice.setHls3Active(dto.isHLS3Active());
        dlmsDevice.setHls4Active(dto.isHLS4Active());
        dlmsDevice.setHls5Active(dto.isHLS5Active());
        dlmsDevice.setMbusIdentificationNumber(dto.getMbusIdentificationNumber());
        dlmsDevice.setMbusManufacturerIdentification(dto.getMbusManufacturerIdentification());
        dlmsDevice.setProtocol("DSMR", dto.getDSMRVersion());

        dlmsDevice.addSecurityKey(new SecurityKey(dlmsDevice, SecurityKeyType.E_METER_MASTER,
                Hex.encodeHexString(dto.getMasterKey()), dto.getDeliveryDate(), null));
        dlmsDevice.addSecurityKey(new SecurityKey(dlmsDevice, SecurityKeyType.E_METER_AUTHENTICATION,
                Hex.encodeHexString(dto.getAuthenticationKey()), dto.getDeliveryDate(), null));
        dlmsDevice.addSecurityKey(new SecurityKey(dlmsDevice, SecurityKeyType.E_METER_ENCRYPTION,
                Hex.encodeHexString(dto.getGlobalEncryptionUnicastKey()), dto.getDeliveryDate(), null));
        dlmsDevice.addSecurityKey(new SecurityKey(dlmsDevice, SecurityKeyType.G_METER_MASTER,
                Hex.encodeHexString(dto.getMbusDefaultKey()), dto.getDeliveryDate(), null));
        return dlmsDevice;
    }
}