//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.infra.networking;

@SuppressWarnings("serial")
public class UnknownDecodingStateException extends Exception {

  private static final String MESSAGE = "Unknown DLMS Push Notification decoding state: %1$s";

  public UnknownDecodingStateException(final String unknownState) {
    super(String.format(MESSAGE, unknownState));
  }
}
