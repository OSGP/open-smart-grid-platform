//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.core.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.repositories.SsldRepository;
import org.opensmartgridplatform.domain.core.valueobjects.Address;
import org.opensmartgridplatform.domain.core.valueobjects.GpsCoordinates;

@ExtendWith(MockitoExtension.class)
public class DeviceConverterTest {

  @Mock private SsldRepository ssldRepository;

  @InjectMocks private DeviceManagementMapper deviceManagementMapper;

  @BeforeEach
  public void initMapper() {
    this.deviceManagementMapper.initialize();
  }

  @Test
  public void testDeviceConversion() throws UnknownHostException {
    final Device device =
        new Device(
            "id",
            "alias",
            new Address("city", "postal", "street", 42, "nr", "munic"),
            new GpsCoordinates(12f, 13f),
            null);
    device.updateRegistrationData(InetAddress.getByName("localhost"), "type");

    final org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Device jaxbDevice =
        this.deviceManagementMapper.map(
            device, org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Device.class);

    assertThat(jaxbDevice.getDeviceIdentification()).isEqualTo("id");
    assertThat(jaxbDevice.getAlias()).isEqualTo("alias");
    assertThat(jaxbDevice.getContainerAddress().getCity()).isEqualTo("city");
    assertThat(jaxbDevice.getContainerAddress().getPostalCode()).isEqualTo("postal");
    assertThat(jaxbDevice.getContainerAddress().getStreet()).isEqualTo("street");
    assertThat(jaxbDevice.getContainerAddress().getNumber()).isEqualTo(new Integer(42));
    assertThat(jaxbDevice.getContainerAddress().getNumberAddition()).isEqualTo("nr");
    assertThat(jaxbDevice.getContainerAddress().getMunicipality()).isEqualTo("munic");
    assertThat(jaxbDevice.getGpsLatitude()).isEqualTo("12.0");
    assertThat(jaxbDevice.getGpsLongitude()).isEqualTo("13.0");
    assertThat(jaxbDevice.getNetworkAddress()).isEqualTo("localhost/127.0.0.1");
    assertThat(jaxbDevice.getDeviceType()).isEqualTo("type");

    final Device mappedBack = this.deviceManagementMapper.map(jaxbDevice, Device.class);

    assertThat(mappedBack.getDeviceIdentification()).isEqualTo("id");
    assertThat(mappedBack.getAlias()).isEqualTo("alias");
    assertThat(mappedBack.getContainerAddress().getCity()).isEqualTo("city");
    assertThat(mappedBack.getContainerAddress().getPostalCode()).isEqualTo("postal");
    assertThat(mappedBack.getContainerAddress().getStreet()).isEqualTo("street");
    assertThat(mappedBack.getContainerAddress().getNumber()).isEqualTo(new Integer(42));
    assertThat(mappedBack.getContainerAddress().getNumberAddition()).isEqualTo("nr");
    assertThat(mappedBack.getContainerAddress().getMunicipality()).isEqualTo("munic");
    assertThat(mappedBack.getGpsCoordinates().getLatitude()).isEqualTo(12);
    assertThat(mappedBack.getGpsCoordinates().getLongitude()).isEqualTo(13);
    // alas networkaddress in jaxb device is just a string, need parsing to
    // convert that to InetAddress
    assertThat(mappedBack.getNetworkAddress()).isEqualTo(null);
    assertThat(mappedBack.getDeviceType()).isEqualTo("type");
  }

  @Test
  public void testSmartMeterConversion() throws UnknownHostException {
    final Device device =
        new SmartMeter(
            "id",
            "alias",
            new Address("city", "postal", "street", 42, "nr", "munic"),
            new GpsCoordinates(12f, 13f));
    device.updateRegistrationData(InetAddress.getByName("localhost"), "type");

    final org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Device jaxbDevice =
        this.deviceManagementMapper.map(
            device, org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Device.class);

    assertThat(jaxbDevice.getDeviceIdentification()).isEqualTo("id");
    assertThat(jaxbDevice.getAlias()).isEqualTo("alias");
    assertThat(jaxbDevice.getContainerAddress().getCity()).isEqualTo("city");
    assertThat(jaxbDevice.getContainerAddress().getPostalCode()).isEqualTo("postal");
    assertThat(jaxbDevice.getContainerAddress().getStreet()).isEqualTo("street");
    assertThat(jaxbDevice.getContainerAddress().getNumber()).isEqualTo(new Integer(42));
    assertThat(jaxbDevice.getContainerAddress().getNumberAddition()).isEqualTo("nr");
    assertThat(jaxbDevice.getContainerAddress().getMunicipality()).isEqualTo("munic");
    assertThat(jaxbDevice.getGpsLatitude()).isEqualTo("12.0");
    assertThat(jaxbDevice.getGpsLongitude()).isEqualTo("13.0");
    assertThat(jaxbDevice.getNetworkAddress()).isEqualTo("localhost/127.0.0.1");
    assertThat(jaxbDevice.getDeviceType()).isEqualTo("type");

    final SmartMeter mappedBack = this.deviceManagementMapper.map(jaxbDevice, SmartMeter.class);

    assertThat(mappedBack.getDeviceIdentification()).isEqualTo("id");
    assertThat(mappedBack.getAlias()).isEqualTo("alias");
    assertThat(mappedBack.getContainerAddress().getCity()).isEqualTo("city");
    assertThat(mappedBack.getContainerAddress().getPostalCode()).isEqualTo("postal");
    assertThat(mappedBack.getContainerAddress().getStreet()).isEqualTo("street");
    assertThat(mappedBack.getContainerAddress().getNumber()).isEqualTo(new Integer(42));
    assertThat(mappedBack.getContainerAddress().getNumberAddition()).isEqualTo("nr");
    assertThat(mappedBack.getContainerAddress().getMunicipality()).isEqualTo("munic");
    assertThat(mappedBack.getGpsCoordinates().getLatitude()).isEqualTo(12);
    assertThat(mappedBack.getGpsCoordinates().getLongitude()).isEqualTo(13);
    // alas networkaddress in jaxb device is just a string, need parsing to
    // convert that to InetAddress
    assertThat(mappedBack.getNetworkAddress()).isEqualTo(null);
    assertThat(mappedBack.getDeviceType()).isEqualTo("type");
  }

