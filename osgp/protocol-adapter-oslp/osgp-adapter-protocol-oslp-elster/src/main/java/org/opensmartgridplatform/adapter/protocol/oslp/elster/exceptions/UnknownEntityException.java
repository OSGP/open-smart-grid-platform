//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.oslp.elster.exceptions;

public class UnknownEntityException extends ProtocolAdapterException {

  /** Serial Version UID. */
  private static final long serialVersionUID = -4937419294803845764L;

  private static final String MESSAGE = "%1$s with id \"%2$s\" could not be found.";

  public UnknownEntityException(final Class<?> entity, final String identification) {
    super(String.format(MESSAGE, entity.getSimpleName(), identification));
  }
}
