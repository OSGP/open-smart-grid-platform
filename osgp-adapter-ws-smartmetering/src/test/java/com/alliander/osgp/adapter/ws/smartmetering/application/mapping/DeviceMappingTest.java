/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.Device;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SmartMeteringDevice;

public class DeviceMappingTest {

    private MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
    private XMLGregorianCalendar deliveryDate;

    @Before
    public void init() {
        final GregorianCalendar gregorianCalendar = new GregorianCalendar(2016, GregorianCalendar.APRIL, 7);
        try {
            this.deliveryDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
        } catch (final DatatypeConfigurationException e) {
            e.printStackTrace();
        }
    }

    // Test to see if a Device can be mapped to a SmartMeteringDevice.
    @Test
    public void testDeviceMapping() {
        // build test data
        final Device device = new Device();
        device.setDeviceIdentification("nr1");
        device.setDeviceType("typeA");
        device.setCommunicationMethod("skype");
        device.setCommunicationProvider("internet");
        device.setICCId("iccId1");
        device.setDSMRVersion("dsmrVersion1");
        device.setSupplier("supplier1");
        device.setHLS3Active(true);
        device.setHLS4Active(true);
        device.setHLS5Active(true);
        device.setMasterKey("masterKey");
        device.setGlobalEncryptionUnicastKey("uniCastKey");
        device.setAuthenticationKey("authenticationKey");
        device.setDeliveryDate(this.deliveryDate);
        // actual mapping
        final SmartMeteringDevice smartMeteringDevice = this.mapperFactory.getMapperFacade().map(device,
                SmartMeteringDevice.class);
        // check mapping
        assertNotNull(smartMeteringDevice);
        assertEquals(device.getDeviceIdentification(), smartMeteringDevice.getDeviceIdentification());
        assertEquals(device.getDeviceType(), smartMeteringDevice.getDeviceType());
        assertEquals(device.getCommunicationMethod(), smartMeteringDevice.getCommunicationMethod());
        assertEquals(device.getCommunicationProvider(), smartMeteringDevice.getCommunicationProvider());
        assertEquals(device.getICCId(), smartMeteringDevice.getICCId());
        assertEquals(device.getDSMRVersion(), smartMeteringDevice.getDSMRVersion());
        assertEquals(device.getSupplier(), smartMeteringDevice.getSupplier());
        assertEquals(device.isHLS3Active(), smartMeteringDevice.isHLS3Active());
        assertEquals(device.isHLS4Active(), smartMeteringDevice.isHLS4Active());
        assertEquals(device.isHLS5Active(), smartMeteringDevice.isHLS5Active());
        assertEquals(device.getMasterKey(), smartMeteringDevice.getMasterKey());
        assertEquals(device.getGlobalEncryptionUnicastKey(), smartMeteringDevice.getGlobalEncryptionUnicastKey());
        assertEquals(device.getAuthenticationKey(), smartMeteringDevice.getAuthenticationKey());
        // convert a Date object to a joda DateTime object, because the
        // getYear/getMonth and getDay methods in Date are deprecated and give
        // wrong results
        final DateTime dateTime = new DateTime(smartMeteringDevice.getDeliveryDate().getTime());
        assertEquals(device.getDeliveryDate().getYear(), dateTime.getYear());
        assertEquals(device.getDeliveryDate().getMonth(), dateTime.getMonthOfYear());
        assertEquals(device.getDeliveryDate().getDay(), dateTime.getDayOfMonth());
    }

    // Test to see if a SmartMeteringDevice can be mapped to a Device
    @Test
    public void testSmartMeteringDeviceMapping() {
        // build test data
        final SmartMeteringDevice smartMeteringDevice = new SmartMeteringDevice();
        smartMeteringDevice.setDeviceIdentification("nr1");
        smartMeteringDevice.setDeviceType("typeA");
        smartMeteringDevice.setCommunicationMethod("skype");
        smartMeteringDevice.setCommunicationProvider("internet");
        smartMeteringDevice.setICCId("iccId1");
        smartMeteringDevice.setDSMRVersion("dsmrVersion1");
        smartMeteringDevice.setSupplier("supplier1");
        smartMeteringDevice.setHLS3Active(true);
        smartMeteringDevice.setHLS4Active(true);
        smartMeteringDevice.setHLS5Active(true);
        smartMeteringDevice.setMasterKey("masterKey");
        smartMeteringDevice.setGlobalEncryptionUnicastKey("uniCastKey");
        smartMeteringDevice.setAuthenticationKey("authenticationKey");
        smartMeteringDevice.setDeliveryDate(new Date());
        // actual mapping
        final Device device = this.mapperFactory.getMapperFacade().map(smartMeteringDevice, Device.class);
        // check mapping
        assertNotNull(device);
        assertEquals(smartMeteringDevice.getDeviceIdentification(), device.getDeviceIdentification());
        assertEquals(smartMeteringDevice.getDeviceType(), device.getDeviceType());
        assertEquals(smartMeteringDevice.getCommunicationMethod(), device.getCommunicationMethod());
        assertEquals(smartMeteringDevice.getCommunicationProvider(), device.getCommunicationProvider());
        assertEquals(smartMeteringDevice.getICCId(), device.getICCId());
        assertEquals(smartMeteringDevice.getDSMRVersion(), device.getDSMRVersion());
        assertEquals(smartMeteringDevice.getSupplier(), device.getSupplier());
        assertEquals(smartMeteringDevice.isHLS3Active(), device.isHLS3Active());
        assertEquals(smartMeteringDevice.isHLS4Active(), device.isHLS4Active());
        assertEquals(smartMeteringDevice.isHLS5Active(), device.isHLS5Active());
        assertEquals(smartMeteringDevice.getMasterKey(), device.getMasterKey());
        assertEquals(smartMeteringDevice.getGlobalEncryptionUnicastKey(), device.getGlobalEncryptionUnicastKey());
        assertEquals(smartMeteringDevice.getAuthenticationKey(), device.getAuthenticationKey());
        // convert a Date object to a joda DateTime object, because the
        // getYear/getMonth and getDay methods in Date are deprecated and give
        // wrong results
        final DateTime dateTime = new DateTime(smartMeteringDevice.getDeliveryDate().getTime());
        assertEquals(dateTime.getYear(), device.getDeliveryDate().getYear());
        assertEquals(dateTime.getMonthOfYear(), device.getDeliveryDate().getMonth());
        assertEquals(dateTime.getDayOfMonth(), device.getDeliveryDate().getDay());
    }
}
