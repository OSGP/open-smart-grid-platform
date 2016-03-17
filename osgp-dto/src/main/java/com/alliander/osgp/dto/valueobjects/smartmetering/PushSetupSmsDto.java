/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PushSetupSmsDto implements Serializable {

    private static final char TAB_CHAR = '\t';

    private static final long serialVersionUID = -3541154908239512383L;

    private final CosemObisCodeDto logicalName;
    private final List<CosemObjectDefinitionDto> pushObjectList;
    private final SendDestinationAndMethodDto sendDestinationAndMethod;
    private final List<WindowElementDto> communicationWindow;
    private final Integer randomisationStartInterval;
    private final Integer numberOfRetries;
    private final Integer repetitionDelay;

    public static class Builder {

        private CosemObisCodeDto logicalName;
        private List<CosemObjectDefinitionDto> pushObjectList;
        private SendDestinationAndMethodDto sendDestinationAndMethod;
        private List<WindowElementDto> communicationWindow;
        private Integer randomisationStartInterval;
        private Integer numberOfRetries;
        private Integer repetitionDelay;

        public PushSetupSmsDto build() {
            return new PushSetupSmsDto(this.logicalName, this.pushObjectList, this.sendDestinationAndMethod,
                    this.communicationWindow, this.randomisationStartInterval, this.numberOfRetries,
                    this.repetitionDelay);
        }

        public Builder logicalName(final CosemObisCodeDto logicalName) {
            this.logicalName = logicalName;
            return this;
        }

        public Builder pushObjectList(final List<CosemObjectDefinitionDto> pushObjectList) {
            this.pushObjectList = pushObjectList;
            return this;
        }

        public Builder sendDestinationAndMethod(final SendDestinationAndMethodDto sendDestinationAndMethod) {
            this.sendDestinationAndMethod = sendDestinationAndMethod;
            return this;
        }

        public Builder communicationWindow(final List<WindowElementDto> communicationWindow) {
            this.communicationWindow = communicationWindow;
            return this;
        }

        public Builder randomisationStartInterval(final Integer randomisationStartInterval) {
            this.randomisationStartInterval = randomisationStartInterval;
            return this;
        }

        public Builder numberOfRetries(final Integer numberOfRetries) {
            this.numberOfRetries = numberOfRetries;
            return this;
        }

        public Builder repetitionDelay(final Integer repetitionDelay) {
            this.repetitionDelay = repetitionDelay;
            return this;
        }
    }

    private PushSetupSmsDto(final CosemObisCodeDto logicalName, final List<CosemObjectDefinitionDto> pushObjectList,
            final SendDestinationAndMethodDto sendDestinationAndMethod, final List<WindowElementDto> communicationWindow,
            final Integer randomisationStartInterval, final Integer numberOfRetries, final Integer repetitionDelay) {
        this.checkRandomisationStartInterval(randomisationStartInterval);
        this.checkNumberOfRetries(numberOfRetries);
        this.checkRepetitionDelay(repetitionDelay);
        this.logicalName = logicalName;
        if (pushObjectList == null) {
            this.pushObjectList = null;
        } else {
            this.pushObjectList = new ArrayList<>(pushObjectList);
        }
        this.sendDestinationAndMethod = sendDestinationAndMethod;
        if (communicationWindow == null) {
            this.communicationWindow = null;
        } else {
            this.communicationWindow = new ArrayList<>(communicationWindow);
        }
        this.randomisationStartInterval = randomisationStartInterval;
        this.numberOfRetries = numberOfRetries;
        this.repetitionDelay = repetitionDelay;
    }

    private void checkRandomisationStartInterval(final Integer randomisationStartInterval) {
        if (randomisationStartInterval == null) {
            return;
        }
        if (randomisationStartInterval < 0 || randomisationStartInterval > 0xFFFF) {
            throw new IllegalArgumentException("randomisationStartInterval not in [0..65535]");
        }
    }

    private void checkNumberOfRetries(final Integer numberOfRetries) {
        if (numberOfRetries == null) {
            return;
        }
        if (numberOfRetries < 0 || numberOfRetries > 0xFF) {
            throw new IllegalArgumentException("numberOfRetries not in [0..255]");
        }
    }

    private void checkRepetitionDelay(final Integer repetitionDelay) {
        if (repetitionDelay == null) {
            return;
        }
        if (repetitionDelay < 0 || repetitionDelay > 0xFFFF) {
            throw new IllegalArgumentException("repetitionDelay not in [0..65535]");
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PushSetupAlarm[").append(System.lineSeparator()).append(TAB_CHAR);
        this.appendToString(sb, "logicalName", this.logicalName, true);
        this.appendToString(sb, "pushObjectList", this.pushObjectList, true);
        this.appendToString(sb, "sendDestinationAndMethod", this.sendDestinationAndMethod, true);
        this.appendToString(sb, "communicationWindow", this.communicationWindow, true);
        this.appendToString(sb, "randomisationStartInterval", this.randomisationStartInterval, true);
        this.appendToString(sb, "numberOfRetries", this.numberOfRetries, true);
        this.appendToString(sb, "repetitionDelay", this.repetitionDelay, false);
        return sb.append(']').toString();
    }

    private StringBuilder appendToString(final StringBuilder sb, final String fieldName, final Object fieldValue,
            final boolean hasNext) {
        sb.append(fieldName).append('=').append(fieldValue).append(System.lineSeparator());
        if (hasNext) {
            sb.append(TAB_CHAR);
        }
        return sb;
    }

    public boolean hasLogicalName() {
        return this.logicalName != null;
    }

    public CosemObisCodeDto getLogicalName() {
        return this.logicalName;
    }

    public boolean hasPushObjectList() {
        return this.pushObjectList != null;
    }

    public List<CosemObjectDefinitionDto> getPushObjectList() {
        if (this.pushObjectList == null) {
            return null;
        }
        return new ArrayList<>(this.pushObjectList);
    }

    public boolean hasSendDestinationAndMethod() {
        return this.sendDestinationAndMethod != null;
    }

    public SendDestinationAndMethodDto getSendDestinationAndMethod() {
        return this.sendDestinationAndMethod;
    }

    public boolean hasCommunicationWindow() {
        return this.communicationWindow != null;
    }

    public List<WindowElementDto> getCommunicationWindow() {
        if (this.communicationWindow == null) {
            return null;
        }
        return new ArrayList<>(this.communicationWindow);
    }

    public boolean hasRandomisationStartInterval() {
        return this.randomisationStartInterval != null;
    }

    public Integer getRandomisationStartInterval() {
        return this.randomisationStartInterval;
    }

    public boolean hasNumberOfRetries() {
        return this.numberOfRetries != null;
    }

    public Integer getNumberOfRetries() {
        return this.numberOfRetries;
    }

    public boolean hasRepetitionDelay() {
        return this.repetitionDelay != null;
    }

    public Integer getRepetitionDelay() {
        return this.repetitionDelay;
    }
}
