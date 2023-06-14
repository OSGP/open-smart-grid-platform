// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.rest.client;

public class DlmsAttributeValuesClientException extends Exception {

  /** Serial Version UID. */
  private static final long serialVersionUID = -6599182946786998965L;

  public DlmsAttributeValuesClientException(final String message) {
    super(message);
  }

  public DlmsAttributeValuesClientException(final String message, final Throwable t) {
    super(message, t);
  }
}
