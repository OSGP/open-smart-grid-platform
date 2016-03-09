/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking;

import com.alliander.osgp.adapter.protocol.iec61850.device.DeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.requests.GetStatusDeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.requests.SetLightDeviceRequest;

public interface DeviceService {
    //
    // // void getConfiguration(DeviceRequest deviceRequest);
    //
    // void doGetConfiguration(DeviceRequest deviceRequest,
    // DeviceResponseHandler deviceResponseHandler, String ipAddress)
    // throws IOException;
    //
    // // void getFirmwareVersion(DeviceRequest deviceRequest);
    //
    // void doGetFirmwareVersion(DeviceRequest deviceRequest,
    // DeviceResponseHandler deviceResponseHandler, String ipAddress)
    // throws IOException;
    //
    void getStatus(GetStatusDeviceRequest deviceRequest);

    //
    // void doGetStatus(DeviceRequest deviceRequest, DeviceResponseHandler
    // deviceResponseHandler, String ipAddress)
    // throws IOException;
    //
    // // void setReboot(DeviceRequest deviceRequest);
    //
    // void doSetReboot(DeviceRequest deviceRequest, DeviceResponseHandler
    // deviceResponseHandler, String ipAddress)
    // throws IOException;
    //
    // // void setConfiguration(SetConfigurationDeviceRequest deviceRequest);
    //
    // void doSetConfiguration(DeviceRequest deviceRequest,
    // DeviceResponseHandler deviceResponseHandler, String ipAddress)
    // throws IOException;
    //
    // // void setEventNotifications(SetEventNotificationsDeviceRequest
    // // deviceRequest);
    //
    // void doSetEventNotifications(DeviceRequest deviceRequest,
    // DeviceResponseHandler deviceResponseHandler,
    // String ipAddress) throws IOException;
    //
    // // void startSelfTest(DeviceRequest deviceRequest);
    //
    // void doStartSelfTest(DeviceRequest deviceRequest, DeviceResponseHandler
    // deviceResponseHandler, String ipAddress)
    // throws IOException;
    //
    // // void stopSelfTest(DeviceRequest deviceRequest);
    //
    // void doStopSelfTest(DeviceRequest deviceRequest, DeviceResponseHandler
    // deviceResponseHandler, String ipAddress)
    // throws IOException;
    //
    // // void updateFirmware(UpdateFirmwareDeviceRequest deviceRequest);
    //
    // void doUpdateFirmware(DeviceRequest deviceRequest, DeviceResponseHandler
    // deviceResponseHandler, String ipAddress)
    // throws IOException;
    //
    // // void getActualPowerUsage(DeviceRequest deviceRequest);
    //
    // void doGetActualPowerUsage(DeviceRequest deviceRequest,
    // DeviceResponseHandler deviceResponseHandler,
    // String ipAddress) throws IOException;
    //
    // // void getPowerUsageHistory(GetPowerUsageHistoryDeviceRequest
    // // deviceRequest);
    //
    // void doGetPowerUsageHistory(
    // PowerUsageHistoryResponseMessageDataContainer
    // powerUsageHistoryResponseMessageDataContainer,
    // GetPowerUsageHistoryDeviceRequest deviceRequest, DeviceResponseHandler
    // deviceResponseHandler,
    // String ipAddress, String domain, String domainVersion, String
    // messageType, int retryCount,
    // boolean isScheduled) throws IOException;
    //
    // // void resumeSchedule(ResumeScheduleDeviceRequest deviceRequest);
    //
    // void doResumeSchedule(DeviceRequest deviceRequest, DeviceResponseHandler
    // deviceResponseHandler, String ipAddress)
    // throws IOException;
    //

    /**
     * Switches the light relays of the device given in the
     * {@link DeviceRequest}, according to the
     */
    void setLight(SetLightDeviceRequest deviceRequest);
    //
    // void doSetLight(DeviceRequest deviceRequest, DeviceResponseHandler
    // deviceResponseHandler, String ipAddress)
    // throws IOException;
    //
    // // void setSchedule(SetScheduleDeviceRequest deviceRequest);
    //
    // void doSetSchedule(SetScheduleDeviceRequest deviceRequest,
    // DeviceResponseHandler deviceResponseHandler,
    // String ipAddress, String domain, String domainVersion, String
    // messageType, int retryCount,
    // boolean isScheduled, PageInfo pageInfo) throws IOException;
    //
    // // void setTransition(SetTransitionDeviceRequest deviceRequest);
    //
    // void doSetTransition(DeviceRequest deviceRequest, DeviceResponseHandler
    // deviceResponseHandler, String ipAddress)
    // throws IOException;
}
