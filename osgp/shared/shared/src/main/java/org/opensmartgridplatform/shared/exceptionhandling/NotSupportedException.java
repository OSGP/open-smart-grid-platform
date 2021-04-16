/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.exceptionhandling;

public class NotSupportedException extends OsgpException {

  /** Serial Version UID. */
  private static final long serialVersionUID = 6225973923998793514L;

  public NotSupportedException(final ComponentType componentType, final String message) {
    super(componentType, message);
  }
}
