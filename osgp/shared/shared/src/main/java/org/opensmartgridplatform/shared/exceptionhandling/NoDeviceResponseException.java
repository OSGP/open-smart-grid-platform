/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.exceptionhandling;

public class NoDeviceResponseException extends Exception {

  /** Serial Version UID. */
  private static final long serialVersionUID = 5129388216260738121L;

  private static final String MESSAGE = "No response from device";

  public NoDeviceResponseException() {
    super(MESSAGE);
  }
}
