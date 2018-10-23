package org.opensmartgridplatform.adapter.ws.core.application.mapping;

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
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.repositories.SsldRepository;
import org.opensmartgridplatform.domain.core.valueobjects.Address;
import org.opensmartgridplatform.domain.core.valueobjects.GpsCoordinates;

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
        final Device device = new Device("id", "alias", new Address("city", "postal", "street", "nr", "munic"),
                new GpsCoordinates(12f, 13f), null);
        device.updateRegistrationData(InetAddress.getByName("localhost"), "type");

        final org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Device jaxbDevice = this.deviceManagementMapper
                .map(device, org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Device.class);

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
        assertEquals("city", mappedBack.getContainerAddress().getCity());
        assertEquals("postal", mappedBack.getContainerAddress().getPostalCode());
        assertEquals("street", mappedBack.getContainerAddress().getStreet());
        assertEquals("nr", mappedBack.getContainerAddress().getNumber());
        assertEquals("munic", mappedBack.getContainerAddress().getMunicipality());
        assertTrue(12 == mappedBack.getGpsCoordinates().getLatitude());
        assertTrue(13 == mappedBack.getGpsCoordinates().getLongitude());
        // alas networkaddress in jaxb device is just a string, need parsing to
        // convert that to InetAddress
        assertEquals(null, mappedBack.getNetworkAddress());
        assertEquals("type", mappedBack.getDeviceType());
    }

    @Test
    public void testSmartMeterConversion() throws UnknownHostException {
        final Device device = new SmartMeter("id", "alias", new Address("city", "postal", "street", "nr", "munic"),
                new GpsCoordinates(12f, 13f));
        device.updateRegistrationData(InetAddress.getByName("localhost"), "type");

        final org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Device jaxbDevice = this.deviceManagementMapper
                .map(device, org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Device.class);

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
        assertEquals("city", mappedBack.getContainerAddress().getCity());
        assertEquals("postal", mappedBack.getContainerAddress().getPostalCode());
        assertEquals("street", mappedBack.getContainerAddress().getStreet());
        assertEquals("nr", mappedBack.getContainerAddress().getNumber());
        assertEquals("munic", mappedBack.getContainerAddress().getMunicipality());
        assertTrue(12 == mappedBack.getGpsCoordinates().getLatitude());
        assertTrue(13 == mappedBack.getGpsCoordinates().getLongitude());
        // alas networkaddress in jaxb device is just a string, need parsing to
        // convert that to InetAddress
        assertEquals(null, mappedBack.getNetworkAddress());
        assertEquals("type", mappedBack.getDeviceType());
    }

    @Test
    public void testSsldConversion() throws UnknownHostException {
        final Ssld device = new Ssld("id", "alias", new Address("city", "postal", "street", "nr", "munic"),
                new GpsCoordinates(12f, 13f), null);
        device.updateRegistrationData(InetAddress.getByName("localhost"), Ssld.SSLD_TYPE);
        device.getOutputSettings();

        when(this.ssldRepository.findByDeviceIdentification(anyString())).thenReturn(device);

        final org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Device jaxbDevice = this.deviceManagementMapper
                .map(device, org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Device.class);

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
            assertEquals(device.getOutputSettings().get(i).getAlias(),
                    jaxbDevice.getOutputSettings().get(i).getAlias());

        }

        final Ssld mappedBack = this.deviceManagementMapper.map(jaxbDevice, Ssld.class);

        assertEquals("id", mappedBack.getDeviceIdentification());
        assertEquals("alias", mappedBack.getAlias());
        assertEquals("city", mappedBack.getContainerAddress().getCity());
        assertEquals("postal", mappedBack.getContainerAddress().getPostalCode());
        assertEquals("street", mappedBack.getContainerAddress().getStreet());
        assertEquals("nr", mappedBack.getContainerAddress().getNumber());
        assertEquals("munic", mappedBack.getContainerAddress().getMunicipality());
        assertTrue(12 == mappedBack.getGpsCoordinates().getLatitude());
        assertTrue(13 == mappedBack.getGpsCoordinates().getLongitude());
        // alas networkaddress in jaxb device is just a string, need parsing to
        // convert that to InetAddress
        assertEquals(null, mappedBack.getNetworkAddress());
        assertEquals(Ssld.SSLD_TYPE, mappedBack.getDeviceType());
        assertEquals(3, mappedBack.getOutputSettings().size());
        for (int i = 0; i < 3; i++) {
            assertEquals(device.getOutputSettings().get(i).getAlias(),
                    mappedBack.getOutputSettings().get(i).getAlias());

        }
    }

}
