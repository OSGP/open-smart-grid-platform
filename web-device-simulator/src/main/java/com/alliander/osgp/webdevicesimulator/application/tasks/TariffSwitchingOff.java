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
public class TariffSwitchingOff implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(TariffSwitchingOff.class);

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private RegisterDevice registerDevice;

    @Override
    public void run() {
        LOGGER.info("Registering devices");

        final List<Device> devices = this.deviceRepository.findAll();

        for (final Device device : devices) {
            LOGGER.info("Tariff switching for : {}: {} ", device.getId(), device.getDeviceIdentification());

            // Switching off Tariff
            this.registerDevice.tariffSwitchOn(device.getId(), false);

            // Send EventNotifications for TariffSwitching Off
            LOGGER.info("Sending TARIFF_EVENTS_TARIFF_OFF event for device : {}: {} ", device.getId(),
                    device.getDeviceIdentification());
            this.registerDevice.sendEventNotificationCommand(device.getId(), Oslp.Event.TARIFF_EVENTS_TARIFF_OFF_VALUE,
                    "TARIFF_EVENTS_TARIFF_OFF event occurred on Tariff Switching off ", null);
        }
    }
}