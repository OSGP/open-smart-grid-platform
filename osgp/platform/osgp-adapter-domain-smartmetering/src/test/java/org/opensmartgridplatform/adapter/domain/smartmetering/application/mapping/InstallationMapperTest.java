/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Test;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SmartMeteringDevice;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SmartMeteringDeviceDto;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

public class InstallationMapperTest {

    private final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    @Test
    public void mapsSmartMeteringDeviceToSmartMeter() {
        final SmartMeteringDevice source = new SmartMeteringDevice();
        source.setDeviceIdentification("device1");
        source.setDeviceType("typeA");
        source.setCommunicationMethod("skype");
        source.setCommunicationProvider("theInternet");
        source.setICCId("value");
        source.setDSMRVersion("latestVersion");
        source.setMasterKey("masterKey".getBytes());
        source.setGlobalEncryptionUnicastKey("globalEncryptionUnicastKey".getBytes());
        source.setAuthenticationKey("authenticationKey".getBytes());
        source.setSupplier("supplier");
        source.setHLS3Active(true);
        source.setHLS4Active(true);
        source.setHLS5Active(true);
        source.setDeliveryDate(new Date());
        source.setMbusIdentificationNumber(12345678L);
        source.setMbusManufacturerIdentification("XYZ");
        source.setMbusVersion((short) 66);
        source.setMbusDeviceTypeIdentification((short) 3);
        source.setMbusDefaultKey("mbusDefaultKey".getBytes());

        final SmartMeter result = this.mapperFactory.getMapperFacade().map(source, SmartMeter.class);

        final SmartMeter expected = this.toSmartMeter(source);
        assertThat(result).isEqualToIgnoringGivenFields(expected, "id", "creationTime", "modificationTime", "version");
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
        smartMeteringDevice.setICCId("value");
        smartMeteringDevice.setDSMRVersion("latestVersion");
        smartMeteringDevice.setMasterKey("masterKey".getBytes());
        smartMeteringDevice.setGlobalEncryptionUnicastKey("globalEncryptionUnicastKey".getBytes());
        smartMeteringDevice.setAuthenticationKey("authenticationKey".getBytes());
        smartMeteringDevice.setSupplier("supplier");
        smartMeteringDevice.setHLS3Active(true);
        smartMeteringDevice.setHLS4Active(true);
        smartMeteringDevice.setHLS5Active(true);
        smartMeteringDevice.setDeliveryDate(new Date());
        smartMeteringDevice.setMbusIdentificationNumber(12345678L);
        smartMeteringDevice.setMbusManufacturerIdentification("XYZ");
        smartMeteringDevice.setMbusVersion((short) 112);
        smartMeteringDevice.setMbusDeviceTypeIdentification((short) 3);
        smartMeteringDevice.setMbusDefaultKey("mbusDefaultKey".getBytes());

        final SmartMeteringDeviceDto smartMeteringDeviceDto = this.mapperFactory.getMapperFacade()
                .map(smartMeteringDevice, SmartMeteringDeviceDto.class);

        assertNotNull(smartMeteringDevice);
        assertNotNull(smartMeteringDeviceDto);
        assertEquals(smartMeteringDevice.getDeviceIdentification(), smartMeteringDeviceDto.getDeviceIdentification());
        assertEquals(smartMeteringDevice.getDeviceType(), smartMeteringDeviceDto.getDeviceType());
        assertEquals(smartMeteringDevice.getCommunicationMethod(), smartMeteringDeviceDto.getCommunicationMethod());
        assertEquals(smartMeteringDevice.getCommunicationProvider(), smartMeteringDeviceDto.getCommunicationProvider());
        assertEquals(smartMeteringDevice.getICCId(), smartMeteringDeviceDto.getICCId());
        assertEquals(smartMeteringDevice.getDSMRVersion(), smartMeteringDeviceDto.getDSMRVersion());
        assertArrayEquals(smartMeteringDevice.getMasterKey(), smartMeteringDeviceDto.getMasterKey());
        assertArrayEquals(smartMeteringDevice.getGlobalEncryptionUnicastKey(),
                smartMeteringDeviceDto.getGlobalEncryptionUnicastKey());
        assertArrayEquals(smartMeteringDevice.getAuthenticationKey(), smartMeteringDeviceDto.getAuthenticationKey());
        assertEquals(smartMeteringDevice.getSupplier(), smartMeteringDeviceDto.getSupplier());
        assertEquals(smartMeteringDevice.isHLS3Active(), smartMeteringDeviceDto.isHLS3Active());
        assertEquals(smartMeteringDevice.isHLS4Active(), smartMeteringDeviceDto.isHLS4Active());
        assertEquals(smartMeteringDevice.isHLS5Active(), smartMeteringDeviceDto.isHLS5Active());
        assertEquals(smartMeteringDevice.getDeliveryDate(), smartMeteringDeviceDto.getDeliveryDate());
        assertEquals(smartMeteringDevice.getMbusIdentificationNumber(),
                smartMeteringDeviceDto.getMbusIdentificationNumber());
        assertEquals(smartMeteringDevice.getMbusManufacturerIdentification(),
                smartMeteringDeviceDto.getMbusManufacturerIdentification());
        assertEquals(smartMeteringDevice.getMbusVersion(), smartMeteringDeviceDto.getMbusVersion());
        assertEquals(smartMeteringDevice.getMbusDeviceTypeIdentification(),
                smartMeteringDeviceDto.getMbusDeviceTypeIdentification());
        assertArrayEquals(smartMeteringDevice.getMbusDefaultKey(), smartMeteringDeviceDto.getMbusDefaultKey());
    }

}
