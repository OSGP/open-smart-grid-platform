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
    private static final String DEVICEIDENTIFICATION = "nr1";
    private static final String DEVICETYPE = "typeA";
    private static final String COMMUNICATIONMETHOD = "skype";
    private static final String COMMUNICATIONPROVIDER = "internet";
    private static final String ICCID = "iccId1";
    private static final String DSMRVERSION = "dsmrVersion1";
    private static final String SUPPLIER = "supplier1";
    private static final boolean ISACTIVE = true;
    private static final String KEY = "key";
    private Date deliveryDateSmartMeteringDevice = new Date();
    private XMLGregorianCalendar deliveryDateDevice;

    /** Needed to initialize a XMLGregorianCalendar instance */
    @Before
    public void init() {
        final GregorianCalendar gregorianCalendar = new GregorianCalendar();
        try {
            this.deliveryDateDevice = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
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

    /** Test to see if a SmartMeteringDevice can be mapped to a Device */
    @Test
    public void testSmartMeteringDeviceMapping() {
        // build test data
        final SmartMeteringDevice smartMeteringDevice = this.createSmartMeteringDevice();

        // actual mapping
        final Device device = this.installationMapper.map(smartMeteringDevice, Device.class);

        // check mapping
        this.checkSmartMeteringDeviceToDeviceMapping(device);

    }

    /** Method to create an instance of Device. */
    private Device createDevice() {

        final Device device = new Device();
        device.setDeviceIdentification(DEVICEIDENTIFICATION);
        device.setDeviceType(DEVICETYPE);
        device.setCommunicationMethod(COMMUNICATIONMETHOD);
        device.setCommunicationProvider(COMMUNICATIONPROVIDER);
        device.setICCId(ICCID);
        device.setDSMRVersion(DSMRVERSION);
        device.setSupplier(SUPPLIER);
        device.setHLS3Active(ISACTIVE);
        device.setHLS4Active(ISACTIVE);
        device.setHLS5Active(ISACTIVE);
        device.setMasterKey(KEY);
        device.setGlobalEncryptionUnicastKey(KEY);
        device.setAuthenticationKey(KEY);
        device.setDeliveryDate(this.deliveryDateDevice);
        return device;
    }

    /** Method to create an instance of SmartMeteringDevice. */
    private SmartMeteringDevice createSmartMeteringDevice() {

        final SmartMeteringDevice smartMeteringDevice = new SmartMeteringDevice();
        smartMeteringDevice.setDeviceIdentification(DEVICEIDENTIFICATION);
        smartMeteringDevice.setDeviceType(DEVICETYPE);
        smartMeteringDevice.setCommunicationMethod(COMMUNICATIONMETHOD);
        smartMeteringDevice.setCommunicationProvider(COMMUNICATIONPROVIDER);
        smartMeteringDevice.setICCId(ICCID);
        smartMeteringDevice.setDSMRVersion(DSMRVERSION);
        smartMeteringDevice.setSupplier(SUPPLIER);
        smartMeteringDevice.setHLS3Active(ISACTIVE);
        smartMeteringDevice.setHLS4Active(ISACTIVE);
        smartMeteringDevice.setHLS5Active(ISACTIVE);
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
        assertEquals(DEVICEIDENTIFICATION, device.getDeviceIdentification());
        assertEquals(DEVICETYPE, device.getDeviceType());
        assertEquals(COMMUNICATIONMETHOD, device.getCommunicationMethod());
        assertEquals(COMMUNICATIONPROVIDER, device.getCommunicationProvider());
        assertEquals(ICCID, device.getICCId());
        assertEquals(DSMRVERSION, device.getDSMRVersion());
        assertEquals(SUPPLIER, device.getSupplier());
        assertEquals(ISACTIVE, device.isHLS3Active());
        assertEquals(ISACTIVE, device.isHLS4Active());
        assertEquals(ISACTIVE, device.isHLS5Active());
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
        assertEquals(DEVICEIDENTIFICATION, smartMeteringDevice.getDeviceIdentification());
        assertEquals(DEVICETYPE, smartMeteringDevice.getDeviceType());
        assertEquals(COMMUNICATIONMETHOD, smartMeteringDevice.getCommunicationMethod());
        assertEquals(COMMUNICATIONPROVIDER, smartMeteringDevice.getCommunicationProvider());
        assertEquals(ICCID, smartMeteringDevice.getICCId());
        assertEquals(DSMRVERSION, smartMeteringDevice.getDSMRVersion());
        assertEquals(SUPPLIER, smartMeteringDevice.getSupplier());
        assertEquals(ISACTIVE, smartMeteringDevice.isHLS3Active());
        assertEquals(ISACTIVE, smartMeteringDevice.isHLS4Active());
        assertEquals(ISACTIVE, smartMeteringDevice.isHLS5Active());
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
