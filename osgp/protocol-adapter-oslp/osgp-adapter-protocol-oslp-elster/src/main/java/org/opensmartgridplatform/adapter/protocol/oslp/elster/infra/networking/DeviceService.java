/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.networking;

import java.io.IOException;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceResponseHandler;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests.GetPowerUsageHistoryDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests.GetStatusDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests.ResumeScheduleDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests.SetConfigurationDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests.SetDeviceVerificationKeyDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests.SetEventNotificationsDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests.SetLightDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests.SetScheduleDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests.SetTransitionDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests.SwitchConfigurationBankRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests.SwitchFirmwareDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests.UpdateDeviceSslCertificationDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests.UpdateFirmwareDeviceRequest;
import org.opensmartgridplatform.dto.valueobjects.PageInfoDto;
import org.opensmartgridplatform.dto.valueobjects.PowerUsageHistoryResponseMessageDataContainerDto;
import org.opensmartgridplatform.oslp.OslpEnvelope;

public interface DeviceService {

    void getConfiguration(DeviceRequest deviceRequest);

    void doGetConfiguration(OslpEnvelope oslpRequest, DeviceRequest deviceRequest,
            DeviceResponseHandler deviceResponseHandler, String ipAddress) throws IOException;

    void getFirmwareVersion(DeviceRequest deviceRequest);

    void doGetFirmwareVersion(OslpEnvelope oslpRequest, DeviceRequest deviceRequest,
            DeviceResponseHandler deviceResponseHandler, String ipAddress) throws IOException;

    void getStatus(GetStatusDeviceRequest deviceRequest);

    void doGetStatus(OslpEnvelope oslpRequest, DeviceRequest deviceRequest, DeviceResponseHandler deviceResponseHandler,
            String ipAddress) throws IOException;

    void setReboot(DeviceRequest deviceRequest);

    void doSetReboot(OslpEnvelope oslpRequest, DeviceRequest deviceRequest, DeviceResponseHandler deviceResponseHandler,
            String ipAddress) throws IOException;

    void setConfiguration(SetConfigurationDeviceRequest deviceRequest);

    void doSetConfiguration(OslpEnvelope oslpRequest, DeviceRequest setConfigurationDeviceRequest,
            DeviceRequest setRebootDeviceRequest, DeviceResponseHandler setConfigurationDeviceResponseHandler,
            DeviceResponseHandler setRebootDeviceResponseHandler, String ipAddress) throws IOException;

    void switchConfiguration(SwitchConfigurationBankRequest deviceRequest);

    void doSwitchConfiguration(OslpEnvelope oslpRequest, DeviceRequest deviceRequest,
            DeviceResponseHandler deviceResponseHandler, String ipAddress) throws IOException;

    void setEventNotifications(SetEventNotificationsDeviceRequest deviceRequest);

    void doSetEventNotifications(OslpEnvelope oslpRequest, DeviceRequest deviceRequest,
            DeviceResponseHandler deviceResponseHandler, String ipAddress) throws IOException;

    void startSelfTest(DeviceRequest deviceRequest);

    void doStartSelfTest(OslpEnvelope oslpRequest, DeviceRequest deviceRequest,
            DeviceResponseHandler deviceResponseHandler, String ipAddress) throws IOException;

    void stopSelfTest(DeviceRequest deviceRequest);

    void doStopSelfTest(OslpEnvelope oslpRequest, DeviceRequest deviceRequest,
            DeviceResponseHandler deviceResponseHandler, String ipAddress) throws IOException;

    void updateFirmware(UpdateFirmwareDeviceRequest deviceRequest);

    void doUpdateFirmware(OslpEnvelope oslpRequest, DeviceRequest deviceRequest,
            DeviceResponseHandler deviceResponseHandler, String ipAddress) throws IOException;

    void switchFirmware(SwitchFirmwareDeviceRequest deviceRequest);

    void doSwitchFirmware(OslpEnvelope oslpRequest, DeviceRequest deviceRequest,
            DeviceResponseHandler deviceResponseHandler, String ipAddress) throws IOException;

    void getActualPowerUsage(DeviceRequest deviceRequest);

    void doGetActualPowerUsage(OslpEnvelope oslpRequest, DeviceRequest deviceRequest,
            DeviceResponseHandler deviceResponseHandler, String ipAddress) throws IOException;

    void getPowerUsageHistory(GetPowerUsageHistoryDeviceRequest deviceRequest);

    void doGetPowerUsageHistory(OslpEnvelope oslpRequest,
            PowerUsageHistoryResponseMessageDataContainerDto powerUsageHistoryResponseMessageDataContainer,
            GetPowerUsageHistoryDeviceRequest deviceRequest, DeviceResponseHandler deviceResponseHandler,
            String ipAddress) throws IOException;

    void resumeSchedule(ResumeScheduleDeviceRequest deviceRequest);

    void doResumeSchedule(OslpEnvelope oslpRequest, DeviceRequest deviceRequest,
            DeviceResponseHandler deviceResponseHandler, String ipAddress) throws IOException;

    void setLight(SetLightDeviceRequest deviceRequest);

    void doSetLight(OslpEnvelope oslpRequest, DeviceRequest setLightdeviceRequest,
            ResumeScheduleDeviceRequest resumeScheduleDeviceRequest,
            DeviceResponseHandler setLightDeviceResponseHandler,
            DeviceResponseHandler resumeScheduleDeviceResponseHandler, String ipAddress) throws IOException;

    void setSchedule(SetScheduleDeviceRequest deviceRequest);

    void doSetSchedule(OslpEnvelope oslpRequest, SetScheduleDeviceRequest deviceRequest,
            DeviceResponseHandler deviceResponseHandler, String ipAddress, String domain, String domainVersion,
            String messageType, int messagePriority, int retryCount, boolean isScheduled, PageInfoDto pageInfo)
            throws IOException;

    void setTransition(SetTransitionDeviceRequest deviceRequest);

    void doSetTransition(OslpEnvelope oslpRequest, DeviceRequest deviceRequest,
            DeviceResponseHandler deviceResponseHandler, String ipAddress) throws IOException;

    void updateDeviceSslCertification(UpdateDeviceSslCertificationDeviceRequest deviceRequest);

    void doUpdateDeviceSslCertification(OslpEnvelope oslpRequest, DeviceRequest deviceRequest,
            DeviceResponseHandler deviceResponseHandler, String ipAddress) throws IOException;

    void setDeviceVerificationKey(SetDeviceVerificationKeyDeviceRequest deviceRequest);

    void doSetDeviceVerificationKey(OslpEnvelope oslpRequest, DeviceRequest deviceRequest,
            DeviceResponseHandler deviceResponseHandler, String ipAddress) throws IOException;
}