  @Test
  public void testSsldConversion() throws Exception {
    final Instant instant = ZonedDateTime.of(2020, 1, 1, 14, 0, 0, 0, ZoneOffset.UTC).toInstant();
    final Date date = Date.from(instant);
    final GregorianCalendar gregorianCalendar =
        GregorianCalendar.from(instant.atZone(ZoneOffset.UTC));
    final XMLGregorianCalendar xmlGregorianCalendar =
        DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);

    final Ssld device =
        new Ssld(
            "id",
            "alias",
            new Address("city", "postal", "street", 42, "nr", "munic"),
            new GpsCoordinates(12f, 13f),
            null);
    device.updateRegistrationData(InetAddress.getByName("localhost"), Ssld.SSLD_TYPE);
    device.getOutputSettings();
    device.setTechnicalInstallationDate(date);

    when(this.ssldRepository.findByDeviceIdentification(anyString())).thenReturn(device);

    final org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Device jaxbDevice =
        this.deviceManagementMapper.map(
            device, org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Device.class);

    assertThat(jaxbDevice.getDeviceIdentification()).isEqualTo("id");
    assertThat(jaxbDevice.getAlias()).isEqualTo("alias");
    assertThat(jaxbDevice.getContainerAddress().getCity()).isEqualTo("city");
    assertThat(jaxbDevice.getContainerAddress().getPostalCode()).isEqualTo("postal");
    assertThat(jaxbDevice.getContainerAddress().getStreet()).isEqualTo("street");
    assertThat(jaxbDevice.getContainerAddress().getNumber()).isEqualTo(new Integer(42));
    assertThat(jaxbDevice.getContainerAddress().getNumberAddition()).isEqualTo("nr");
    assertThat(jaxbDevice.getContainerAddress().getMunicipality()).isEqualTo("munic");
    assertThat(jaxbDevice.getGpsLatitude()).isEqualTo("12.0");
    assertThat(jaxbDevice.getGpsLongitude()).isEqualTo("13.0");
    assertThat(jaxbDevice.getNetworkAddress()).isEqualTo("localhost/127.0.0.1");
    assertThat(jaxbDevice.getDeviceType()).isEqualTo(Ssld.SSLD_TYPE);
    assertThat(jaxbDevice.getOutputSettings().size()).isEqualTo(3);
    for (int i = 0; i < 3; i++) {
      assertThat(device.getOutputSettings().get(i).getAlias())
          .isEqualTo(jaxbDevice.getOutputSettings().get(i).getAlias());
    }
    assertThat(jaxbDevice.getTechnicalInstallationDate()).isEqualTo(xmlGregorianCalendar);

    final Ssld mappedBack = this.deviceManagementMapper.map(jaxbDevice, Ssld.class);

    assertThat(mappedBack.getDeviceIdentification()).isEqualTo("id");
    assertThat(mappedBack.getAlias()).isEqualTo("alias");
    assertThat(mappedBack.getContainerAddress().getCity()).isEqualTo("city");
    assertThat(mappedBack.getContainerAddress().getPostalCode()).isEqualTo("postal");
    assertThat(mappedBack.getContainerAddress().getStreet()).isEqualTo("street");
    assertThat(mappedBack.getContainerAddress().getNumber()).isEqualTo(new Integer(42));
    assertThat(mappedBack.getContainerAddress().getNumberAddition()).isEqualTo("nr");
    assertThat(mappedBack.getContainerAddress().getMunicipality()).isEqualTo("munic");
    assertThat(mappedBack.getGpsCoordinates().getLatitude()).isEqualTo(12.0f);
    assertThat(mappedBack.getGpsCoordinates().getLongitude()).isEqualTo(13.0f);
    // alas networkaddress in jaxb device is just a string, need parsing to
    // convert that to InetAddress
    assertThat(mappedBack.getNetworkAddress()).isEqualTo(null);
    assertThat(mappedBack.getDeviceType()).isEqualTo(Ssld.SSLD_TYPE);
    assertThat(mappedBack.getOutputSettings().size()).isEqualTo(3);
    for (int i = 0; i < 3; i++) {
      assertThat(device.getOutputSettings().get(i).getAlias())
          .isEqualTo(mappedBack.getOutputSettings().get(i).getAlias());
    }
    assertThat(mappedBack.getTechnicalInstallationDate()).isEqualTo(date);
  }
}
