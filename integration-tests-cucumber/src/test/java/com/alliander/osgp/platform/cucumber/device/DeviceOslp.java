package com.alliander.osgp.platform.cucumber.device;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.protocol.oslp.domain.entities.OslpDevice;
import com.alliander.osgp.adapter.protocol.oslp.domain.repositories.OslpDeviceRepository;

import cucumber.api.java.en.Given;

public class DeviceOslp {

    @Autowired
    private OslpDeviceRepository oslpDeviceRespository;

    @Given("^an oslp device$")
    public void anOslpDevice(final Map<String, String> settings) throws Throwable {
        final String deviceIdentification = settings.get("DeviceIdentification");

        // TODO read data from table
        final OslpDevice device = new OslpDevice("MTIzNA==", deviceIdentification, "OSLP");
        this.oslpDeviceRespository.save(device);
    }
}
