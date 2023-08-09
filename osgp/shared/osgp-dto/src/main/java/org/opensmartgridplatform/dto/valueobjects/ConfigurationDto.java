// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class ConfigurationDto implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = 8359276160483972289L;

  private final LightTypeDto lightType;

  private final DaliConfigurationDto daliConfiguration;

  private RelayConfigurationDto relayConfiguration;

  private final LinkTypeDto preferredLinkType;

  private Integer timeSyncFrequency;

  private DeviceFixedIpDto deviceFixedIp;

  private Boolean dhcpEnabled;

  private Boolean tlsEnabled;

  private Integer tlsPortNumber;

  private String commonNameString;

  private Integer communicationTimeout;

  private Integer communicationNumberOfRetries;

  private Integer communicationPauseTimeBetweenConnectionTrials;

  private String osgpIpAddress;

  private Integer osgpPortNumber;

  private String ntpHost;

  private Boolean ntpEnabled;

  private Integer ntpSyncInterval;

  private Boolean testButtonEnabled;

  private Boolean automaticSummerTimingEnabled;

  private Integer astroGateSunRiseOffset;

  private Integer astroGateSunSetOffset;

  private List<Integer> switchingDelays;

  private List<RelayMatrixDto> relayLinking;

  private Boolean relayRefreshing;

  private ZonedDateTime summerTimeDetails;

  private ZonedDateTime winterTimeDetails;

  private ConfigurationDto(final Builder builder) {
    this.lightType = builder.lightType;
    this.daliConfiguration = builder.daliConfiguration;
    this.relayConfiguration = builder.relayConfiguration;
    this.preferredLinkType = builder.preferredLinkType;
  }

  public static class Builder {

    private LightTypeDto lightType;
    private DaliConfigurationDto daliConfiguration;
    private RelayConfigurationDto relayConfiguration;
    private LinkTypeDto preferredLinkType;

    public ConfigurationDto build() {
      return new ConfigurationDto(this);
    }

    public Builder withLightType(final LightTypeDto lightType) {
      this.lightType = lightType;
      return this;
    }

    public Builder withDaliConfiguration(final DaliConfigurationDto daliConfiguration) {
      this.daliConfiguration = daliConfiguration;
      return this;
    }

    public Builder withRelayConfiguration(final RelayConfigurationDto relayConfiguration) {
      this.relayConfiguration = relayConfiguration;
      return this;
    }

    public Builder withPreferredLinkType(final LinkTypeDto preferredLinkType) {
      this.preferredLinkType = preferredLinkType;
      return this;
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public LightTypeDto getLightType() {
    return this.lightType;
  }

  public DaliConfigurationDto getDaliConfiguration() {
    return this.daliConfiguration;
  }

  public RelayConfigurationDto getRelayConfiguration() {
    return this.relayConfiguration;
  }

  public void setRelayConfiguration(final RelayConfigurationDto relayConfiguration) {
    this.relayConfiguration = relayConfiguration;
  }

  public LinkTypeDto getPreferredLinkType() {
    return this.preferredLinkType;
  }

  public Integer getTimeSyncFrequency() {
    return this.timeSyncFrequency;
  }

  public void setTimeSyncFrequency(final Integer timeSyncFrequency) {
    this.timeSyncFrequency = timeSyncFrequency;
  }

  public DeviceFixedIpDto getDeviceFixedIp() {
    return this.deviceFixedIp;
  }

  public void setDeviceFixedIp(final DeviceFixedIpDto deviceFixedIp) {
    this.deviceFixedIp = deviceFixedIp;
  }

  public Boolean isDhcpEnabled() {
    return this.dhcpEnabled;
  }

  public void setDhcpEnabled(final Boolean dhcpEnabled) {
    this.dhcpEnabled = dhcpEnabled;
  }

  public Boolean isTlsEnabled() {
    return this.tlsEnabled;
  }

  public void setTlsEnabled(final Boolean tlsEnabled) {
    this.tlsEnabled = tlsEnabled;
  }

  public Integer getTlsPortNumber() {
    return this.tlsPortNumber;
  }

  public void setTlsPortNumber(final Integer tlsPortNumber) {
    this.tlsPortNumber = tlsPortNumber;
  }

  public String getCommonNameString() {
    return this.commonNameString;
  }

  public void setCommonNameString(final String commonNameString) {
    this.commonNameString = commonNameString;
  }

  public Integer getCommunicationTimeout() {
    return this.communicationTimeout;
  }

  public void setCommunicationTimeout(final Integer communicationTimeout) {
    this.communicationTimeout = communicationTimeout;
  }

  public Integer getCommunicationNumberOfRetries() {
    return this.communicationNumberOfRetries;
  }

  public void setCommunicationNumberOfRetries(final Integer communicationNumberOfRetries) {
    this.communicationNumberOfRetries = communicationNumberOfRetries;
  }

  public Integer getCommunicationPauseTimeBetweenConnectionTrials() {
    return this.communicationPauseTimeBetweenConnectionTrials;
  }

  public void setCommunicationPauseTimeBetweenConnectionTrials(
      final Integer communicationPauseTimeBetweenConnectionTrials) {
    this.communicationPauseTimeBetweenConnectionTrials =
        communicationPauseTimeBetweenConnectionTrials;
  }

  public String getOsgpIpAddres() {
    return this.osgpIpAddress;
  }

  public void setOsgpIpAddress(final String osgpIpAddress) {
    this.osgpIpAddress = osgpIpAddress;
  }

  public Integer getOsgpPortNumber() {
    return this.osgpPortNumber;
  }

  public void setOsgpPortNumber(final Integer osgpPortNumber) {
    this.osgpPortNumber = osgpPortNumber;
  }

  public String getNtpHost() {
    return this.ntpHost;
  }

  public void setNtpHost(final String ntpHost) {
    this.ntpHost = ntpHost;
  }

  public Boolean getNtpEnabled() {
    return this.ntpEnabled;
  }

  public void setNtpEnabled(final Boolean ntpEnabled) {
    this.ntpEnabled = ntpEnabled;
  }

  public Integer getNtpSyncInterval() {
    return this.ntpSyncInterval;
  }

  public void setNtpSyncInterval(final Integer ntpSyncInterval) {
    this.ntpSyncInterval = ntpSyncInterval;
  }

  public Boolean isTestButtonEnabled() {
    return this.testButtonEnabled;
  }

  public void setTestButtonEnabled(final Boolean testButtonEnabled) {
    this.testButtonEnabled = testButtonEnabled;
  }

  public Boolean isAutomaticSummerTimingEnabled() {
    return this.automaticSummerTimingEnabled;
  }

  public void setAutomaticSummerTimingEnabled(final Boolean automaticSummerTimingEnabled) {
    this.automaticSummerTimingEnabled = automaticSummerTimingEnabled;
  }

  public void setRelayLinking(final List<RelayMatrixDto> relayLinking) {
    this.relayLinking = relayLinking;
  }

  public Integer getAstroGateSunRiseOffset() {
    return this.astroGateSunRiseOffset;
  }

  public void setAstroGateSunRiseOffset(final Integer astroGateSunRiseOffset) {
    this.astroGateSunRiseOffset = astroGateSunRiseOffset;
  }

  public Integer getAstroGateSunSetOffset() {
    return this.astroGateSunSetOffset;
  }

  public void setAstroGateSunSetOffset(final Integer astroGateSunSetOffset) {
    this.astroGateSunSetOffset = astroGateSunSetOffset;
  }

  public Boolean isRelayRefreshing() {
    return this.relayRefreshing;
  }

  public void setRelayRefreshing(final Boolean relayRefreshing) {
    this.relayRefreshing = relayRefreshing;
  }

  public List<Integer> getSwitchingDelays() {
    return this.switchingDelays;
  }

  public void setSwitchingDelays(final List<Integer> switchingDelays) {
    this.switchingDelays = switchingDelays;
  }

  public List<RelayMatrixDto> getRelayLinking() {
    return this.relayLinking;
  }

  public ZonedDateTime getSummerTimeDetails() {
    return this.summerTimeDetails;
  }

  public void setSummerTimeDetails(final ZonedDateTime summerTimeDetails) {
    this.summerTimeDetails = summerTimeDetails;
  }

  public ZonedDateTime getWinterTimeDetails() {
    return this.winterTimeDetails;
  }

  public void setWinterTimeDetails(final ZonedDateTime winterTimeDetails) {
    this.winterTimeDetails = winterTimeDetails;
  }
}
