/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking;

import com.alliander.osgp.adapter.protocol.iec61850.device.DeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.DeviceResponseHandler;
import com.alliander.osgp.adapter.protocol.iec61850.device.requests.GetPowerUsageHistoryDeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.requests.SetConfigurationDeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.requests.SetLightDeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.requests.SetScheduleDeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.requests.SetTransitionDeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.requests.UpdateFirmwareDeviceRequest;
import com.alliander.osgp.dto.valueobjects.DeviceStatus;

public interface DeviceService {

    void getConfiguration(DeviceRequest deviceRequest, DeviceResponseHandler deviceResponseHandler);

    void getFirmwareVersion(DeviceRequest deviceRequest, DeviceResponseHandler deviceResponseHandler);

    /**
     * Gets all status data from the device and returns it as a
     * {@link DeviceStatus}
     */
    void getStatus(DeviceRequest deviceRequest, final DeviceResponseHandler deviceResponseHandler);

    void setReboot(DeviceRequest deviceRequest, DeviceResponseHandler deviceResponseHandler);

    void setConfiguration(SetConfigurationDeviceRequest deviceRequest, DeviceResponseHandler deviceResponseHandler);

    // void setEventNotifications(DeviceRequest deviceRequest,
    // DeviceResponseHandler deviceResponseHandler,
    // String ipAddress);

    /**
     * Runs a selftest. StartOfTest is a boolean, used to indicate whether the
     * test is started or stopped
     */
    void runSelfTest(DeviceRequest deviceRequest, DeviceResponseHandler deviceResponseHandler, boolean startOfTest);

    void updateFirmware(UpdateFirmwareDeviceRequest deviceRequest, DeviceResponseHandler deviceResponseHandler);

    void getPowerUsageHistory(GetPowerUsageHistoryDeviceRequest deviceRequest,
            DeviceResponseHandler deviceResponseHandler);

    /**
     * Switches the light relays of the device given in the
     * {@link DeviceRequest}, according to the
     */
    void setLight(SetLightDeviceRequest deviceRequest, DeviceResponseHandler deviceResponseHandler);

    void setSchedule(SetScheduleDeviceRequest deviceRequest, DeviceResponseHandler deviceResponseHandler);

    void setTransition(SetTransitionDeviceRequest deviceRequest, DeviceResponseHandler deviceResponseHandler);
}
