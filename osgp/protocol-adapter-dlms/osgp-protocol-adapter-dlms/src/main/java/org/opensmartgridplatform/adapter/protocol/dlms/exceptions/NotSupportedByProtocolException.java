// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.exceptions;

public class NotSupportedByProtocolException extends ProtocolAdapterException
    implements NonRetryableException {

  private static final long serialVersionUID = -5950272388282811074L;

  public NotSupportedByProtocolException(final String message) {
    super(message);
  }
}
