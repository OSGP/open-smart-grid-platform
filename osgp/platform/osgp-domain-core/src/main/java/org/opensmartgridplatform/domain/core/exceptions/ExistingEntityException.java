/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.exceptions;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

/** */
@SoapFault(faultCode = FaultCode.SERVER)
public class ExistingEntityException extends PlatformException {

  /** Serial Version UID. */
  private static final long serialVersionUID = 7521165131677764473L;

  private static final String MESSAGE = "%1$s with id %2$s already exists.";

  /**
   * Constructor
   *
   * @param entity
   * @param identification
   * @param throwable
   */
  public ExistingEntityException(
      final Class<?> entity, final String identification, final Throwable throwable) {
    super(String.format(MESSAGE, entity.getSimpleName(), identification), throwable);
  }

  /**
   * Constructor
   *
   * @param entity
   * @param identification
   */
  public ExistingEntityException(final Class<?> entity, final String identification) {
    super(String.format(MESSAGE, entity.getSimpleName(), identification));
  }
}
