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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.oslp.Oslp;
import org.opensmartgridplatform.shared.infra.jms.MessageType;

import lombok.Getter;
import lombok.Setter;

public class DeviceState {

    @Getter
    private final String deviceUID;
    @Getter @Setter
    private Integer sequenceNumber = 0;

    private static final Integer SEQUENCE_NUMBER_MAXIMUM = 65535;

    private final ConcurrentMap<MessageType, ConcurrentLinkedQueue<Oslp.Message>> mockedResponsesMap = new ConcurrentHashMap<>();
    private final ConcurrentMap<MessageType, ConcurrentLinkedQueue<Oslp.Message>> receivedRequestsMap = new ConcurrentHashMap<>();

    public DeviceState(final String deviceUID) {
        this.deviceUID = deviceUID;
    }

    public Oslp.Message getMockedResponse(final MessageType messageType) throws DeviceSimulatorException {
        return this.getFromMap(this.mockedResponsesMap, messageType);
    }

    public Oslp.Message getReceivedRequest(final MessageType messageType) throws DeviceSimulatorException {
        return this.getFromMap(this.receivedRequestsMap, messageType);
    }

    public void addMockedResponse(final MessageType messageType, final Oslp.Message message) {
        this.getQueue(this.mockedResponsesMap, messageType).add(message);
    }

    public void addReceivedRequest(final MessageType messageType, final Oslp.Message message) {
        this.getQueue(this.receivedRequestsMap, messageType).add(message);
    }

    public boolean hasMockedResponses(final MessageType messageType) {
        return !this.getQueue(this.mockedResponsesMap, messageType).isEmpty();
    }

    public boolean hasReceivedRequests(final MessageType messageType) {
        return !this.getQueue(this.receivedRequestsMap, messageType).isEmpty();
    }

    public int mockedResponsesQueued(final MessageType messageType) {
        return this.getQueue(this.receivedRequestsMap, messageType).size();
    }

    public void incrementSequenceNumber() {
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

        this.sequenceNumber = next;
    }

    private ConcurrentLinkedQueue<Oslp.Message> getQueue(final Map<MessageType, ConcurrentLinkedQueue<Oslp.Message>> messageMap, final MessageType messageType) {
        messageMap.computeIfAbsent(messageType, k -> new ConcurrentLinkedQueue<>());
        return messageMap.get(messageType);
    }

    private Oslp.Message getFromMap(final Map<MessageType, ConcurrentLinkedQueue<Oslp.Message>> messageMap, final MessageType messageType)
            throws DeviceSimulatorException {
        try {
            return this.getQueue(messageMap, messageType).poll();
        } catch (final EmptyStackException e) {
            throw new DeviceSimulatorException(String.format("No message of type %s found for device %s", messageType, this.deviceUID), e);
        }
    }

}
