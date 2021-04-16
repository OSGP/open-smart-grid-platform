/*
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.secretmanagement.application.exception;

/**
 * Unchecked exception that wraps another (checked) exception. Can be used to handle checked
 * exception in streams.
 */
public class ExceptionWrapper extends RuntimeException {
  private static final long serialVersionUID = -1239332310446200862L;

  public ExceptionWrapper(final Exception exc) {
    super(exc);
  }
}
