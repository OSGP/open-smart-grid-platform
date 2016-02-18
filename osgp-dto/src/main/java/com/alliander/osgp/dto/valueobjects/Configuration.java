/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects;

import java.io.Serializable;
import java.util.List;

public class Configuration implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 8359276160483972289L;

    private final LightType lightType;

    private final DaliConfiguration daliConfiguration;

    private final RelayConfiguration relayConfiguration;

    private final Integer shortTermHistoryIntervalMinutes;

    private final Integer longTermHistoryInterval;

    private final LongTermIntervalType longTermHistoryIntervalType;

    private final LinkType preferredLinkType;

    private final MeterType meterType;

    private Integer timeSyncFrequency;

    private String deviceFixIpValue;

    private boolean isDhcpEnabled;

    private Integer communicationTimeout;

    private Integer communicationNumberOfRetries;

    private Integer communicationPauseTimeBetweenConnectionTrials;

    private String ospgIpAddress;

    private Integer osgpPortNumber;

    private boolean isTestButtonEnabled;

    private boolean isAutomaticSummerTimingEnabled;

    private Integer astroGateSunRiseOffset;

    private Integer astroGateSunSetOffset;

    private List<Integer> switchingDelay;

    private List<RelayMatrix> relayLinking;

    private boolean relayRefreshing;

    private String summerTimeDetails;

    private String winterTimeDetails;

    public Configuration(final LightType lightType, final DaliConfiguration daliConfiguration,
            final RelayConfiguration relayConfiguration, final Integer shortTermHistoryIntervalMinutes,
            final LinkType preferredLinkType, final MeterType meterType, final Integer longTermHistoryInterval,
            final LongTermIntervalType longTermHistoryIntervalType) {
        this.lightType = lightType;
        this.daliConfiguration = daliConfiguration;
        this.relayConfiguration = relayConfiguration;
        this.shortTermHistoryIntervalMinutes = shortTermHistoryIntervalMinutes;
        this.preferredLinkType = preferredLinkType;
        this.meterType = meterType;
        this.longTermHistoryInterval = longTermHistoryInterval;
        this.longTermHistoryIntervalType = longTermHistoryIntervalType;
    }

    public MeterType getMeterType() {
        return this.meterType;
    }

    public LightType getLightType() {
        return this.lightType;
    }

    public DaliConfiguration getDaliConfiguration() {
        return this.daliConfiguration;
    }

    public RelayConfiguration getRelayConfiguration() {
        return this.relayConfiguration;
    }

    public Integer getShortTermHistoryIntervalMinutes() {
        return this.shortTermHistoryIntervalMinutes;
    }

    public LinkType getPreferredLinkType() {
        return this.preferredLinkType;
    }

    public Integer getLongTermHistoryInterval() {
        return this.longTermHistoryInterval;
    }

    public LongTermIntervalType getLongTermHistoryIntervalType() {
        return this.longTermHistoryIntervalType;
    }

    public Integer getTimeSyncFrequency() {
        return this.timeSyncFrequency;
    }

    public void setTimeSyncFrequency(final Integer timeSyncFrequency) {
        this.timeSyncFrequency = timeSyncFrequency;
    }

    public String getDeviceFixIpValue() {
        return this.deviceFixIpValue;
    }

    public void setDeviceFixIpValue(final String deviceFixIpValue) {
        this.deviceFixIpValue = deviceFixIpValue;
    }

    public boolean isDhcpEnabled() {
        return this.isDhcpEnabled;
    }

    public void setDhcpEnabled(final boolean isDhcpEnabled) {
        this.isDhcpEnabled = isDhcpEnabled;
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

    public void setCommunicationPauseTimeBetweenConnectionTrials(final Integer communicationPauseTimeBetweenConnectionTrials) {
        this.communicationPauseTimeBetweenConnectionTrials = communicationPauseTimeBetweenConnectionTrials;
    }

    public String getOspgIpAddress() {
        return this.ospgIpAddress;
    }

    public void setOspgIpAddress(final String ospgIpAddress) {
        this.ospgIpAddress = ospgIpAddress;
    }

    public Integer getOsgpPortNumber() {
        return this.osgpPortNumber;
    }

    public void setOsgpPortNumber(final Integer osgpPortNumber) {
        this.osgpPortNumber = osgpPortNumber;
    }

    public boolean isTestButtonEnabled() {
        return this.isTestButtonEnabled;
    }

    public void setTestButtonEnabled(final boolean isTestButtonEnabled) {
        this.isTestButtonEnabled = isTestButtonEnabled;
    }

    public boolean isAutomaticSummerTimingEnabled() {
        return this.isAutomaticSummerTimingEnabled;
    }

    public void setAutomaticSummerTimingEnabled(final boolean isAutomaticSummerTimingEnabled) {
        this.isAutomaticSummerTimingEnabled = isAutomaticSummerTimingEnabled;
    }

    public void setRelayLinking(final List<RelayMatrix> relayLinking) {
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

    public boolean isRelayRefreshing() {
        return this.relayRefreshing;
    }

    public void setRelayRefreshing(final boolean relayRefreshing) {
        this.relayRefreshing = relayRefreshing;
    }

    public String getSummerTimeDetails() {
        return this.summerTimeDetails;
    }

    public void setSummerTimeDetails(final String summerTimeDetails) {
        this.summerTimeDetails = summerTimeDetails;
    }

    public String getWinterTimeDetails() {
        return this.winterTimeDetails;
    }

    public void setWinterTimeDetails(final String winterTimeDetails) {
        this.winterTimeDetails = winterTimeDetails;
    }

    public List<Integer> getSwitchingDelay() {
        return this.switchingDelay;
    }

    public void setSwitchingDelay(final List<Integer> switchingDelay) {
        this.switchingDelay = switchingDelay;
    }

    public List<RelayMatrix> getRelayLinking() {
        return this.relayLinking;
    }

}
