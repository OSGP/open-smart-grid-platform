// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.webdevicesimulator.service;

import com.google.protobuf.ByteString;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.text.MessageFormat;
import java.util.Random;
import javax.annotation.Resource;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.opensmartgridplatform.oslp.Oslp;
import org.opensmartgridplatform.oslp.Oslp.DeviceType;
import org.opensmartgridplatform.oslp.Oslp.EventNotification;
import org.opensmartgridplatform.oslp.Oslp.Message;
import org.opensmartgridplatform.oslp.OslpEnvelope;
import org.opensmartgridplatform.webdevicesimulator.application.services.DeviceManagementService;
import org.opensmartgridplatform.webdevicesimulator.application.services.OslpLogService;
import org.opensmartgridplatform.webdevicesimulator.domain.entities.Device;
import org.opensmartgridplatform.webdevicesimulator.domain.entities.DeviceMessageStatus;
import org.opensmartgridplatform.webdevicesimulator.domain.valueobjects.Event;
import org.opensmartgridplatform.webdevicesimulator.domain.valueobjects.ProtocolType;
import org.opensmartgridplatform.webdevicesimulator.exceptions.DeviceSimulatorException;
import org.opensmartgridplatform.webdevicesimulator.service.OslpChannelHandler.OutOfSequenceEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

public class RegisterDevice {
  private static final Logger LOGGER = LoggerFactory.getLogger(RegisterDevice.class);

  @Autowired private DeviceManagementService deviceManagementService;

  @Autowired private OslpLogService oslpLogService;

  @Resource private OslpChannelHandler oslpChannelHandler;

  @Resource private String oslpAddressServer;

  @Resource private int oslpPortClient;

  @Resource private int oslpElsterPortClient;

  @Resource private String oslpSignatureProvider;

  @Resource private PrivateKey privateKey;

  @Resource private String oslpSignature;

  private String currentTime;

  private String errorMessage;

  private final Random byteGenerator = new SecureRandom();

  public DeviceMessageStatus sendRegisterDeviceCommand(
      final long deviceId, final Boolean hasSchedule) {

    // Find device.
    final Device device = this.deviceManagementService.findDevice(deviceId);
    if (device == null) {
      // Set the DeviceMessageStatus NOT_FOUND as the Device is not found.
      return DeviceMessageStatus.NOT_FOUND;
    }

    this.errorMessage = "";

    try {
      // Generate random sequence number and random device number.
      final Integer sequenceNumber = device.doGenerateRandomNumber();
      final Integer randomDevice = device.doGenerateRandomNumber();

      // Create registration message.
      final OslpEnvelope oslpRequest =
          this.createEnvelopeBuilder(device.getDeviceUid(), sequenceNumber)
              .withPayloadMessage(
                  Message.newBuilder()
                      .setRegisterDeviceRequest(
                          Oslp.RegisterDeviceRequest.newBuilder()
                              .setDeviceIdentification(device.getDeviceIdentification())
                              .setIpAddress(
                                  ByteString.copyFrom(
                                      InetAddress.getByName(device.getIpAddress()).getAddress()))
                              .setDeviceType(
                                  device.getDeviceType().isEmpty()
                                      ? DeviceType.PSLD
                                      : DeviceType.valueOf(device.getDeviceType()))
                              .setHasSchedule(hasSchedule)
                              .setRandomDevice(randomDevice))
                      .build())
              .build();

      // Write outgoing request to log.
      this.writeOslpLogItem(oslpRequest, device, false);

      final OslpEnvelope response = this.sendRequest(device, oslpRequest);

      // Write incoming response to log.
      this.writeOslpLogItem(response, device, true);

      this.currentTime = response.getPayloadMessage().getRegisterDeviceResponse().getCurrentTime();

      // Get the sequence number from the response envelope and check it.
      this.checkSequenceNumber(response.getSequenceNumber(), sequenceNumber);

      // Get the two random numbers and check them both.
      this.checkRandomDeviceAndRandomPlatform(
          randomDevice,
          response.getPayloadMessage().getRegisterDeviceResponse().getRandomDevice(),
          response.getPayloadMessage().getRegisterDeviceResponse().getRandomPlatform());

      // Set the sequence number and persist it.
      device.setSequenceNumber(sequenceNumber);

      // Get the two random numbers and persist them both.
      device.setRandomDevice(
          response.getPayloadMessage().getRegisterDeviceResponse().getRandomDevice());
      device.setRandomPlatform(
          response.getPayloadMessage().getRegisterDeviceResponse().getRandomPlatform());

      // Save the entity.
      this.deviceManagementService.updateDevice(device);

      // Set the DeviceMessageStatus OK as the registration is successful.
      return DeviceMessageStatus.OK;
    } catch (final UnknownHostException ex) {
      LOGGER.error("incorrect IP address format", ex);
    } catch (final Exception e) {
      LOGGER.error("register device exception", e);
      this.errorMessage = e.getMessage();
      // Set the DeviceMessageStatus FAILURE as the registration is NOT
      // successful.
      return DeviceMessageStatus.FAILURE;
    }

    return DeviceMessageStatus.NOT_FOUND;
  }

