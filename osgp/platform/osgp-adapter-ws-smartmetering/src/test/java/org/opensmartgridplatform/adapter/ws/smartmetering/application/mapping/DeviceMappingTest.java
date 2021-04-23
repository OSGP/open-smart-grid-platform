/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.Device;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SmartMeteringDevice;

public class DeviceMappingTest {

  private static final String DEVICE_IDENTIFICATION = "nr1";
  private static final String DEVICE_TYPE = "typeA";
  private static final String COMMUNICATION_METHOD = "skype";
  private static final String COMMUNICATION_PROVIDER = "internet";
  private static final String ICC_ID = "iccId1";
  private static final String PROTOCOL_NAME = "test-protocol-name";
  private static final String PROTOCOL_VERSION = "test-protocol-version";
  private static final String SUPPLIER = "supplier1";
  private static final boolean IS_ACTIVE = true;
  private static final byte[] KEY = "key".getBytes();
  private final InstallationMapper installationMapper = new InstallationMapper();
  private final Date deliveryDateSmartMeteringDevice = new Date();
  private XMLGregorianCalendar deliveryDateDevice;

  /** Method to check the mapping of a Device object to a SmartMeteringDevice object. */
  private void checkDeviceToSmartMeteringDeviceMapping(
      final SmartMeteringDevice smartMeteringDevice) {

    assertThat(smartMeteringDevice).isNotNull();
    assertThat(smartMeteringDevice.getDeviceIdentification()).isEqualTo(DEVICE_IDENTIFICATION);
    assertThat(smartMeteringDevice.getDeviceType()).isEqualTo(DEVICE_TYPE);
    assertThat(smartMeteringDevice.getCommunicationMethod()).isEqualTo(COMMUNICATION_METHOD);
    assertThat(smartMeteringDevice.getCommunicationProvider()).isEqualTo(COMMUNICATION_PROVIDER);
    assertThat(smartMeteringDevice.getIccId()).isEqualTo(ICC_ID);
    assertThat(smartMeteringDevice.getProtocolName()).isEqualTo(PROTOCOL_NAME);
    assertThat(smartMeteringDevice.getProtocolVersion()).isEqualTo(PROTOCOL_VERSION);
    assertThat(smartMeteringDevice.getSupplier()).isEqualTo(SUPPLIER);
    assertThat(smartMeteringDevice.isHls3Active()).isEqualTo(IS_ACTIVE);
    assertThat(smartMeteringDevice.isHls4Active()).isEqualTo(IS_ACTIVE);
    assertThat(smartMeteringDevice.isHls5Active()).isEqualTo(IS_ACTIVE);
    assertThat(smartMeteringDevice.getMasterKey()).isEqualTo(KEY);
    assertThat(smartMeteringDevice.getGlobalEncryptionUnicastKey()).isEqualTo(KEY);
    assertThat(smartMeteringDevice.getAuthenticationKey()).isEqualTo(KEY);

    // convert a Date object to a joda DateTime object, because the
    // getYear/getMonth and getDay methods in Date are deprecated and give
    // wrong results
    final DateTime dateTime = this.createDateTime(this.deliveryDateSmartMeteringDevice);
    assertThat(dateTime.getYear()).isEqualTo(this.deliveryDateDevice.getYear());
    assertThat(dateTime.getMonthOfYear()).isEqualTo(this.deliveryDateDevice.getMonth());
    assertThat(dateTime.getDayOfMonth()).isEqualTo(this.deliveryDateDevice.getDay());
  }

