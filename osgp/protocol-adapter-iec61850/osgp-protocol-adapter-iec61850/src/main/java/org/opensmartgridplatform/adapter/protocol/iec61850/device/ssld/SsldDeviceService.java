/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld;

import javax.jms.JMSException;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceMessageStatus;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceResponseHandler;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.requests.SetConfigurationDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.requests.SetEventNotificationsDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.requests.SetLightDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.requests.SetScheduleDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.requests.SetTransitionDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.requests.UpdateDeviceSslCertificationDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.requests.UpdateFirmwareDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.responses.GetConfigurationDeviceResponse;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.responses.GetFirmwareVersionDeviceResponse;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.responses.GetStatusDeviceResponse;
import org.opensmartgridplatform.dto.valueobjects.ConfigurationDto;
import org.opensmartgridplatform.dto.valueobjects.DeviceStatusDto;
import org.opensmartgridplatform.dto.valueobjects.EventNotificationTypeDto;
import org.opensmartgridplatform.dto.valueobjects.ScheduleDto;
import org.opensmartgridplatform.dto.valueobjects.TransitionTypeDto;

public interface SsldDeviceService {

  /**
   * Reads the {@link DeviceStatusDto} from the device.
   *
   * <p>Returns a {@link GetStatusDeviceResponse} via the deviceResponseHandler's callback.
   */
  void getStatus(DeviceRequest deviceRequest, final DeviceResponseHandler deviceResponseHandler)
      throws JMSException;

  /**
   * Switches the given light relays on or off, depending on the given {@link LightValue} list.
   *
   * <p>Returns a {@link DeviceMessageStatus} via the deviceResponseHandler's callback.
   */
  void setLight(SetLightDeviceRequest deviceRequest, DeviceResponseHandler deviceResponseHandler)
      throws JMSException;

  /**
   * Writes all the given {@link ConfigurationDto Configuration} data to the device. Ignores all
   * null values.
   *
   * <p>Returns a {@link DeviceMessageStatus} via the deviceResponseHandler's callback.
   */
  void setConfiguration(
      SetConfigurationDeviceRequest deviceRequest, DeviceResponseHandler deviceResponseHandler)
      throws JMSException;

  /**
   * Reads {@link ConfigurationDto Configuration} data from the device.
   *
   * <p>Returns a {@link GetConfigurationDeviceResponse} via the deviceResponseHandler's callback.
   */
  void getConfiguration(DeviceRequest deviceRequest, DeviceResponseHandler deviceResponseHandler)
      throws JMSException;

  /**
   * Signals the Device to reboot.
   *
   * <p>Returns a {@link DeviceMessageStatus} via the deviceResponseHandler's callback.
   */
  void setReboot(DeviceRequest deviceRequest, DeviceResponseHandler deviceResponseHandler)
      throws JMSException;

  /**
   * Runs a self-test by turning all light relays on or off, depending on StartOfTest, then checking
   * to see it they are all on/off.
   *
   * <p>Returns a {@link DeviceMessageStatus} via the deviceResponseHandler's callback.
   */
  void runSelfTest(
      DeviceRequest deviceRequest, DeviceResponseHandler deviceResponseHandler, boolean startOfTest)
      throws JMSException;

  /**
   * Writes the list {@link ScheduleDto} entries to the device.
   *
   * <p>Returns a {@link DeviceMessageStatus} via the deviceResponseHandler's callback.
   */
  void setSchedule(
      SetScheduleDeviceRequest deviceRequest, DeviceResponseHandler deviceResponseHandler)
      throws JMSException;

  /**
   * Reads both the version of the functional and the security firmware.
   *
   * <p>Returns a {@link GetFirmwareVersionDeviceResponse} via the deviceResponseHandler's callback.
   */
  void getFirmwareVersion(DeviceRequest deviceRequest, DeviceResponseHandler deviceResponseHandler)
      throws JMSException;

  /**
   * Writes the {@link TransitionTypeDto} to the device.
   *
   * <p>Returns a {@link DeviceMessageStatus} via the deviceResponseHandler's callback.
   */
  void setTransition(
      SetTransitionDeviceRequest deviceRequest, DeviceResponseHandler deviceResponseHandler)
      throws JMSException;

  /**
   * Writes the download URL of the new firmware and the time it has to start downloading to the
   * Device.
   *
   * <p>Returns a {@link DeviceMessageStatus} via the deviceResponseHandler's callback.
   */
  void updateFirmware(
      UpdateFirmwareDeviceRequest deviceRequest, DeviceResponseHandler deviceResponseHandler)
      throws JMSException;

  /**
   * Writes the download URL of the new SSL certificate and the time it has to start downloading to
   * the Device.
   *
   * <p>Returns a {@link DeviceMessageStatus} via the deviceResponseHandler's callback.
   */
  void updateDeviceSslCertification(
      UpdateDeviceSslCertificationDeviceRequest deviceRequest,
      DeviceResponseHandler deviceResponseHandler)
      throws JMSException;

  /**
   * Writes the {@link EventNotificationTypeDto} list to the device.
   *
   * <p>Returns a {@link DeviceMessageStatus} via the deviceResponseHandler's callback.
   */
  void setEventNotifications(
      SetEventNotificationsDeviceRequest deviceRequest, DeviceResponseHandler deviceResponseHandler)
      throws JMSException;
}