  public DeviceMessageStatus sendConfirmDeviceRegistrationCommand(final long deviceId) {
    // Find device.
    Device device = this.deviceManagementService.findDevice(deviceId);
    if (device == null) {
      // Set the DeviceMessageStatus NOT_FOUND as the device is not found.
      return DeviceMessageStatus.NOT_FOUND;
    }
    this.errorMessage = "";

    try {
      final Integer sequenceNumber = device.doGetNextSequence();
      // Create registration confirmation message.
      final OslpEnvelope oslpRequest =
          this.createEnvelopeBuilder(device.getDeviceUid(), sequenceNumber)
              .withPayloadMessage(
                  Message.newBuilder()
                      .setConfirmRegisterDeviceRequest(
                          Oslp.ConfirmRegisterDeviceRequest.newBuilder()
                              .setRandomDevice(device.getRandomDevice())
                              .setRandomPlatform(device.getRandomPlatform()))
                      .build())
              .build();

      // Write outgoing request to log.
      this.writeOslpLogItem(oslpRequest, device, false);

      final OslpEnvelope response = this.sendRequest(device, oslpRequest);

      // Write incoming response to log.
      this.writeOslpLogItem(response, device, true);

      // Get the sequence number from the response envelope and check it.
      this.checkSequenceNumber(response.getSequenceNumber(), sequenceNumber);

      // Get the two random numbers and check them both.
      this.checkRandomDeviceAndRandomPlatform(
          device.getRandomDevice(),
          response.getPayloadMessage().getConfirmRegisterDeviceResponse().getRandomDevice(),
          device.getRandomPlatform(),
          response.getPayloadMessage().getConfirmRegisterDeviceResponse().getRandomPlatform());

      // Successful.
      device.setSequenceNumber(sequenceNumber);
      device = this.deviceManagementService.updateDevice(device);

      // Check if there has been an out of sequence security event.
      OutOfSequenceEvent outOfSequenceEvent =
          this.oslpChannelHandler.hasOutOfSequenceEventForDevice(device.getId());
      while (outOfSequenceEvent != null) {
        // An event has occurred, send
        // SECURITY_EVENTS_OUT_OF_SEQUENCE_VALUE event notification.
        this.sendEventNotificationCommand(
            outOfSequenceEvent.getDeviceId(),
            Oslp.Event.SECURITY_EVENTS_OUT_OF_SEQUENCE_VALUE,
            "out of sequence event occurred at time stamp: "
                + outOfSequenceEvent.getTimestamp().toString()
                + " for request: "
                + outOfSequenceEvent.getRequest(),
            null);

        // Check if there has been another event, this will return null
        // if no more events are present in the list.
        outOfSequenceEvent = this.oslpChannelHandler.hasOutOfSequenceEventForDevice(device.getId());
      }

      // Set the DeviceMessageStatus OK as the confirm registration is
      // successful.
      return DeviceMessageStatus.OK;
    } catch (final Exception e) {
      LOGGER.error("confirm device registration exception", e);
      this.errorMessage = e.getMessage();

      // Set the DeviceMessageStatus FAILURE as the confirm registration
      // is NOT successful.
      return DeviceMessageStatus.FAILURE;
    }
  }

  private String getFormattedCurrentTimestamp() {
    final String format = "yyyyMMddHHmmss";
    final DateTime dateTime = DateTime.now().toDateTime(DateTimeZone.UTC);
    final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(format);
    final String timestamp = dateTimeFormatter.print(dateTime);
    LOGGER.info(
        "generated timestamp for EventNotificationRequest: {} using format: {}", timestamp, format);
    return timestamp;
  }

