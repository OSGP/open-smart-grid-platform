// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.oslp;

@SuppressWarnings("serial")
public class UnknownOslpDecodingStateException extends Exception {

  private static final String MESSAGE = "Unknown OSLP decoding state: %1$s";

  public UnknownOslpDecodingStateException(final String unknownState) {
    super(String.format(MESSAGE, unknownState));
  }
}
