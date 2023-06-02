//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
