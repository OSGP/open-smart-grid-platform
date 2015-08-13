package com.alliander.osgp.webdevicesimulator.application.tasks;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.oslp.Oslp;
import com.alliander.osgp.webdevicesimulator.application.services.DeviceManagementService;
import com.alliander.osgp.webdevicesimulator.domain.entities.Device;
import com.alliander.osgp.webdevicesimulator.domain.repositories.DeviceRepository;
import com.alliander.osgp.webdevicesimulator.service.RegisterDevice;
import com.alliander.osgp.webdevicesimulator.service.SwitchingServices;

@Component
public class EveningMorningBurnersLightSwitchingOff implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(EveningMorningBurnersLightSwitchingOff.class);

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private SwitchingServices switchingServices;

    @Autowired
    private RegisterDevice registerDevice;

    @Autowired
    private DeviceManagementService deviceManagementService;

    @Override
    public void run() {

        if (this.deviceManagementService.getLightSwitching()) {

            LOGGER.info("Publiclighting Switching off for devices with Evening/Morning Burners");

            final List<Device> devices = this.deviceRepository.findByHasEveningMorningBurner(true);

            for (final Device device : devices) {
                LOGGER.info("Light switching for : {}: {} ", device.getId(), device.getDeviceIdentification());

                // Switching off Light
                this.switchingServices.lightSwitchOff(device.getId());

                // Send EventNotifications for LightSwitching Off
                LOGGER.info("Sending LIGHT_EVENTS_LIGHT_OFF event for device : {}: {} ", device.getId(),
                        device.getDeviceIdentification());

                // The event index for Evening/Morning Burners is 2.
                this.registerDevice.sendEventNotificationCommand(device.getId(),
                        Oslp.Event.LIGHT_EVENTS_LIGHT_OFF_VALUE,
                        "LIGHT_EVENTS_LIGHT_OFF event occurred on Light Switching off ", 2);
            }
        }
    }
}