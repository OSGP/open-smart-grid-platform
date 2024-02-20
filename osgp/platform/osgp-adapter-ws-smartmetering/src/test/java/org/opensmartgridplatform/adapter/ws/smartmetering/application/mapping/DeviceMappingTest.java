// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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

class DeviceMappingTest {

  private static final String DEVICE_IDENTIFICATION = "nr1";
  private static final String DEVICE_TYPE = "typeA";
  private static final String COMMUNICATION_METHOD = "skype";
  private static final String COMMUNICATION_PROVIDER = "internet";
  private static final String ICC_ID = "iccId1";
  private static final String PROTOCOL_NAME = "test-protocol-name";
  private static final String PROTOCOL_VERSION = "test-protocol-version";
  private static final String PROTOCOL_VARIANT = "test-protocol-variant";
  private static final String SUPPLIER = "supplier1";
  private static final Long PORT = 3000L;
  private static final int CHALLENGE_LENGTH = 15;
  private static final int WITH_LIST_MAX = 32;
  private static final boolean IS_ACTIVE = true;
  private static final boolean LLS1_ACTIVE = true;

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
    assertThat(smartMeteringDevice.getProtocolVariant()).isEqualTo(PROTOCOL_VARIANT);
    assertThat(smartMeteringDevice.getSupplier()).isEqualTo(SUPPLIER);
    assertThat(smartMeteringDevice.isHls3Active()).isEqualTo(IS_ACTIVE);
    assertThat(smartMeteringDevice.isHls4Active()).isEqualTo(IS_ACTIVE);
    assertThat(smartMeteringDevice.isHls5Active()).isEqualTo(IS_ACTIVE);
    assertThat(smartMeteringDevice.getMasterKey()).isEqualTo(KEY);
    assertThat(smartMeteringDevice.getGlobalEncryptionUnicastKey()).isEqualTo(KEY);
    assertThat(smartMeteringDevice.getAuthenticationKey()).isEqualTo(KEY);
    assertThat(smartMeteringDevice.getPort()).isEqualTo(PORT);
    assertThat(smartMeteringDevice.isPolyphase()).isEqualTo(IS_ACTIVE);
    assertThat(smartMeteringDevice.isIpAddressIsStatic()).isEqualTo(IS_ACTIVE);
    assertThat(smartMeteringDevice.isSelectiveAccessSupported()).isEqualTo(IS_ACTIVE);
    assertThat(smartMeteringDevice.getWithListMax()).isEqualTo(WITH_LIST_MAX);
    assertThat(smartMeteringDevice.getChallengeLength()).isEqualTo(CHALLENGE_LENGTH);

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
    assertThat(device.getProtocolVariant()).isEqualTo(PROTOCOL_VARIANT);
    assertThat(device.getSupplier()).isEqualTo(SUPPLIER);
    assertThat(device.isHls3Active()).isEqualTo(IS_ACTIVE);
    assertThat(device.isHls4Active()).isEqualTo(IS_ACTIVE);
    assertThat(device.isHls5Active()).isEqualTo(IS_ACTIVE);
    assertThat(device.isLls1Active()).isEqualTo(LLS1_ACTIVE);
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
    device.setProtocolVariant(PROTOCOL_VARIANT);
    device.setSupplier(SUPPLIER);
    device.setHls3Active(IS_ACTIVE);
    device.setHls4Active(IS_ACTIVE);
    device.setHls5Active(IS_ACTIVE);
    device.setMasterKey(KEY);
    device.setGlobalEncryptionUnicastKey(KEY);
    device.setAuthenticationKey(KEY);
    device.setDeliveryDate(this.deliveryDateDevice);
    device.setPolyphase(IS_ACTIVE);
    device.setPort(PORT);
    device.setChallengeLength(CHALLENGE_LENGTH);
    device.setIpAddressIsStatic(IS_ACTIVE);
    device.setSelectiveAccessSupported(IS_ACTIVE);
    device.setWithListMax(WITH_LIST_MAX);
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
    smartMeteringDevice.setProtocolVariant(PROTOCOL_VARIANT);
    smartMeteringDevice.setSupplier(SUPPLIER);
    smartMeteringDevice.setLls1Active(LLS1_ACTIVE);
    smartMeteringDevice.setHls3Active(IS_ACTIVE);
    smartMeteringDevice.setHls4Active(IS_ACTIVE);
    smartMeteringDevice.setHls5Active(IS_ACTIVE);
    smartMeteringDevice.setMasterKey(KEY);
    smartMeteringDevice.setGlobalEncryptionUnicastKey(KEY);
    smartMeteringDevice.setAuthenticationKey(KEY);
    smartMeteringDevice.setDeliveryDate(this.deliveryDateSmartMeteringDevice);
    smartMeteringDevice.setPolyphase(IS_ACTIVE);
    smartMeteringDevice.setPort(PORT);
    smartMeteringDevice.setChallengeLength(CHALLENGE_LENGTH);
    smartMeteringDevice.setIpAddressIsStatic(IS_ACTIVE);
    smartMeteringDevice.setWithListMax(WITH_LIST_MAX);
    smartMeteringDevice.setSelectiveAccessSupported(IS_ACTIVE);

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
  void testDeviceMapping() {

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
  void testSmartMeteringDeviceMapping() {
    // build test data
    final SmartMeteringDevice smartMeteringDevice = this.createSmartMeteringDevice();

    // actual mapping
    final Device device = this.installationMapper.map(smartMeteringDevice, Device.class);

    // check mapping
    this.checkSmartMeteringDeviceToDeviceMapping(device);
  }
}
