/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.util.ArrayList;
import java.util.List;

public class AbstractPushSetupDtoBuilder {
    private final CosemObisCodeDto logicalName;
    private final List<CosemObjectDefinitionDto> pushObjectList;
    private final SendDestinationAndMethodDto sendDestinationAndMethod;
    private final List<WindowElementDto> communicationWindow;
    private final Integer randomisationStartInterval;
    private final Integer numberOfRetries;
    private final Integer repetitionDelay;

    private AbstractPushSetupDtoBuilder(final Builder builder) {
        this.logicalName = builder.logicalName;
        this.pushObjectList = builder.pushObjectList;
        this.sendDestinationAndMethod = builder.sendDestinationAndMethod;
        this.communicationWindow = builder.communicationWindow;
        this.randomisationStartInterval = builder.randomisationStartInterval;
        this.numberOfRetries = builder.numberOfRetries;
        this.repetitionDelay = builder.repetitionDelay;
    }

    public static class Builder {

        private CosemObisCodeDto logicalName = null;
        private List<CosemObjectDefinitionDto> pushObjectList = null;
        private SendDestinationAndMethodDto sendDestinationAndMethod = null;
        private List<WindowElementDto> communicationWindow = null;
        private Integer randomisationStartInterval = null;
        private Integer numberOfRetries = null;
        private Integer repetitionDelay = null;

        public AbstractPushSetupDtoBuilder build() {
            return new AbstractPushSetupDtoBuilder(this);
        }

        public Builder withLogicalName(final CosemObisCodeDto logicalName) {
            this.logicalName = logicalName;
            return this;
        }

        public Builder withPushObjectList(final List<CosemObjectDefinitionDto> pushObjectList) {
            if (pushObjectList == null) {
                this.pushObjectList = null;
            } else {
                this.pushObjectList = new ArrayList<>(pushObjectList);
            }
            return this;
        }

        public Builder withSendDestinationAndMethod(final SendDestinationAndMethodDto sendDestinationAndMethod) {
            this.sendDestinationAndMethod = sendDestinationAndMethod;
            return this;
        }

        public Builder withCommunicationWindow(final List<WindowElementDto> communicationWindow) {
            if (communicationWindow == null) {
                this.communicationWindow = null;
            } else {
                this.communicationWindow = new ArrayList<>(communicationWindow);
            }
            this.communicationWindow = communicationWindow;
            return this;
        }

        public Builder withRandomisationStartInterval(final Integer randomisationStartInterval) {
            AbstractPushSetupDto.checkRandomisationStartInterval(randomisationStartInterval);
            this.randomisationStartInterval = randomisationStartInterval;
            return this;
        }

        public Builder withNumberOfRetries(final Integer numberOfRetries) {
            AbstractPushSetupDto.checkNumberOfRetries(numberOfRetries);
            this.numberOfRetries = numberOfRetries;
            return this;
        }

        public Builder withRepetitionDelay(final Integer repetitionDelay) {
            AbstractPushSetupDto.checkRepetitionDelay(repetitionDelay);
            this.repetitionDelay = repetitionDelay;
            return this;
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public CosemObisCodeDto getLogicalName() {
        return this.logicalName;
    }

    public List<CosemObjectDefinitionDto> getPushObjectList() {
        return this.pushObjectList;
    }

    public SendDestinationAndMethodDto getSendDestinationAndMethod() {
        return this.sendDestinationAndMethod;
    }

    public List<WindowElementDto> getCommunicationWindow() {
        return this.communicationWindow;
    }

    public Integer getRandomisationStartInterval() {
        return this.randomisationStartInterval;
    }

    public Integer getNumberOfRetries() {
        return this.numberOfRetries;
    }

    public Integer getRepetitionDelay() {
        return this.repetitionDelay;
    }

}