  /** Method to check the mapping of a SmartMeteringDevice object to a Device object. */
  private void checkSmartMeteringDeviceToDeviceMapping(final Device device) {

    assertThat(device).isNotNull();
    assertThat(device.getDeviceIdentification()).isEqualTo(DEVICE_IDENTIFICATION);
    assertThat(device.getDeviceType()).isEqualTo(DEVICE_TYPE);
    assertThat(device.getCommunicationMethod()).isEqualTo(COMMUNICATION_METHOD);
    assertThat(device.getCommunicationProvider()).isEqualTo(COMMUNICATION_PROVIDER);
    assertThat(device.getIccId()).isEqualTo(ICC_ID);
    assertThat(device.getProtocolName()).isEqualTo(PROTOCOL_NAME);
    assertThat(device.getProtocolVersion()).isEqualTo(PROTOCOL_VERSION);
    assertThat(device.getSupplier()).isEqualTo(SUPPLIER);
    assertThat(device.isHls3Active()).isEqualTo(IS_ACTIVE);
    assertThat(device.isHls4Active()).isEqualTo(IS_ACTIVE);
    assertThat(device.isHls5Active()).isEqualTo(IS_ACTIVE);
    assertThat(device.getMasterKey()).isEqualTo(KEY);
    assertThat(device.getGlobalEncryptionUnicastKey()).isEqualTo(KEY);
    assertThat(device.getAuthenticationKey()).isEqualTo(KEY);

    // convert a Date object to a joda DateTime object, because the
    // getYear/getMonth and getDay methods in Date are deprecated and give
    // wrong results
    final DateTime dateTime = this.createDateTime(this.deliveryDateSmartMeteringDevice);
    assertThat(device.getDeliveryDate().getYear()).isEqualTo(dateTime.getYear());
    assertThat(device.getDeliveryDate().getMonth()).isEqualTo(dateTime.getMonthOfYear());
    assertThat(device.getDeliveryDate().getDay()).isEqualTo(dateTime.getDayOfMonth());
  }

  /**
   * Method to convert a Date to a DateTime object so it can be used for assertEquals statements.
   */
  private DateTime createDateTime(final Date date) {
    return new DateTime(date);
  }

  /** Method to create an instance of Device. */
  private Device createDevice() {

    final Device device = new Device();
    device.setDeviceIdentification(DEVICE_IDENTIFICATION);
    device.setDeviceType(DEVICE_TYPE);
    device.setCommunicationMethod(COMMUNICATION_METHOD);
    device.setCommunicationProvider(COMMUNICATION_PROVIDER);
    device.setIccId(ICC_ID);
    device.setProtocolName(PROTOCOL_NAME);
    device.setProtocolVersion(PROTOCOL_VERSION);
    device.setSupplier(SUPPLIER);
    device.setHls3Active(IS_ACTIVE);
    device.setHls4Active(IS_ACTIVE);
    device.setHls5Active(IS_ACTIVE);
    device.setMasterKey(KEY);
    device.setGlobalEncryptionUnicastKey(KEY);
    device.setAuthenticationKey(KEY);
    device.setDeliveryDate(this.deliveryDateDevice);
    return device;
  }

  /** Method to create an instance of SmartMeteringDevice. */
  private SmartMeteringDevice createSmartMeteringDevice() {

    final SmartMeteringDevice smartMeteringDevice = new SmartMeteringDevice();
    smartMeteringDevice.setDeviceIdentification(DEVICE_IDENTIFICATION);
    smartMeteringDevice.setDeviceType(DEVICE_TYPE);
    smartMeteringDevice.setCommunicationMethod(COMMUNICATION_METHOD);
    smartMeteringDevice.setCommunicationProvider(COMMUNICATION_PROVIDER);
    smartMeteringDevice.setIccId(ICC_ID);
    smartMeteringDevice.setProtocolName(PROTOCOL_NAME);
    smartMeteringDevice.setProtocolVersion(PROTOCOL_VERSION);
    smartMeteringDevice.setSupplier(SUPPLIER);
    smartMeteringDevice.setHls3Active(IS_ACTIVE);
    smartMeteringDevice.setHls4Active(IS_ACTIVE);
    smartMeteringDevice.setHls5Active(IS_ACTIVE);
    smartMeteringDevice.setMasterKey(KEY);
    smartMeteringDevice.setGlobalEncryptionUnicastKey(KEY);
    smartMeteringDevice.setAuthenticationKey(KEY);
    smartMeteringDevice.setDeliveryDate(this.deliveryDateSmartMeteringDevice);
    return smartMeteringDevice;
  }

  /** Needed to initialize a XMLGregorianCalendar instance */
  @BeforeEach
  public void init() {
    try {
      this.deliveryDateDevice =
          DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
    } catch (final DatatypeConfigurationException e) {
      e.printStackTrace();
    }
  }

  /** Test to see if a Device object can be mapped to a SmartMeteringDevice object */
  @Test
  public void testDeviceMapping() {

    // build test data
    final Device device = this.createDevice();

    // actual mapping
    final SmartMeteringDevice smartMeteringDevice =
        this.installationMapper.map(device, SmartMeteringDevice.class);

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
}
