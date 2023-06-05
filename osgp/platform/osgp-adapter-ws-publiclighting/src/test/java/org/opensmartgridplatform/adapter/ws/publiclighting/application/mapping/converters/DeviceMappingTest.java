// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.publiclighting.application.mapping.converters;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.ws.publiclighting.application.mapping.AdHocManagementMapper;
import org.opensmartgridplatform.domain.core.entities.LightMeasurementDevice;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.valueobjects.Address;
import org.opensmartgridplatform.domain.core.valueobjects.GpsCoordinates;

class DeviceMappingTest {

  private static final String DEVICE_IDENTIFICATION = "TestDevice-1";
  private static final String DEVICE_TYPE = "SSLD";
  private static final String CONTAINER_CITY = "some-city";
  private static final Integer CONTAINER_NUMBER = 42;
  private static final String CONTAINER_POSTAL_CODE = "1234AB";
  private static final String CONTAINER_STREET = "some-street";
  private static final Float GPS_LATITUDE = 51.0f;
  private static final Float GPS_LONGITUDE = 5.0f;
  private static final String LMD_DESCRIPTION = "LmdDesciption";
  private static final String LMD_CODE = "LmdCode";
  private static final String LMD_COLOR = "LmdColor";
  private static final Short LMD_DIGITAL_INPUT = 1;
  private static final AdHocManagementMapper mapper = new AdHocManagementMapper();

  @Test
  void testConvertSsldToCore() {
    final Ssld ssld = mapper.map(this.adhocManagementSsld(), Ssld.class);

    assertThat(ssld)
        .usingRecursiveComparison()
        .ignoringFields("creationTime", "modificationTime")
        .isEqualTo(this.coreSsld());
  }

  @Test
  void testConvertLmdToCore() {
    final LightMeasurementDevice lmd =
        mapper.map(this.adhocManagementLmd(), LightMeasurementDevice.class);

    assertThat(lmd)
        .usingRecursiveComparison()
        .ignoringFields("creationTime", "modificationTime")
        .isEqualTo(this.coreLmd());
  }

  @Test
  void testConvertSsldToAdHocMananagement() {
    final org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.Ssld device =
        mapper.map(
            this.coreSsld(),
            org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.Ssld.class);

    assertThat(device)
        .usingRecursiveComparison()
        .ignoringFields("deviceUid")
        .isEqualTo(this.adhocManagementSsld());
  }

  @Test
  void testConvertLmdToAdHocMananagement() {
    final org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement
            .LightMeasurementDevice
        lmd =
            mapper.map(
                this.coreLmd(),
                org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement
                    .LightMeasurementDevice.class);

    assertThat(lmd)
        .usingRecursiveComparison()
        .ignoringFields("deviceUid")
        .isEqualTo(this.adhocManagementLmd());
  }

  private org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.Ssld
      adhocManagementSsld() {
    final org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.Ssld ssld =
        new org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.Ssld();
    ssld.setDeviceIdentification(DEVICE_IDENTIFICATION);
    ssld.setContainerPostalCode(CONTAINER_POSTAL_CODE);
    ssld.setContainerCity(CONTAINER_CITY);
    ssld.setContainerStreet(CONTAINER_STREET);
    ssld.setContainerNumber(CONTAINER_NUMBER.toString());
    ssld.setGpsLatitude(GPS_LATITUDE);
    ssld.setGpsLongitude(GPS_LONGITUDE);
    ssld.setDeviceType(DEVICE_TYPE);
    ssld.setActivated(true);
    ssld.setHasSchedule(false);
    ssld.setPublicKeyPresent(false);
    return ssld;
  }

  private org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement
          .LightMeasurementDevice
      adhocManagementLmd() {
    final org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement
            .LightMeasurementDevice
        lmd =
            new org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement
                .LightMeasurementDevice();
    lmd.setDeviceIdentification(DEVICE_IDENTIFICATION);
    lmd.setContainerPostalCode(CONTAINER_POSTAL_CODE);
    lmd.setContainerCity(CONTAINER_CITY);
    lmd.setContainerStreet(CONTAINER_STREET);
    lmd.setContainerNumber(CONTAINER_NUMBER.toString());
    lmd.setGpsLatitude(GPS_LATITUDE);
    lmd.setGpsLongitude(GPS_LONGITUDE);
    lmd.setDeviceType(DEVICE_TYPE);
    lmd.setActivated(true);
    lmd.setDescription(LMD_DESCRIPTION);
    lmd.setCode(LMD_CODE);
    lmd.setColor(LMD_COLOR);
    lmd.setDigitalInput(LMD_DIGITAL_INPUT);
    return lmd;
  }

  private Ssld coreSsld() {
    final Ssld ssld =
        new Ssld(DEVICE_IDENTIFICATION, null, this.address(), this.gpsCoordinates(), null);
    ssld.updateRegistrationData(null, DEVICE_TYPE);
    ssld.setHasSchedule(false);
    ssld.setPublicKeyPresent(false);
    return ssld;
  }

  private LightMeasurementDevice coreLmd() {
    final LightMeasurementDevice lmd =
        new LightMeasurementDevice(
            DEVICE_IDENTIFICATION, null, this.address(), this.gpsCoordinates(), null);
    lmd.updateRegistrationData(null, DEVICE_TYPE);
    lmd.setDescription(LMD_DESCRIPTION);
    lmd.setCode(LMD_CODE);
    lmd.setColor(LMD_COLOR);
    lmd.setDigitalInput(LMD_DIGITAL_INPUT);
    return lmd;
  }

  private GpsCoordinates gpsCoordinates() {
    return new GpsCoordinates(GPS_LATITUDE, GPS_LONGITUDE);
  }

  private Address address() {
    return new Address(
        CONTAINER_CITY, CONTAINER_POSTAL_CODE, CONTAINER_STREET, CONTAINER_NUMBER, null, null);
  }
}
