// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging;

import jakarta.xml.bind.DatatypeConverter;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.iec61850.RegisterDeviceRequest;

public class Iec61850LogItemRequestMessage {

  private static final int MAX_MESSAGE_LENGTH = 8000;

  private final boolean incoming;

  private final String encodedMessage;

  private final String decodedMessage;

  private final String deviceIdentification;

  private final String organisationIdentification;

  private final boolean valid;

  private final int payloadMessageSerializedSize;

  public Iec61850LogItemRequestMessage(
      final String deviceIdentification,
      final boolean incoming,
      final boolean valid,
      final RegisterDeviceRequest message,
      final int payloadMessageSerializedSize) {
    this.deviceIdentification = deviceIdentification;
    this.organisationIdentification = "";
    this.incoming = incoming;
    this.valid = valid;
    this.payloadMessageSerializedSize = payloadMessageSerializedSize;

    // Truncate the log-items to max length.
    this.encodedMessage =
        StringUtils.substring(bytesToCArray(message.toByteArray()), 0, MAX_MESSAGE_LENGTH);
    this.decodedMessage = StringUtils.substring(message.toString(), 0, MAX_MESSAGE_LENGTH);
  }

  public Iec61850LogItemRequestMessage(
      final String deviceIdentification,
      final String organisationIdentification,
      final boolean incoming,
      final boolean valid,
      final String message,
      final int payloadMessageSerializedSize) {
    this.deviceIdentification = deviceIdentification;
    this.organisationIdentification = organisationIdentification;
    this.incoming = incoming;
    this.valid = valid;
    this.payloadMessageSerializedSize = payloadMessageSerializedSize;

    // Truncate the log-items to max length.
    this.encodedMessage = null;
    this.decodedMessage = StringUtils.substring(message, 0, MAX_MESSAGE_LENGTH);
  }

  public Boolean isIncoming() {
    return this.incoming;
  }

  public String getEncodedMessage() {
    return this.encodedMessage;
  }

  public String getDecodedMessage() {
    return this.decodedMessage;
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  public String getOrganisationIdentification() {
    return this.organisationIdentification;
  }

  private static String bytesToCArray(final byte[] bytes) {
    String s = "";
    if (bytes.length > 0) {
      s = DatatypeConverter.printHexBinary(bytes);
      // Split every two chars with
      // ', ' to create a C array.
      s = s.replaceAll("(.{2})", ", 0x$1");
      // Remove the leading comma.
      s = s.substring(2);
    }
    return s;
  }

  public Boolean isValid() {
    return this.valid;
  }

  public int getPayloadMessageSerializedSize() {
    return this.payloadMessageSerializedSize;
  }
}
