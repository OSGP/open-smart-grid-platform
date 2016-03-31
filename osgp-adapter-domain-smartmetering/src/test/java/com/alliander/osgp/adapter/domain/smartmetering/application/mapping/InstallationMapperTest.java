/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import org.junit.Test;

import com.alliander.osgp.domain.core.entities.SmartMeter;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SmartMeteringDevice;
import com.alliander.osgp.dto.valueobjects.smartmetering.SmartMeteringDeviceDto;

public class InstallationMapperTest {

    private MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    @Test
    public void testSmartMeteringDeviceToSmartMeterMapping() {

        final SmartMeteringDevice smartMeteringDevice = new SmartMeteringDevice();
        smartMeteringDevice.setDeviceIdentification("device1");
        smartMeteringDevice.setDeviceType("typeA");
        smartMeteringDevice.setCommunicationMethod("skype");
        smartMeteringDevice.setCommunicationProvider("theInternet");
        smartMeteringDevice.setICCId("value");
        smartMeteringDevice.setDSMRVersion("latestVersion");
        smartMeteringDevice.setMasterKey("masterKey");
        smartMeteringDevice.setGlobalEncryptionUnicastKey("globalEncryptionUnicastKey");
        smartMeteringDevice.setAuthenticationKey("authenticationKey");
        smartMeteringDevice.setSupplier("supplier");
        smartMeteringDevice.setHLS3Active(true);
        smartMeteringDevice.setHLS4Active(true);
        smartMeteringDevice.setHLS5Active(true);
        smartMeteringDevice.setDeliveryDate(new Date());
        final SmartMeter device = this.mapperFactory.getMapperFacade().map(smartMeteringDevice, SmartMeter.class);

        assertNotNull(device);

        // We only test if mapping of deviceIdentification succeeded, because it
        // is on the only required field
        assertEquals(smartMeteringDevice.getDeviceIdentification(), device.getDeviceIdentification());

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
        smartMeteringDevice.setMasterKey("masterKey");
        smartMeteringDevice.setGlobalEncryptionUnicastKey("globalEncryptionUnicastKey");
        smartMeteringDevice.setAuthenticationKey("authenticationKey");
        smartMeteringDevice.setSupplier("supplier");
        smartMeteringDevice.setHLS3Active(true);
        smartMeteringDevice.setHLS4Active(true);
        smartMeteringDevice.setHLS5Active(true);
        smartMeteringDevice.setDeliveryDate(new Date());
        final SmartMeteringDeviceDto smartMeteringDeviceDto = this.mapperFactory.getMapperFacade().map(
                smartMeteringDevice, SmartMeteringDeviceDto.class);

        assertNotNull(smartMeteringDevice);
        assertNotNull(smartMeteringDeviceDto);
        assertEquals(smartMeteringDevice.getDeviceIdentification(), smartMeteringDeviceDto.getDeviceIdentification());
        assertEquals(smartMeteringDevice.getDeviceType(), smartMeteringDeviceDto.getDeviceType());
        assertEquals(smartMeteringDevice.getCommunicationMethod(), smartMeteringDeviceDto.getCommunicationMethod());
        assertEquals(smartMeteringDevice.getCommunicationProvider(), smartMeteringDeviceDto.getCommunicationProvider());
        assertEquals(smartMeteringDevice.getICCId(), smartMeteringDeviceDto.getICCId());
        assertEquals(smartMeteringDevice.getDSMRVersion(), smartMeteringDeviceDto.getDSMRVersion());
        assertEquals(smartMeteringDevice.getMasterKey(), smartMeteringDeviceDto.getMasterKey());
        assertEquals(smartMeteringDevice.getGlobalEncryptionUnicastKey(),
                smartMeteringDeviceDto.getGlobalEncryptionUnicastKey());
        assertEquals(smartMeteringDevice.getAuthenticationKey(), smartMeteringDeviceDto.getAuthenticationKey());
        assertEquals(smartMeteringDevice.getSupplier(), smartMeteringDeviceDto.getSupplier());
        assertEquals(smartMeteringDevice.isHLS3Active(), smartMeteringDeviceDto.isHLS3Active());
        assertEquals(smartMeteringDevice.isHLS4Active(), smartMeteringDeviceDto.isHLS4Active());
        assertEquals(smartMeteringDevice.isHLS5Active(), smartMeteringDeviceDto.isHLS5Active());
        assertEquals(smartMeteringDevice.getDeliveryDate(), smartMeteringDeviceDto.getDeliveryDate());
    }

}
