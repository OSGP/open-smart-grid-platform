package com.alliander.osgp.adapter.protocol.oslp.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alliander.osgp.adapter.protocol.oslp.device.DeviceMessageStatus;
import com.alliander.osgp.adapter.protocol.oslp.exceptions.DeviceMessageFailedException;
import com.alliander.osgp.adapter.protocol.oslp.exceptions.DeviceMessageRejectedException;

@Service
public class DeviceResponseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceResponseService.class);

    public void handleDeviceMessageStatus(final DeviceMessageStatus status) throws DeviceMessageRejectedException,
            DeviceMessageFailedException {
        switch (status) {
        case FAILURE:
            LOGGER.info("Failure device message status received: {}", status);
            throw new DeviceMessageFailedException();
        case REJECTED:
            LOGGER.info("Rejected device message status received: {}", status);
            throw new DeviceMessageRejectedException();
        case OK:
            LOGGER.info("OK device message status received: {}", status);
            break;
        default:
            LOGGER.warn("Unknown device message status received: {}", status);
            break;
        }
    }
}
