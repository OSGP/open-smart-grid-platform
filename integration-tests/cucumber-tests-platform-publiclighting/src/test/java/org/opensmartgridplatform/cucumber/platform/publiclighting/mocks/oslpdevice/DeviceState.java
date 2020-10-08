/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.publiclighting.mocks.oslpdevice;

import java.util.EmptyStackException;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.oslp.Oslp;
import org.opensmartgridplatform.shared.infra.jms.MessageType;

import lombok.Getter;

public class DeviceState {

    @Getter
    private final String deviceUID;

    // Device settings
    @Getter
    private Integer sequenceNumber = 0;

    private static final Integer SEQUENCE_NUMBER_MAXIMUM = 65535;

    private final ConcurrentMap<MessageType, Stack<Oslp.Message>> mockedResponsesMap = new ConcurrentHashMap<>();
    private final ConcurrentMap<MessageType, Stack<Oslp.Message>> receivedRequestsMap = new ConcurrentHashMap<>();

    public DeviceState(final String deviceUID) {
        this.deviceUID = deviceUID;
    }

    public Oslp.Message getResponse(final MessageType messageType) throws DeviceSimulatorException {
        return this.getFromMap(this.mockedResponsesMap, messageType);
    }

    public Oslp.Message getRequest(final MessageType messageType) throws DeviceSimulatorException {
        return this.getFromMap(this.receivedRequestsMap, messageType);
    }

    public void addResponse(final MessageType messageType, final Oslp.Message message) {
        this.getStack(this.mockedResponsesMap, messageType).push(message);
    }

    public void addReceivedRequest(final MessageType messageType, final Oslp.Message message) {
        this.getStack(this.receivedRequestsMap, messageType).add(message);
    }

    public boolean hasResponses(final MessageType messageType) {
        return !this.getStack(this.mockedResponsesMap, messageType).empty();
    }

    public boolean hasRequests(final MessageType messageType) {
        return !this.getStack(this.receivedRequestsMap, messageType).empty();
    }

    public int incrementSequenceNumber() {
        int numberToAddToSequenceNumberValue = 1;

        if (ScenarioContext.current().get(PlatformKeys.NUMBER_TO_ADD_TO_SEQUENCE_NUMBER) != null) {
            final String numberToAddAsNextSequenceNumber = ScenarioContext.current()
                    .get(PlatformKeys.NUMBER_TO_ADD_TO_SEQUENCE_NUMBER)
                    .toString();
            if (!numberToAddAsNextSequenceNumber.isEmpty()) {
                numberToAddToSequenceNumberValue = Integer.parseInt(numberToAddAsNextSequenceNumber);
            }
        }
        int next = this.sequenceNumber + numberToAddToSequenceNumberValue;
        if (next > SEQUENCE_NUMBER_MAXIMUM) {
            final int sequenceNumberMaximumCross = next - SEQUENCE_NUMBER_MAXIMUM;
            if (sequenceNumberMaximumCross >= 1) {
                next = sequenceNumberMaximumCross - 1;
            }
        } else if (next < 0) {
            final int sequenceNumberMaximumCross = next * -1;
            if (sequenceNumberMaximumCross >= 1) {
                next = SEQUENCE_NUMBER_MAXIMUM - sequenceNumberMaximumCross + 1;
            }
        }
        return this.sequenceNumber = next;
    }

    private Stack<Oslp.Message> getStack(final Map<MessageType, Stack<Oslp.Message>> messageMap, final MessageType messageType) {
        if (messageMap.get(messageType) == null) {
            messageMap.put(messageType, new Stack<>());
        }
        return messageMap.get(messageType);
    }

    private Oslp.Message getFromMap(final Map<MessageType, Stack<Oslp.Message>> messageMap, final MessageType messageType)
            throws DeviceSimulatorException {
        try {
            return this.getStack(messageMap, messageType).pop();
        } catch (final EmptyStackException e) {
            throw new DeviceSimulatorException(String.format("No message of type %s found for device %s", messageType, this.deviceUID), e);
        }
    }

}
