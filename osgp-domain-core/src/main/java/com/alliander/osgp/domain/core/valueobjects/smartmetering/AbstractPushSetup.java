/**
/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class AbstractPushSetup implements Serializable {
    private static final long serialVersionUID = -1080411684155651756L;
    private static final char TAB_CHAR = '\t';

    private final CosemObisCode logicalName;
    private final List<CosemObjectDefinition> pushObjectList;
    private final SendDestinationAndMethod sendDestinationAndMethod;
    private final List<WindowElement> communicationWindow;
    private final Integer randomisationStartInterval;
    private final Integer numberOfRetries;
    private final Integer repetitionDelay;

    AbstractPushSetup(final AbstractPushSetup abstractPushSetup) {
        this.logicalName = abstractPushSetup.getLogicalName();
        this.pushObjectList = abstractPushSetup.getPushObjectList();
        this.sendDestinationAndMethod = abstractPushSetup.getSendDestinationAndMethod();
        this.communicationWindow = abstractPushSetup.getCommunicationWindow();
        this.randomisationStartInterval = abstractPushSetup.getRandomisationStartInterval();
        this.numberOfRetries = abstractPushSetup.getNumberOfRetries();
        this.repetitionDelay = abstractPushSetup.getRepetitionDelay();
    }

    private AbstractPushSetup(final AbstractBuilder builder) {
        this.logicalName = builder.logicalName;
        this.pushObjectList = builder.pushObjectList;
        this.sendDestinationAndMethod = builder.sendDestinationAndMethod;
        this.communicationWindow = builder.communicationWindow;
        this.randomisationStartInterval = builder.randomisationStartInterval;
        this.numberOfRetries = builder.numberOfRetries;
        this.repetitionDelay = builder.repetitionDelay;
    }

    public static class AbstractBuilder {

        protected CosemObisCode logicalName;
        protected List<CosemObjectDefinition> pushObjectList;
        protected SendDestinationAndMethod sendDestinationAndMethod;
        protected List<WindowElement> communicationWindow;
        protected Integer randomisationStartInterval;
        protected Integer numberOfRetries;
        protected Integer repetitionDelay;

        public AbstractPushSetup build() {
            return new AbstractPushSetup(this);
        }

        public AbstractBuilder withLogicalName(final CosemObisCode logicalName) {
            this.logicalName = logicalName;
            return this;
        }

        public AbstractBuilder withPushObjectList(final List<CosemObjectDefinition> pushObjectList) {
            if (pushObjectList == null) {
                this.pushObjectList = null;
            } else {
                this.pushObjectList = new ArrayList<>(pushObjectList);
            }
            return this;
        }

        public AbstractBuilder withSendDestinationAndMethod(final SendDestinationAndMethod sendDestinationAndMethod) {
            this.sendDestinationAndMethod = sendDestinationAndMethod;
            return this;
        }

        public AbstractBuilder withCommunicationWindow(final List<WindowElement> communicationWindow) {
            if (communicationWindow == null) {
                this.communicationWindow = null;
            } else {
                this.communicationWindow = new ArrayList<>(communicationWindow);
            }
            return this;
        }

        public AbstractBuilder withRandomisationStartInterval(final Integer randomisationStartInterval) {
            AbstractPushSetup.checkRandomisationStartInterval(randomisationStartInterval);
            this.randomisationStartInterval = randomisationStartInterval;
            return this;
        }

        public AbstractBuilder withNumberOfRetries(final Integer numberOfRetries) {
            AbstractPushSetup.checkNumberOfRetries(numberOfRetries);
            this.numberOfRetries = numberOfRetries;
            return this;
        }

        public AbstractBuilder withRepetitionDelay(final Integer repetitionDelay) {
            AbstractPushSetup.checkRepetitionDelay(repetitionDelay);
            this.repetitionDelay = repetitionDelay;
            return this;
        }
    }

    public static AbstractBuilder newBuilder() {
        return new AbstractBuilder();
    }

    private static void checkRandomisationStartInterval(final Integer randomisationStartInterval) {
        if (randomisationStartInterval == null) {
            return;
        }
        if (randomisationStartInterval < 0 || randomisationStartInterval > 0xFFFF) {
            throw new IllegalArgumentException("randomisationStartInterval not in [0..65535]");
        }
    }

    private static void checkNumberOfRetries(final Integer numberOfRetries) {
        if (numberOfRetries == null) {
            return;
        }
        if (numberOfRetries < 0 || numberOfRetries > 0xFF) {
            throw new IllegalArgumentException("numberOfRetries not in [0..255]");
        }
    }

    private static void checkRepetitionDelay(final Integer repetitionDelay) {
        if (repetitionDelay == null) {
            return;
        }
        if (repetitionDelay < 0 || repetitionDelay > 0xFFFF) {
            throw new IllegalArgumentException("repetitionDelay not in [0..65535]");
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(this.getClass().getSimpleName()).append("[")
                .append(System.lineSeparator()).append(TAB_CHAR);
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

    public CosemObisCode getLogicalName() {
        return this.logicalName;
    }

    public boolean hasPushObjectList() {
        return this.pushObjectList != null;
    }

    public List<CosemObjectDefinition> getPushObjectList() {
        if (this.pushObjectList == null) {
            return null;
        }
        return new ArrayList<>(this.pushObjectList);
    }

    public boolean hasSendDestinationAndMethod() {
        return this.sendDestinationAndMethod != null;
    }

    public SendDestinationAndMethod getSendDestinationAndMethod() {
        return this.sendDestinationAndMethod;
    }

    public boolean hasCommunicationWindow() {
        return this.communicationWindow != null;
    }

    public List<WindowElement> getCommunicationWindow() {
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
