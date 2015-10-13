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
import com.alliander.osgp.adapter.protocol.oslp.device.requests.GetStatusDeviceRequest;
import com.alliander.osgp.adapter.protocol.oslp.device.requests.ResumeScheduleDeviceRequest;
import com.alliander.osgp.adapter.protocol.oslp.device.requests.SetConfigurationDeviceRequest;
import com.alliander.osgp.adapter.protocol.oslp.device.requests.SetEventNotificationsDeviceRequest;
import com.alliander.osgp.adapter.protocol.oslp.device.requests.SetLightDeviceRequest;
import com.alliander.osgp.adapter.protocol.oslp.device.requests.SetScheduleDeviceRequest;
import com.alliander.osgp.adapter.protocol.oslp.device.requests.SetTransitionDeviceRequest;
import com.alliander.osgp.adapter.protocol.oslp.device.requests.UpdateFirmwareDeviceRequest;
import com.alliander.osgp.dto.valueobjects.PageInfo;
import com.alliander.osgp.dto.valueobjects.PowerUsageHistoryResponseMessageDataContainer;
import com.alliander.osgp.oslp.OslpEnvelope;

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

    // CHANGED FUNCTIONS

    void newGetFirmwareVersion(DeviceRequest deviceRequest, String ipAddress, String domain, String domainVersion,
            String messageType, int retryCount, boolean isScheduled);

    void doGetFirmwareVerion(OslpEnvelope oslpRequest, DeviceRequest deviceRequest,
            DeviceResponseHandler deviceResponseHandler, String ipAddress) throws IOException;

    void newGetConfiguration(DeviceRequest deviceRequest, String ipAddress, String domain, String domainVersion,
            String messageType, int retryCount, boolean isScheduled);

    void doGetConfiguration(OslpEnvelope oslpRequest, DeviceRequest deviceRequest,
            DeviceResponseHandler deviceResponseHandler, String ipAddress) throws IOException;

    void newSetConfiguration(SetConfigurationDeviceRequest deviceRequest, String ipAddress, String domain,
            String domainVersion, String messageType, int retryCount, boolean isScheduled);

    void doSetConfiguration(OslpEnvelope oslpRequest, DeviceRequest deviceRequest,
            DeviceResponseHandler deviceResponseHandler, String ipAddress) throws IOException;

    void newGetStatus(GetStatusDeviceRequest deviceRequest, String ipAddress, String domain, String domainVersion,
            String messageType, int retryCount, boolean isScheduled);

    void doGetStatus(OslpEnvelope oslpRequest, DeviceRequest deviceRequest,
            DeviceResponseHandler deviceResponseHandler, String ipAddress) throws IOException;

    void newSetReboot(DeviceRequest deviceRequest, String ipAddress, String domain, String domainVersion,
            String messageType, int retryCount, boolean isScheduled);

    void doSetReboot(OslpEnvelope oslpRequest, DeviceRequest deviceRequest,
            DeviceResponseHandler deviceResponseHandler, String ipAddress) throws IOException;

    void newSetEventNotifications(SetEventNotificationsDeviceRequest deviceRequest, String ipAddress, String domain,
            String domainVersion, String messageType, int retryCount, boolean isScheduled);

    void doSetEventNotifications(OslpEnvelope oslpRequest, DeviceRequest deviceRequest,
            DeviceResponseHandler deviceResponseHandler, String ipAddress) throws IOException;

    void newStartSelfTest(DeviceRequest deviceRequest, String ipAddress, String domain, String domainVersion,
            String messageType, int retryCount, boolean isScheduled);

    void doStartSelfTest(OslpEnvelope oslpRequest, DeviceRequest deviceRequest,
            DeviceResponseHandler deviceResponseHandler, String ipAddress) throws IOException;

    void newStopSelfTest(DeviceRequest deviceRequest, String ipAddress, String domain, String domainVersion,
            String messageType, int retryCount, boolean isScheduled);

    void doStopSelfTest(OslpEnvelope oslpRequest, DeviceRequest deviceRequest,
            DeviceResponseHandler deviceResponseHandler, String ipAddress) throws IOException;

    void newUpdateFirmware(UpdateFirmwareDeviceRequest deviceRequest, String ipAddress, String domain,
            String domainVersion, String messageType, int retryCount, boolean isScheduled);

    void doUpdateFirmware(OslpEnvelope oslpRequest, DeviceRequest deviceRequest,
            DeviceResponseHandler deviceResponseHandler, String ipAddress) throws IOException;

    void newGetActualPowerUsage(DeviceRequest deviceRequest, String ipAddress, String domain, String domainVersion,
            String messageType, int retryCount, boolean isScheduled);

    void doGetActualPowerUsage(OslpEnvelope oslpRequest, DeviceRequest deviceRequest,
            DeviceResponseHandler deviceResponseHandler, String ipAddress) throws IOException;

    void newGetPowerUsageHistory(GetPowerUsageHistoryDeviceRequest deviceRequest, String ipAddress, String domain,
            String domainVersion, String messageType, int retryCount, boolean isScheduled);

    void doGetPowerUsageHistory(OslpEnvelope oslpRequest,
            PowerUsageHistoryResponseMessageDataContainer powerUsageHistoryResponseMessageDataContainer,
            GetPowerUsageHistoryDeviceRequest deviceRequest, DeviceResponseHandler deviceResponseHandler,
            String ipAddress, final String domain, final String domainVersion, final String messageType,
            final int retryCount, final boolean isScheduled) throws IOException;

    void newResumeSchedule(ResumeScheduleDeviceRequest deviceRequest, String ipAddress, String domain,
            String domainVersion, String messageType, int retryCount, boolean isScheduled);

    void doResumeSchedule(OslpEnvelope oslpRequest, DeviceRequest deviceRequest,
            DeviceResponseHandler deviceResponseHandler, String ipAddress) throws IOException;

    void newSetLight(SetLightDeviceRequest deviceRequest, String ipAddress, String domain, String domainVersion,
            String messageType, int retryCount, boolean isScheduled);

    void doSetLight(OslpEnvelope oslpRequest, DeviceRequest deviceRequest, DeviceResponseHandler deviceResponseHandler,
            String ipAddress) throws IOException;

    void newSetSchedule(SetScheduleDeviceRequest deviceRequest, String ipAddress, String domain, String domainVersion,
            String messageType, int retryCount, boolean isScheduled);

    void doSetSchedule(OslpEnvelope oslpRequest, SetScheduleDeviceRequest deviceRequest,
            DeviceResponseHandler deviceResponseHandler, String ipAddress, String domain, String domainVersion,
            String messageType, int retryCount, boolean isScheduled, PageInfo pageInfo) throws IOException;

    void newSetTransition(SetTransitionDeviceRequest deviceRequest, String ipAddress, String domain,
            String domainVersion, String messageType, int retryCount, boolean isScheduled);

    void doSetTransition(OslpEnvelope oslpRequest, DeviceRequest deviceRequest,
            DeviceResponseHandler deviceResponseHandler, String ipAddress) throws IOException;
}
