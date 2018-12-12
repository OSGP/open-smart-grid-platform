package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import org.opensmartgridplatform.dto.testutil.DateBuilder;

/** Creates instances, for testing purposes only. */
public class SmartMeteringDeviceDtoBuilder {
    private static int counter = 0;

    public SmartMeteringDeviceDto build() {
        counter += 1;
        final SmartMeteringDeviceDto dto = new SmartMeteringDeviceDto();
        dto.setAuthenticationKey(("authenticationKey" + counter).getBytes());
        dto.setCommunicationMethod("communicationMethod" + counter);
        dto.setCommunicationProvider("communicationProvider" + counter);
        dto.setDeliveryDate(new DateBuilder().build());
        dto.setDeviceIdentification("devicerIdentification" + counter);
        dto.setDeviceType("deviceType" + counter);
        dto.setDSMRVersion("dsmrVersion" + counter);
        dto.setGlobalEncryptionUnicastKey(("globalEncryptionUnicastKey" + counter).getBytes());
        dto.setHLS3Active(true);
        dto.setHLS4Active(true);
        dto.setHLS5Active(true);
        dto.setICCId("ICCId" + counter);
        dto.setMasterKey(("masterKey" + counter).getBytes());
        dto.setMbusIdentificationNumber(1000L + counter);
        dto.setMbusManufacturerIdentification("mbusManufacturerIdentification" + counter);
        dto.setMbusVersion((short) counter);
        dto.setMbusDeviceTypeIdentification((short) (100 + counter));
        dto.setMbusDefaultKey(("defaultKey" + counter).getBytes());
        dto.setSupplier("supplier" + counter);
        return dto;
    }
}