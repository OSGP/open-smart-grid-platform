/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects;

import java.io.Serializable;

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
}
