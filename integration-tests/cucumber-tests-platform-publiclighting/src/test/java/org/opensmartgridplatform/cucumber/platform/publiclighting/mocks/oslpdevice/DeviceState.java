// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.publiclighting.mocks.oslpdevice;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import lombok.Getter;
import lombok.Setter;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.oslp.Oslp;
import org.opensmartgridplatform.shared.infra.jms.MessageType;

public class DeviceState {

  @Getter private final String deviceUid;
  @Getter @Setter private Integer sequenceNumber = 0;

  private static final Integer SEQUENCE_NUMBER_MAXIMUM = 65535;

  private final ConcurrentMap<MessageType, ConcurrentLinkedQueue<Oslp.Message>> mockedResponsesMap =
      new ConcurrentHashMap<>();
  private final ConcurrentMap<MessageType, ConcurrentLinkedQueue<Oslp.Message>>
      receivedRequestsMap = new ConcurrentHashMap<>();

  public DeviceState(final String deviceUid) {
    this.deviceUid = deviceUid;
  }

  public Oslp.Message pollMockedResponse(final MessageType messageType)
      throws DeviceSimulatorException {
    return this.pollMessage(this.mockedResponsesMap, messageType);
  }

  public Oslp.Message pollReceivedRequest(final MessageType messageType)
      throws DeviceSimulatorException {
    return this.pollMessage(this.receivedRequestsMap, messageType);
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

  public void incrementSequenceNumber() {
    int increment = 1;

    final Object incrementObject =
        ScenarioContext.current().get(PlatformKeys.NUMBER_TO_ADD_TO_SEQUENCE_NUMBER);
    if (incrementObject != null) {
      final String numberToAddAsNextSequenceNumber = incrementObject.toString();
      increment = Integer.parseInt(numberToAddAsNextSequenceNumber);
    }

    int next = this.sequenceNumber + increment;
    if (next > SEQUENCE_NUMBER_MAXIMUM) {
      next -= SEQUENCE_NUMBER_MAXIMUM - 1;
    } else if (next < 0) {
      next = SEQUENCE_NUMBER_MAXIMUM - next * -1 + 1;
    }

    this.setSequenceNumber(next);
  }

  private ConcurrentLinkedQueue<Oslp.Message> getQueue(
      final Map<MessageType, ConcurrentLinkedQueue<Oslp.Message>> messageMap,
      final MessageType messageType) {
    return messageMap.computeIfAbsent(messageType, k -> new ConcurrentLinkedQueue<>());
  }

  private Oslp.Message pollMessage(
      final Map<MessageType, ConcurrentLinkedQueue<Oslp.Message>> messageMap,
      final MessageType messageType)
      throws DeviceSimulatorException {
    final Oslp.Message message = this.getQueue(messageMap, messageType).poll();
    if (message == null) {
      throw new DeviceSimulatorException(
          String.format("No message of type %s found for device %s", messageType, this.deviceUid));
    }

    return message;
  }
}
