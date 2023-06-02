//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.shared.exceptionhandling;

public class NoDeviceResponseException extends Exception {

  /** Serial Version UID. */
  private static final long serialVersionUID = 5129388216260738121L;

  private static final String MESSAGE = "No response from device";

  public NoDeviceResponseException() {
    super(MESSAGE);
  }
}
