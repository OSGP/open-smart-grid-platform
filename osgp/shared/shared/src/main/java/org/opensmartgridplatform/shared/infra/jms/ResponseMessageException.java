// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.infra.jms;

public class ResponseMessageException extends Exception {

  /** Serial Version UID. */
  private static final long serialVersionUID = 5651554598195858729L;

  public ResponseMessageException(final String message) {
    super(message);
  }

  public ResponseMessageException(final String message, final Throwable throwable) {
    super(message, throwable);
  }
}
