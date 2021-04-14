/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.exceptionhandling;

public class OsgpException extends Exception {

  /** Serial Version UID. */
  private static final long serialVersionUID = 3985910152334024442L;

  protected final ComponentType componentType;

  public OsgpException(final ComponentType componentType, final String message) {
    super(message);
    this.componentType = componentType;
  }

  public OsgpException(
      final ComponentType componentType, final String message, final Throwable cause) {
    super(message, cause);
    this.componentType = componentType;
  }

  public ComponentType getComponentType() {
    return this.componentType;
  }
}