  private OslpEnvelope createEventNotificationRequest(
      final Device device, final int sequenceNumber, final Event event) {
    final String deviceUid = device.getDeviceUid();
    final Oslp.Event oslpEvent = event.getOslpEvent();
    final String description = event.getDescription();
    final Integer index = event.getIndex();
    final String timestamp = event.getTimestamp();
    final boolean hasTimestamp = event.hasTimestamp();

    // Create an event notification depending on device protocol (for now
    // with 1 event).

    Oslp.EventNotification eventNotification = null;
    if (device.getProtocol().equals(ProtocolType.OSLP.toString())) {
      eventNotification =
          EventNotification.newBuilder()
              .setEvent(oslpEvent)
              .setDescription(description == null ? "" : description)
              .setIndex(ByteString.copyFrom(new byte[] {index == null ? 0 : index.byteValue()}))
              .build();
    } else if (device.getProtocol().equals(ProtocolType.OSLP_ELSTER.toString())) {
      final Oslp.EventNotification.Builder builder = EventNotification.newBuilder();
      builder.setEvent(oslpEvent);
      if (StringUtils.isNotEmpty(description)) {
        builder.setDescription(description);
      }
      if (index != null) {
        builder.setIndex(ByteString.copyFrom(new byte[] {index.byteValue()}));
      }
      if (timestamp != null && hasTimestamp) {
        builder.setTimestamp(timestamp);
      }
      eventNotification = builder.build();
    }
    Assert.notNull(
        eventNotification,
        "Failed to create EventNotification. Is the protocol for the simulated device supported?");

    // Create event notification request.
    final Oslp.EventNotificationRequest eventNotificationRequest =
        Oslp.EventNotificationRequest.newBuilder().addNotifications(eventNotification).build();

    return this.createEnvelopeBuilder(deviceUid, sequenceNumber)
        .withPayloadMessage(
            Message.newBuilder().setEventNotificationRequest(eventNotificationRequest).build())
        .build();
  }

  private void writeOslpLogItem(
      final OslpEnvelope oslpEnvelope, final Device device, final boolean incoming) {
    this.oslpLogService.writeOslpLogItem(oslpEnvelope, device, incoming);
  }

  private OslpEnvelope sendRequest(final Device device, final OslpEnvelope request)
      throws IOException, DeviceSimulatorException {
    // Original protocol port.
    int port = this.oslpPortClient;
    // Newer protocol port.
    if (device.getProtocol().equals(ProtocolType.OSLP_ELSTER.toString())) {
      port = this.oslpElsterPortClient;
    }

    // Attempt to send the request and receive response.
    LOGGER.info("Trying to send request: {}", request.getPayloadMessage());
    final OslpEnvelope response =
        this.oslpChannelHandler.send(
            new InetSocketAddress(this.oslpAddressServer, port),
            request,
            device.getDeviceIdentification());
    LOGGER.info("Received response: {}", response.getPayloadMessage());
    return response;
  }

  public DeviceMessageStatus sendEventNotificationCommand(
      final Long id, final Integer event, final String description, final Integer index) {
    return this.sendEventNotificationCommand(id, event, description, index, true);
  }

  public DeviceMessageStatus sendEventNotificationCommand(
      final Long id,
      final Integer event,
      final String description,
      final Integer index,
      final boolean hasTimestamp) {
    // Find device.
    final Device device = this.deviceManagementService.findDevice(id);
    if (device == null) {
      // Set the DeviceMessageStatus NOT_FOUND as the device is not found.
      return DeviceMessageStatus.NOT_FOUND;
    }
    this.errorMessage = "";

    try {
      // Set index when provided in request.
      final int sequenceNumber = device.doGetNextSequence();
      final String timestamp = this.getFormattedCurrentTimestamp();
      final Oslp.Event oslpEvent = Oslp.Event.valueOf(event);

      // Create request and write outgoing request to log.
      final Event deviceSimulatorEvent =
          new Event(oslpEvent, description, index, timestamp, hasTimestamp);
      final OslpEnvelope request =
          this.createEventNotificationRequest(device, sequenceNumber, deviceSimulatorEvent);
      this.writeOslpLogItem(request, device, false);

      // Send event notification message and receive response.
      final OslpEnvelope response = this.sendRequest(device, request);
      // Write incoming response to log.
      this.writeOslpLogItem(response, device, true);

      // Get the sequence number from the response envelope and check it.
      this.checkSequenceNumber(response.getSequenceNumber(), sequenceNumber);
      // Success, update the sequence number of the device.
      device.setSequenceNumber(sequenceNumber);
      this.deviceManagementService.updateDevice(device);
      // Set the DeviceMessageStatus OK as the SendEvent is Success.
      return DeviceMessageStatus.OK;
    } catch (final Exception e) {
      LOGGER.error("send event notification exception", e);
      this.errorMessage = e.getMessage();
      // Set the DeviceMessageStatus FAILURE as the SendEvent is NOT
      // successful.
      return DeviceMessageStatus.FAILURE;
    }
  }

