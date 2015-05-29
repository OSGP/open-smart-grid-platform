package com.alliander.osgp.webdevicesimulator.application.tasks;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.oslp.Oslp;
import com.alliander.osgp.webdevicesimulator.domain.entities.Device;
import com.alliander.osgp.webdevicesimulator.domain.repositories.DeviceRepository;
import com.alliander.osgp.webdevicesimulator.service.RegisterDevice;

@Component
public class LightSwitchingOff implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(LightSwitchingOff.class);

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private RegisterDevice registerDevice;

    @Override
    public void run() {
        LOGGER.info("Registering devices");

        final List<Device> devices = this.deviceRepository.findAll();

        for (final Device device : devices) {
            LOGGER.info("Light switching for : {}: {} ", device.getId(), device.getDeviceIdentification());

            // Switching off Light
            this.registerDevice.lightSwitchOn(device.getId(), false);

            // Send EventNotifications for LightSwitching Off
            LOGGER.info("Sending LIGHT_EVENTS_LIGHT_OFF event for device : {}: {} ", device.getId(),
                    device.getDeviceIdentification());
            this.registerDevice.sendEventNotificationCommand(device.getId(), Oslp.Event.LIGHT_EVENTS_LIGHT_OFF_VALUE,
                    "LIGHT_EVENTS_LIGHT_OFF event occurred on Light Switching off ", null);
        }
    }
}