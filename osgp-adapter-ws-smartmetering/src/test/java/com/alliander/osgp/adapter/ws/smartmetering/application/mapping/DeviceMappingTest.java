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

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.Device;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SmartMeteringDevice;

public class DeviceMappingTest {

    private InstallationMapper installationMapper = new InstallationMapper();
    private static final String DEVICE_IDENTIFICATION = "nr1";
    private static final String DEVICE_TYPE = "typeA";
    private static final String COMMUNICATION_METHOD = "skype";
    private static final String COMMUNICATION_PROVIDER = "internet";
    private static final String ICC_ID = "iccId1";
    private static final String DSMR_VERSION = "dsmrVersion1";
    private static final String SUPPLIER = "supplier1";
    private static final boolean IS_ACTIVE = true;
    private static final String KEY = "key";
    private Date deliveryDateSmartMeteringDevice = new Date();
    private XMLGregorianCalendar deliveryDateDevice;

    /**
     * Needed to initialize a XMLGregorianCalendar instance
     */
    @Before
    public void init() {
        try {
            this.deliveryDateDevice = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
        } catch (final DatatypeConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test to see if a Device object can be mapped to a SmartMeteringDevice
     * object
     */
    @Test
    public void testDeviceMapping() {

        // build test data
        final Device device = this.createDevice();

        // actual mapping
        final SmartMeteringDevice smartMeteringDevice = this.installationMapper.map(device, SmartMeteringDevice.class);

        // check mapping
        this.checkDeviceToSmartMeteringDeviceMapping(smartMeteringDevice);

    }

    /**
     * Test to see if a SmartMeteringDevice can be mapped to a Device
     */
    @Test
    public void testSmartMeteringDeviceMapping() {
        // build test data
        final SmartMeteringDevice smartMeteringDevice = this.createSmartMeteringDevice();

        // actual mapping
        final Device device = this.installationMapper.map(smartMeteringDevice, Device.class);

        // check mapping
        this.checkSmartMeteringDeviceToDeviceMapping(device);

    }

    /**
     * Method to create an instance of Device.
     */
    private Device createDevice() {

        final Device device = new Device();
        device.setDeviceIdentification(DEVICE_IDENTIFICATION);
        device.setDeviceType(DEVICE_TYPE);
        device.setCommunicationMethod(COMMUNICATION_METHOD);
        device.setCommunicationProvider(COMMUNICATION_PROVIDER);
        device.setICCId(ICC_ID);
        device.setDSMRVersion(DSMR_VERSION);
        device.setSupplier(SUPPLIER);
        device.setHLS3Active(IS_ACTIVE);
        device.setHLS4Active(IS_ACTIVE);
        device.setHLS5Active(IS_ACTIVE);
        device.setMasterKey(KEY);
        device.setGlobalEncryptionUnicastKey(KEY);
        device.setAuthenticationKey(KEY);
        device.setDeliveryDate(this.deliveryDateDevice);
        return device;
    }

    /**
     * Method to create an instance of SmartMeteringDevice.
     */
    private SmartMeteringDevice createSmartMeteringDevice() {

        final SmartMeteringDevice smartMeteringDevice = new SmartMeteringDevice();
        smartMeteringDevice.setDeviceIdentification(DEVICE_IDENTIFICATION);
        smartMeteringDevice.setDeviceType(DEVICE_TYPE);
        smartMeteringDevice.setCommunicationMethod(COMMUNICATION_METHOD);
        smartMeteringDevice.setCommunicationProvider(COMMUNICATION_PROVIDER);
        smartMeteringDevice.setICCId(ICC_ID);
        smartMeteringDevice.setDSMRVersion(DSMR_VERSION);
        smartMeteringDevice.setSupplier(SUPPLIER);
        smartMeteringDevice.setHLS3Active(IS_ACTIVE);
        smartMeteringDevice.setHLS4Active(IS_ACTIVE);
        smartMeteringDevice.setHLS5Active(IS_ACTIVE);
        smartMeteringDevice.setMasterKey(KEY);
        smartMeteringDevice.setGlobalEncryptionUnicastKey(KEY);
        smartMeteringDevice.setAuthenticationKey(KEY);
        smartMeteringDevice.setDeliveryDate(this.deliveryDateSmartMeteringDevice);
        return smartMeteringDevice;
    }

    /**
     * Method to check the mapping of a SmartMeteringDevice object to a Device
     * object.
     */
    private void checkSmartMeteringDeviceToDeviceMapping(final Device device) {

        assertNotNull(device);
        assertEquals(DEVICE_IDENTIFICATION, device.getDeviceIdentification());
        assertEquals(DEVICE_TYPE, device.getDeviceType());
        assertEquals(COMMUNICATION_METHOD, device.getCommunicationMethod());
        assertEquals(COMMUNICATION_PROVIDER, device.getCommunicationProvider());
        assertEquals(ICC_ID, device.getICCId());
        assertEquals(DSMR_VERSION, device.getDSMRVersion());
        assertEquals(SUPPLIER, device.getSupplier());
        assertEquals(IS_ACTIVE, device.isHLS3Active());
        assertEquals(IS_ACTIVE, device.isHLS4Active());
        assertEquals(IS_ACTIVE, device.isHLS5Active());
        assertEquals(KEY, device.getMasterKey());
        assertEquals(KEY, device.getGlobalEncryptionUnicastKey());
        assertEquals(KEY, device.getAuthenticationKey());

        // convert a Date object to a joda DateTime object, because the
        // getYear/getMonth and getDay methods in Date are deprecated and give
        // wrong results
        final DateTime dateTime = this.createDateTime(this.deliveryDateSmartMeteringDevice);
        assertEquals(dateTime.getYear(), device.getDeliveryDate().getYear());
        assertEquals(dateTime.getMonthOfYear(), device.getDeliveryDate().getMonth());
        assertEquals(dateTime.getDayOfMonth(), device.getDeliveryDate().getDay());
    }

    /**
     * Method to check the mapping of a Device object to a SmartMeteringDevice
     * object.
     */
    private void checkDeviceToSmartMeteringDeviceMapping(final SmartMeteringDevice smartMeteringDevice) {

        assertNotNull(smartMeteringDevice);
        assertEquals(DEVICE_IDENTIFICATION, smartMeteringDevice.getDeviceIdentification());
        assertEquals(DEVICE_TYPE, smartMeteringDevice.getDeviceType());
        assertEquals(COMMUNICATION_METHOD, smartMeteringDevice.getCommunicationMethod());
        assertEquals(COMMUNICATION_PROVIDER, smartMeteringDevice.getCommunicationProvider());
        assertEquals(ICC_ID, smartMeteringDevice.getICCId());
        assertEquals(DSMR_VERSION, smartMeteringDevice.getDSMRVersion());
        assertEquals(SUPPLIER, smartMeteringDevice.getSupplier());
        assertEquals(IS_ACTIVE, smartMeteringDevice.isHLS3Active());
        assertEquals(IS_ACTIVE, smartMeteringDevice.isHLS4Active());
        assertEquals(IS_ACTIVE, smartMeteringDevice.isHLS5Active());
        assertEquals(KEY, smartMeteringDevice.getMasterKey());
        assertEquals(KEY, smartMeteringDevice.getGlobalEncryptionUnicastKey());
        assertEquals(KEY, smartMeteringDevice.getAuthenticationKey());

        // convert a Date object to a joda DateTime object, because the
        // getYear/getMonth and getDay methods in Date are deprecated and give
        // wrong results
        final DateTime dateTime = this.createDateTime(this.deliveryDateSmartMeteringDevice);
        assertEquals(this.deliveryDateDevice.getYear(), dateTime.getYear());
        assertEquals(this.deliveryDateDevice.getMonth(), dateTime.getMonthOfYear());
        assertEquals(this.deliveryDateDevice.getDay(), dateTime.getDayOfMonth());
    }

    /**
     * Method to convert a Date to a DateTime object so it can be used for
     * assertEquals statements.
     */
    private DateTime createDateTime(final Date date) {

        final DateTime dateTime = new DateTime(date);
        return dateTime;
    }

}
