// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DeviceStatusDto implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = -483312190851322867L;

  private List<LightValueDto> lightValues;

  private LinkTypeDto preferredLinkType;

  private LinkTypeDto actualLinkType;

  private LightTypeDto lightType;

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

  public DeviceStatusDto(
      final List<LightValueDto> lightValues,
      final LinkTypeDto preferredLinkType,
      final LinkTypeDto actualLinkType,
      final LightTypeDto lightType,
      final int eventNotificationsMask) {
    this.lightValues = lightValues;
    this.preferredLinkType = preferredLinkType;
    this.actualLinkType = actualLinkType;
    this.lightType = lightType;
    this.eventNotificationsMask = eventNotificationsMask;
  }

  public List<LightValueDto> getLightValues() {
    return this.lightValues;
  }

  public LinkTypeDto getPreferredLinkType() {
    return this.preferredLinkType;
  }

  public LinkTypeDto getActualLinkType() {
    return this.actualLinkType;
  }

  public LightTypeDto getLightType() {
    return this.lightType;
  }

  public int getEventNotificationsMask() {
    return this.eventNotificationsMask;
  }

  public List<EventNotificationTypeDto> getEventNotifications() {
    final List<EventNotificationTypeDto> events = new ArrayList<>();
    if (this.eventNotificationsMask > 0) {
      for (final EventNotificationTypeDto event : EventNotificationTypeDto.values()) {
        if ((this.eventNotificationsMask & (1 << event.ordinal())) != 0) {
          events.add(event);
        }
      }
    }
    return events;
  }

  public void updateLightValues(final List<LightValueDto> lightValues) {
    this.lightValues = lightValues;
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

  public void setLightValues(final List<LightValueDto> lightValues) {
    this.lightValues = lightValues;
  }

  public void setPreferredLinkType(final LinkTypeDto preferredLinkType) {
    this.preferredLinkType = preferredLinkType;
  }

  public void setActualLinkType(final LinkTypeDto actualLinkType) {
    this.actualLinkType = actualLinkType;
  }

  public void setLightType(final LightTypeDto lightType) {
    this.lightType = lightType;
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
