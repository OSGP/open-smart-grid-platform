// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.networking;

import java.io.IOException;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceResponseHandler;
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
import org.opensmartgridplatform.oslp.LegacyOslpEnvelope;

public interface DeviceService {

  void getConfiguration(DeviceRequest deviceRequest);

  void doGetConfiguration(
      LegacyOslpEnvelope oslpRequest,
      DeviceRequest deviceRequest,
      DeviceResponseHandler deviceResponseHandler,
      String ipAddress)
      throws IOException;

  void getFirmwareVersion(DeviceRequest deviceRequest);

  void doGetFirmwareVersion(
      LegacyOslpEnvelope oslpRequest,
      DeviceRequest deviceRequest,
      DeviceResponseHandler deviceResponseHandler,
      String ipAddress)
      throws IOException;

  void getStatus(GetStatusDeviceRequest deviceRequest);

  void doGetStatus(
      LegacyOslpEnvelope oslpRequest,
      DeviceRequest deviceRequest,
      DeviceResponseHandler deviceResponseHandler,
      String ipAddress)
      throws IOException;

  void setReboot(DeviceRequest deviceRequest);

  void doSetReboot(
      LegacyOslpEnvelope oslpRequest,
      DeviceRequest deviceRequest,
      DeviceResponseHandler deviceResponseHandler,
      String ipAddress)
      throws IOException;

  void setConfiguration(SetConfigurationDeviceRequest deviceRequest);

  void doSetConfiguration(
      LegacyOslpEnvelope oslpRequest,
      DeviceRequest setConfigurationDeviceRequest,
      DeviceRequest setRebootDeviceRequest,
      DeviceResponseHandler setConfigurationDeviceResponseHandler,
      DeviceResponseHandler setRebootDeviceResponseHandler,
      String ipAddress)
      throws IOException;

  void switchConfiguration(SwitchConfigurationBankRequest deviceRequest);

  void doSwitchConfiguration(
      LegacyOslpEnvelope oslpRequest,
      DeviceRequest deviceRequest,
      DeviceResponseHandler deviceResponseHandler,
      String ipAddress)
      throws IOException;

  void setEventNotifications(SetEventNotificationsDeviceRequest deviceRequest);

  void doSetEventNotifications(
      LegacyOslpEnvelope oslpRequest,
      DeviceRequest deviceRequest,
      DeviceResponseHandler deviceResponseHandler,
      String ipAddress)
      throws IOException;

  void startSelfTest(DeviceRequest deviceRequest);

  void doStartSelfTest(
      LegacyOslpEnvelope oslpRequest,
      DeviceRequest deviceRequest,
      DeviceResponseHandler deviceResponseHandler,
      String ipAddress)
      throws IOException;

  void stopSelfTest(DeviceRequest deviceRequest);

  void doStopSelfTest(
      LegacyOslpEnvelope oslpRequest,
      DeviceRequest deviceRequest,
      DeviceResponseHandler deviceResponseHandler,
      String ipAddress)
      throws IOException;

  void updateFirmware(UpdateFirmwareDeviceRequest deviceRequest);

  void doUpdateFirmware(
      LegacyOslpEnvelope oslpRequest,
      DeviceRequest deviceRequest,
      DeviceResponseHandler deviceResponseHandler,
      String ipAddress)
      throws IOException;

  void switchFirmware(SwitchFirmwareDeviceRequest deviceRequest);

  void doSwitchFirmware(
      LegacyOslpEnvelope oslpRequest,
      DeviceRequest deviceRequest,
      DeviceResponseHandler deviceResponseHandler,
      String ipAddress)
      throws IOException;

  void resumeSchedule(ResumeScheduleDeviceRequest deviceRequest);

  void doResumeSchedule(
      LegacyOslpEnvelope oslpRequest,
      DeviceRequest deviceRequest,
      DeviceResponseHandler deviceResponseHandler,
      String ipAddress)
      throws IOException;

  void setLight(SetLightDeviceRequest deviceRequest);

  void doSetLight(
      LegacyOslpEnvelope oslpRequest,
      DeviceRequest setLightdeviceRequest,
      ResumeScheduleDeviceRequest resumeScheduleDeviceRequest,
      DeviceResponseHandler setLightDeviceResponseHandler,
      DeviceResponseHandler resumeScheduleDeviceResponseHandler,
      String ipAddress)
      throws IOException;

  void setSchedule(SetScheduleDeviceRequest deviceRequest);

  void doSetSchedule(
      LegacyOslpEnvelope oslpRequest,
      SetScheduleDeviceRequest deviceRequest,
      DeviceResponseHandler deviceResponseHandler,
      String ipAddress,
      String domain,
      String domainVersion,
      String messageType,
      int messagePriority,
      int retryCount,
      boolean isScheduled,
      PageInfoDto pageInfo)
      throws IOException;

  void setTransition(SetTransitionDeviceRequest deviceRequest);

  void doSetTransition(
      LegacyOslpEnvelope oslpRequest,
      DeviceRequest deviceRequest,
      DeviceResponseHandler deviceResponseHandler,
      String ipAddress)
      throws IOException;

  void updateDeviceSslCertification(UpdateDeviceSslCertificationDeviceRequest deviceRequest);

  void doUpdateDeviceSslCertification(
      LegacyOslpEnvelope oslpRequest,
      DeviceRequest deviceRequest,
      DeviceResponseHandler deviceResponseHandler,
      String ipAddress)
      throws IOException;

  void setDeviceVerificationKey(SetDeviceVerificationKeyDeviceRequest deviceRequest);

  void doSetDeviceVerificationKey(
      LegacyOslpEnvelope oslpRequest,
      DeviceRequest deviceRequest,
      DeviceResponseHandler deviceResponseHandler,
      String ipAddress)
      throws IOException;
}