  /**
   * @deprecated No longer used, as device creation scripts create device UID
   */
  @Deprecated
  private byte[] createRandomDeviceUid() {
    // Generate random bytes for UID
    final byte[] deviceUid = new byte[OslpEnvelope.DEVICE_ID_LENGTH];
    this.byteGenerator.nextBytes(deviceUid);
    // Combine manufacturer id of 2 bytes (1 is AME) and device UID of 10
    // bytes.
    return ArrayUtils.addAll(new byte[] {0, 1}, deviceUid);
  }

  public OslpEnvelope.Builder createEnvelopeBuilder(
      final String deviceUid, final Integer sequenceNumber) {
    final byte[] sequenceNumberBytes = new byte[2];
    sequenceNumberBytes[0] = (byte) (sequenceNumber >>> 8);
    sequenceNumberBytes[1] = sequenceNumber.byteValue();

    return new OslpEnvelope.Builder()
        .withSignature(this.oslpSignature)
        .withProvider(this.oslpSignatureProvider)
        .withPrimaryKey(this.privateKey)
        .withDeviceId(Base64.decodeBase64(deviceUid))
        .withSequenceNumber(sequenceNumberBytes);
  }

  /** Check for RegisterDevice, ConfirmRegisterDevice and SendEventNotification. */
  public void checkSequenceNumber(final byte[] bytes, final Integer sequenceNumber)
      throws DeviceSimulatorException {
    if (bytes == null) {
      throw new DeviceSimulatorException("sequence number byte array is null");
    }
    if (bytes.length != 2) {
      throw new DeviceSimulatorException(
          MessageFormat.format(
              "sequence number byte array incorrect length - expected length: {0} actual length: {1}",
              2, bytes.length));
    }
    if (sequenceNumber == null) {
      throw new DeviceSimulatorException("sequence number Integer is null");
    }

    final Integer num = ((bytes[0] & 0xFF) << 8) | (bytes[1] & 0xFF);

    if (sequenceNumber - num != 0) {
      throw new DeviceSimulatorException(
          MessageFormat.format(
              "sequence number incorrect - expected sequence number: {0} actual sequence number: {1}",
              sequenceNumber, num));
    }
  }

  /** Check for RegisterDevice. */
  public void checkRandomDeviceAndRandomPlatform(
      final Integer randomDevice,
      final Integer responseRandomDevice,
      final Integer responseRandomPlatform)
      throws DeviceSimulatorException {
    if (responseRandomDevice == null) {
      throw new DeviceSimulatorException("random device Integer is null");
    }
    if (randomDevice - responseRandomDevice != 0) {
      throw new DeviceSimulatorException(
          MessageFormat.format(
              "random device number incorrect - expected random device number: {0} actual random device number: {1}",
              randomDevice, responseRandomDevice));
    }
    if (responseRandomPlatform == null) {
      throw new DeviceSimulatorException("random platform Integer is null");
    }
  }

  /** Check for ConfirmRegisterDevice. */
  private void checkRandomDeviceAndRandomPlatform(
      final Integer randomDevice,
      final Integer responseRandomDevice,
      final Integer randomPlatform,
      final Integer responseRandomPlatform)
      throws DeviceSimulatorException {
    this.checkRandomDeviceAndRandomPlatform(
        randomDevice, responseRandomDevice, responseRandomPlatform);

    if (randomPlatform - responseRandomPlatform != 0) {
      throw new DeviceSimulatorException(
          MessageFormat.format(
              "random platform number incorrect - expected random platform number: {0} actual random platform number: {1}",
              randomPlatform, responseRandomPlatform));
    }
  }

  public String getCurrentTime() {
    return this.currentTime;
  }

  public String getErrorMessage() {
    return this.errorMessage;
  }
}
