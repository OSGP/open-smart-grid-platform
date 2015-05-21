/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.infra.networking;

import java.io.IOException;

import com.alliander.osgp.adapter.protocol.oslp.device.DeviceRequest;
import com.alliander.osgp.adapter.protocol.oslp.device.DeviceResponseHandler;
import com.alliander.osgp.adapter.protocol.oslp.device.requests.GetPowerUsageHistoryDeviceRequest;
import com.alliander.osgp.adapter.protocol.oslp.device.requests.ResumeScheduleDeviceRequest;
import com.alliander.osgp.adapter.protocol.oslp.device.requests.SetConfigurationDeviceRequest;
import com.alliander.osgp.adapter.protocol.oslp.device.requests.SetEventNotificationsDeviceRequest;
import com.alliander.osgp.adapter.protocol.oslp.device.requests.SetLightDeviceRequest;
import com.alliander.osgp.adapter.protocol.oslp.device.requests.SetScheduleDeviceRequest;
import com.alliander.osgp.adapter.protocol.oslp.device.requests.SetTransitionDeviceRequest;
import com.alliander.osgp.adapter.protocol.oslp.device.requests.UpdateFirmwareDeviceRequest;

public interface DeviceService {

    void startSelfTest(DeviceRequest deviceRequest, DeviceResponseHandler deviceResponseHandler, String ipAddress)
            throws IOException;

    void stopSelfTest(DeviceRequest deviceRequest, DeviceResponseHandler deviceResponseHandler, String ipAddress)
            throws IOException;

    void setLight(SetLightDeviceRequest deviceRequest, DeviceResponseHandler deviceResponseHandler, String ipAddress)
            throws IOException;

    void getStatus(DeviceRequest deviceRequest, DeviceResponseHandler deviceResponseHandler, String ipAddress)
            throws IOException;

    void setEventNotifications(SetEventNotificationsDeviceRequest deviceRequest,
            DeviceResponseHandler deviceResponseHandler, String ipAddress) throws IOException;

    void updateFirmware(UpdateFirmwareDeviceRequest deviceRequest, DeviceResponseHandler deviceResponseHandler,
            String ipAddress) throws IOException;

    void setConfiguration(SetConfigurationDeviceRequest deviceRequest, DeviceResponseHandler deviceResponseHandler,
            String ipAddress) throws IOException;

    void getConfiguration(DeviceRequest deviceRequest, DeviceResponseHandler deviceResponseHandler, String ipAddress)
            throws IOException;

    void getActualPowerUsage(DeviceRequest deviceRequest, DeviceResponseHandler deviceResponseHandler, String ipAddress)
            throws IOException;

    void getPowerUsageHistory(GetPowerUsageHistoryDeviceRequest deviceRequest,
            DeviceResponseHandler deviceResponseHandler, String ipAddress) throws IOException;

    void setSchedule(SetScheduleDeviceRequest deviceRequest, DeviceResponseHandler deviceResponseHandler,
            String ipAddress) throws IOException;

    void resumeSchedule(ResumeScheduleDeviceRequest deviceRequest, DeviceResponseHandler deviceResponseHandler,
            String ipAddress) throws IOException;

    void setReboot(DeviceRequest deviceRequest, DeviceResponseHandler deviceResponseHandler, String ipAddress)
            throws IOException;

    void setTransition(SetTransitionDeviceRequest deviceRequest, DeviceResponseHandler deviceResponseHandler,
            String ipAddress) throws IOException;

    void getFirmwareVersion(DeviceRequest deviceRequest, DeviceResponseHandler deviceResponseHandler, String ipAddress)
            throws IOException;
}
