/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping;

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
    public void testSmartMeteringDeviceToSmartMeterMapping() {

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
        smartMeteringDevice.setMbusVersion((short) 66);
        smartMeteringDevice.setMbusDeviceTypeIdentification((short) 3);
        smartMeteringDevice.setMbusDefaultKey("mbusDefaultKey".getBytes());

        final SmartMeter device = this.mapperFactory.getMapperFacade().map(smartMeteringDevice, SmartMeter.class);

        assertNotNull(device);

        assertEquals(smartMeteringDevice.getDeviceIdentification(), device.getDeviceIdentification());
        assertEquals(smartMeteringDevice.getSupplier(), device.getSupplier());
        assertEquals(smartMeteringDevice.getDeviceType(), device.getDeviceType());
        assertEquals(smartMeteringDevice.getMbusIdentificationNumber(), device.getMbusIdentificationNumber());
        assertEquals(smartMeteringDevice.getMbusManufacturerIdentification(),
                device.getMbusManufacturerIdentification());
        assertEquals(smartMeteringDevice.getMbusVersion(), device.getMbusVersion());
        assertEquals(smartMeteringDevice.getMbusDeviceTypeIdentification(), device.getMbusDeviceTypeIdentification());
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
