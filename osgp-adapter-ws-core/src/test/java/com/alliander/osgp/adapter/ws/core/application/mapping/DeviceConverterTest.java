package com.alliander.osgp.adapter.ws.core.application.mapping;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
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

        Assert.assertEquals("id", jaxbDevice.getDeviceIdentification());
        Assert.assertEquals("alias", jaxbDevice.getAlias());
        Assert.assertEquals("city", jaxbDevice.getContainerCity());
        Assert.assertEquals("postal", jaxbDevice.getContainerPostalCode());
        Assert.assertEquals("street", jaxbDevice.getContainerStreet());
        Assert.assertEquals("nr", jaxbDevice.getContainerNumber());
        Assert.assertEquals("munic", jaxbDevice.getContainerMunicipality());
        Assert.assertEquals("12.0", jaxbDevice.getGpsLatitude());
        Assert.assertEquals("13.0", jaxbDevice.getGpsLongitude());
        Assert.assertEquals("localhost/127.0.0.1", jaxbDevice.getNetworkAddress());
        Assert.assertEquals("type", jaxbDevice.getDeviceType());
    }

    @Test
    public void testSmartMeterConversion() throws UnknownHostException {
        final Device device = new SmartMeter("id", "alias", "city", "postal", "street", "nr", "munic", 12f, 13f);
        device.updateRegistrationData(InetAddress.getByName("localhost"), "type");

        final com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device jaxbDevice = this.deviceManagementMapper
                .map(device, com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device.class);

        Assert.assertEquals("id", jaxbDevice.getDeviceIdentification());
        Assert.assertEquals("alias", jaxbDevice.getAlias());
        Assert.assertEquals("city", jaxbDevice.getContainerCity());
        Assert.assertEquals("postal", jaxbDevice.getContainerPostalCode());
        Assert.assertEquals("street", jaxbDevice.getContainerStreet());
        Assert.assertEquals("nr", jaxbDevice.getContainerNumber());
        Assert.assertEquals("munic", jaxbDevice.getContainerMunicipality());
        Assert.assertEquals("12.0", jaxbDevice.getGpsLatitude());
        Assert.assertEquals("13.0", jaxbDevice.getGpsLongitude());
        Assert.assertEquals("localhost/127.0.0.1", jaxbDevice.getNetworkAddress());
        Assert.assertEquals("type", jaxbDevice.getDeviceType());

    }

    @Test
    public void testSsldConversion() throws UnknownHostException {
        final Ssld device = new Ssld("id", "alias", "city", "postal", "street", "nr", "munic", 12f, 13f);
        device.updateRegistrationData(InetAddress.getByName("localhost"), Ssld.SSLD_TYPE);
        device.getOutputSettings();

        Mockito.when(this.ssldRepository.findByDeviceIdentification(Mockito.anyString())).thenReturn(device);

        final com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device jaxbDevice = this.deviceManagementMapper
                .map(device, com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device.class);

        Assert.assertEquals("id", jaxbDevice.getDeviceIdentification());
        Assert.assertEquals("alias", jaxbDevice.getAlias());
        Assert.assertEquals("city", jaxbDevice.getContainerCity());
        Assert.assertEquals("postal", jaxbDevice.getContainerPostalCode());
        Assert.assertEquals("street", jaxbDevice.getContainerStreet());
        Assert.assertEquals("nr", jaxbDevice.getContainerNumber());
        Assert.assertEquals("munic", jaxbDevice.getContainerMunicipality());
        Assert.assertEquals("12.0", jaxbDevice.getGpsLatitude());
        Assert.assertEquals("13.0", jaxbDevice.getGpsLongitude());
        Assert.assertEquals("localhost/127.0.0.1", jaxbDevice.getNetworkAddress());
        Assert.assertEquals(Ssld.SSLD_TYPE, jaxbDevice.getDeviceType());
        Assert.assertEquals(3, jaxbDevice.getOutputSettings().size());
        for (int i = 0; i < 3; i++) {
            Assert.assertEquals(device.getOutputSettings().get(i).getAlias(), jaxbDevice.getOutputSettings().get(i)
                    .getAlias());

        }

    }

}
