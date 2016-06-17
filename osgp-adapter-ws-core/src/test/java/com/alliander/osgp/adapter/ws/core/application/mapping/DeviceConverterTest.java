package com.alliander.osgp.adapter.ws.core.application.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.SmartMeter;
import com.alliander.osgp.domain.core.entities.Ssld;
import com.alliander.osgp.domain.core.repositories.SsldRepository;

@RunWith(MockitoJUnitRunner.class)
public class DeviceConverterTest {

    @Mock
    private SsldRepository ssldRepository;

    private DeviceManagementMapper deviceManagementMapper;

    @Before
    public void initMapper() {
        this.deviceManagementMapper = new TestableDeviceManagementMapper(this.ssldRepository);
        this.deviceManagementMapper.initialize();
    }

    @Test
    public void testDeviceConversion() throws UnknownHostException {
        final Device device = new Device("id", "alias", "city", "postal", "street", "nr", "munic", 12f, 13f);
        device.updateRegistrationData(InetAddress.getByName("localhost"), "type");

        final com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device jaxbDevice = this.deviceManagementMapper
                .map(device, com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device.class);

        assertEquals("id", jaxbDevice.getDeviceIdentification());
        assertEquals("alias", jaxbDevice.getAlias());
        assertEquals("city", jaxbDevice.getContainerCity());
        assertEquals("postal", jaxbDevice.getContainerPostalCode());
        assertEquals("street", jaxbDevice.getContainerStreet());
        assertEquals("nr", jaxbDevice.getContainerNumber());
        assertEquals("munic", jaxbDevice.getContainerMunicipality());
        assertEquals("12.0", jaxbDevice.getGpsLatitude());
        assertEquals("13.0", jaxbDevice.getGpsLongitude());
        assertEquals("localhost/127.0.0.1", jaxbDevice.getNetworkAddress());
        assertEquals("type", jaxbDevice.getDeviceType());

        final Device mappedBack = this.deviceManagementMapper.map(jaxbDevice, Device.class);

        assertEquals("id", mappedBack.getDeviceIdentification());
        assertEquals("alias", mappedBack.getAlias());
        assertEquals("city", mappedBack.getContainerCity());
        assertEquals("postal", mappedBack.getContainerPostalCode());
        assertEquals("street", mappedBack.getContainerStreet());
        assertEquals("nr", mappedBack.getContainerNumber());
        assertEquals("munic", mappedBack.getContainerMunicipality());
        assertTrue(12 == mappedBack.getGpsLatitude());
        assertTrue(13 == mappedBack.getGpsLongitude());
        // alas networkaddress in jaxb device is just a string, need parsing to
        // convert that to InetAddress
        assertEquals(null, mappedBack.getNetworkAddress());
        assertEquals("type", mappedBack.getDeviceType());
    }

    @Test
    public void testSmartMeterConversion() throws UnknownHostException {
        final Device device = new SmartMeter("id", "alias", "city", "postal", "street", "nr", "munic", 12f, 13f);
        device.updateRegistrationData(InetAddress.getByName("localhost"), "type");

        final com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device jaxbDevice = this.deviceManagementMapper
                .map(device, com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device.class);

        assertEquals("id", jaxbDevice.getDeviceIdentification());
        assertEquals("alias", jaxbDevice.getAlias());
        assertEquals("city", jaxbDevice.getContainerCity());
        assertEquals("postal", jaxbDevice.getContainerPostalCode());
        assertEquals("street", jaxbDevice.getContainerStreet());
        assertEquals("nr", jaxbDevice.getContainerNumber());
        assertEquals("munic", jaxbDevice.getContainerMunicipality());
        assertEquals("12.0", jaxbDevice.getGpsLatitude());
        assertEquals("13.0", jaxbDevice.getGpsLongitude());
        assertEquals("localhost/127.0.0.1", jaxbDevice.getNetworkAddress());
        assertEquals("type", jaxbDevice.getDeviceType());

        final SmartMeter mappedBack = this.deviceManagementMapper.map(jaxbDevice, SmartMeter.class);

        assertEquals("id", mappedBack.getDeviceIdentification());
        assertEquals("alias", mappedBack.getAlias());
        assertEquals("city", mappedBack.getContainerCity());
        assertEquals("postal", mappedBack.getContainerPostalCode());
        assertEquals("street", mappedBack.getContainerStreet());
        assertEquals("nr", mappedBack.getContainerNumber());
        assertEquals("munic", mappedBack.getContainerMunicipality());
        assertTrue(12 == mappedBack.getGpsLatitude());
        assertTrue(13 == mappedBack.getGpsLongitude());
        // alas networkaddress in jaxb device is just a string, need parsing to
        // convert that to InetAddress
        assertEquals(null, mappedBack.getNetworkAddress());
        assertEquals("type", mappedBack.getDeviceType());
    }

    @Test
    public void testSsldConversion() throws UnknownHostException {
        final Ssld device = new Ssld("id", "alias", "city", "postal", "street", "nr", "munic", 12f, 13f);
        device.updateRegistrationData(InetAddress.getByName("localhost"), Ssld.SSLD_TYPE);
        device.getOutputSettings();

        when(this.ssldRepository.findByDeviceIdentification(anyString())).thenReturn(device);

        final com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device jaxbDevice = this.deviceManagementMapper
                .map(device, com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device.class);

        assertEquals("id", jaxbDevice.getDeviceIdentification());
        assertEquals("alias", jaxbDevice.getAlias());
        assertEquals("city", jaxbDevice.getContainerCity());
        assertEquals("postal", jaxbDevice.getContainerPostalCode());
        assertEquals("street", jaxbDevice.getContainerStreet());
        assertEquals("nr", jaxbDevice.getContainerNumber());
        assertEquals("munic", jaxbDevice.getContainerMunicipality());
        assertEquals("12.0", jaxbDevice.getGpsLatitude());
        assertEquals("13.0", jaxbDevice.getGpsLongitude());
        assertEquals("localhost/127.0.0.1", jaxbDevice.getNetworkAddress());
        assertEquals(Ssld.SSLD_TYPE, jaxbDevice.getDeviceType());
        assertEquals(3, jaxbDevice.getOutputSettings().size());
        for (int i = 0; i < 3; i++) {
            assertEquals(device.getOutputSettings().get(i).getAlias(), jaxbDevice.getOutputSettings().get(i).getAlias());

        }

        final Ssld mappedBack = this.deviceManagementMapper.map(jaxbDevice, Ssld.class);

        assertEquals("id", mappedBack.getDeviceIdentification());
        assertEquals("alias", mappedBack.getAlias());
        assertEquals("city", mappedBack.getContainerCity());
        assertEquals("postal", mappedBack.getContainerPostalCode());
        assertEquals("street", mappedBack.getContainerStreet());
        assertEquals("nr", mappedBack.getContainerNumber());
        assertEquals("munic", mappedBack.getContainerMunicipality());
        assertTrue(12 == mappedBack.getGpsLatitude());
        assertTrue(13 == mappedBack.getGpsLongitude());
        // alas networkaddress in jaxb device is just a string, need parsing to
        // convert that to InetAddress
        assertEquals(null, mappedBack.getNetworkAddress());
        assertEquals(Ssld.SSLD_TYPE, mappedBack.getDeviceType());
        assertEquals(3, mappedBack.getOutputSettings().size());
        for (int i = 0; i < 3; i++) {
            assertEquals(device.getOutputSettings().get(i).getAlias(), mappedBack.getOutputSettings().get(i).getAlias());

        }
    }

}
