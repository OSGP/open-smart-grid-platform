/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.exceptionhandling;

public class DeprecatedPropertyException extends RuntimeException {

  /** Serial Version UID. */
  private static final long serialVersionUID = 5003641861132609212L;

  public DeprecatedPropertyException(final String message) {
    super(message);
  }
}
