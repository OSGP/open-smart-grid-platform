// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.infra.networking;

@SuppressWarnings("serial")
public class UnrecognizedMessageDataException extends Exception {

  private static final String MESSAGE =
      "Data in DLMS Push Notification cannot be decoded. Reason: %1$s";

  public UnrecognizedMessageDataException(final String reason) {
    super(String.format(MESSAGE, reason));
  }

  public UnrecognizedMessageDataException(final String reason, final Throwable cause) {
    super(String.format(MESSAGE, reason), cause);
  }
}
