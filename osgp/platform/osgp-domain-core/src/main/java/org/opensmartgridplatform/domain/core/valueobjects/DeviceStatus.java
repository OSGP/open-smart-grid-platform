//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.valueobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DeviceStatus implements Status, Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = -6922074543798047230L;

  private List<LightValue> lightValues;

  private final LinkType preferredLinkType;

  private final LinkType actualLinkType;

  private final LightType lightType;

  private Integer eventNotificationsMask;

  private Integer numberOfOutputs;

  private Integer dcOutputVoltageMaximum;

  private Integer dcOutputVoltageCurrent;

  private Integer maximumOutputPowerOnDcOutput;

  private String serialNumber;

  private String macAddress;

  private String hardwareId;

  private Integer internalFlashMemSize;

  private Integer externalFlashMemSize;

  private Integer lastInternalTestResultCode;

  private Integer startupCounter;

  private String bootLoaderVersion;

  private String firmwareVersion;

  private String currentConfigurationBackUsed;

  private String name;

  private String currentTime;

  private String currentIp;

  public DeviceStatus(
      final List<LightValue> lightValues,
      final LinkType preferredLinkType,
      final LinkType actualLinkType,
      final LightType lightType,
      final Integer eventNotificationsMask) {
    this.lightValues = lightValues;
    this.preferredLinkType = preferredLinkType;
    this.actualLinkType = actualLinkType;
    this.lightType = lightType;
    this.eventNotificationsMask = eventNotificationsMask;
  }

  public List<EventNotificationType> getEventNotifications() {
    final List<EventNotificationType> events = new ArrayList<>();
    if (this.eventNotificationsMask > 0) {
      for (final EventNotificationType event : EventNotificationType.values()) {
        if ((this.eventNotificationsMask & (1 << event.ordinal())) != 0) {
          events.add(event);
        }
      }
    }
    return events;
  }

  public void updateLightValues(final List<LightValue> lightValues) {
    this.lightValues = lightValues;
  }

  public List<LightValue> getLightValues() {
    return this.lightValues;
  }

  public LinkType getPreferredLinkType() {
    return this.preferredLinkType;
  }

  public LinkType getActualLinkType() {
    return this.actualLinkType;
  }

  public LightType getLightType() {
    return this.lightType;
  }

  public Integer getEventNotificationsMask() {
    return this.eventNotificationsMask;
  }

  public Integer getNumberOfOutputs() {
    return this.numberOfOutputs;
  }

  public Integer getDcOutputVoltageMaximum() {
    return this.dcOutputVoltageMaximum;
  }

  public Integer getDcOutputVoltageCurrent() {
    return this.dcOutputVoltageCurrent;
  }

  public Integer getMaximumOutputPowerOnDcOutput() {
    return this.maximumOutputPowerOnDcOutput;
  }

  public String getSerialNumber() {
    return this.serialNumber;
  }

  public String getMacAddress() {
    return this.macAddress;
  }

  public String getHardwareId() {
    return this.hardwareId;
  }

  public Integer getInternalFlashMemSize() {
    return this.internalFlashMemSize;
  }

  public Integer getExternalFlashMemSize() {
    return this.externalFlashMemSize;
  }

  public Integer getLastInternalTestResultCode() {
    return this.lastInternalTestResultCode;
  }

  public Integer getStartupCounter() {
    return this.startupCounter;
  }

  public String getBootLoaderVersion() {
    return this.bootLoaderVersion;
  }

  public String getFirmwareVersion() {
    return this.firmwareVersion;
  }

  public String getCurrentConfigurationBackUsed() {
    return this.currentConfigurationBackUsed;
  }

  public String getName() {
    return this.name;
  }

  public String getCurrentTime() {
    return this.currentTime;
  }

  public String getCurrentIp() {
    return this.currentIp;
  }

  public void setEventNotificationsMask(final Integer eventNotificationsMask) {
    this.eventNotificationsMask = eventNotificationsMask;
  }

  public void setNumberOfOutputs(final Integer numberOfOutputs) {
    this.numberOfOutputs = numberOfOutputs;
  }

  public void setDcOutputVoltageMaximum(final Integer dcOutputVoltageMaximum) {
    this.dcOutputVoltageMaximum = dcOutputVoltageMaximum;
  }

  public void setDcOutputVoltageCurrent(final Integer dcOutputVoltageCurrent) {
    this.dcOutputVoltageCurrent = dcOutputVoltageCurrent;
  }

  public void setMaximumOutputPowerOnDcOutput(final Integer maximumOutputPowerOnDcOutput) {
    this.maximumOutputPowerOnDcOutput = maximumOutputPowerOnDcOutput;
  }

  public void setSerialNumber(final String serialNumber) {
    this.serialNumber = serialNumber;
  }

  public void setMacAddress(final String macAddress) {
    this.macAddress = macAddress;
  }

  public void setHardwareId(final String hardwareId) {
    this.hardwareId = hardwareId;
  }

  public void setInternalFlashMemSize(final Integer internalFlashMemSize) {
    this.internalFlashMemSize = internalFlashMemSize;
  }

  public void setExternalFlashMemSize(final Integer externalFlashMemSize) {
    this.externalFlashMemSize = externalFlashMemSize;
  }

  public void setLastInternalTestResultCode(final Integer lastInternalTestResultCode) {
    this.lastInternalTestResultCode = lastInternalTestResultCode;
  }

  public void setStartupCounter(final Integer startupCounter) {
    this.startupCounter = startupCounter;
  }

  public void setBootLoaderVersion(final String bootLoaderVersion) {
    this.bootLoaderVersion = bootLoaderVersion;
  }

  public void setFirmwareVersion(final String firmwareVersion) {
    this.firmwareVersion = firmwareVersion;
  }

  public void setCurrentConfigurationBackUsed(final String currentConfigurationBackUsed) {
    this.currentConfigurationBackUsed = currentConfigurationBackUsed;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public void setCurrentTime(final String currentTime) {
    this.currentTime = currentTime;
  }

  public void setCurrentIp(final String currentIp) {
    this.currentIp = currentIp;
  }
}
