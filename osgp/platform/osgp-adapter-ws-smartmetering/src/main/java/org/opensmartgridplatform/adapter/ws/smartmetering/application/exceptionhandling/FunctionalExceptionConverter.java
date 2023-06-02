//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.smartmetering.application.exceptionhandling;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.FunctionalFault;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

public class FunctionalExceptionConverter
    extends CustomConverter<FunctionalException, FunctionalFault> {

  @Override
  public FunctionalFault convert(
      final FunctionalException source,
      final Type<? extends FunctionalFault> destinationType,
      final MappingContext context) {

    if (source == null) {
      return null;
    }

    final FunctionalFault destination = new FunctionalFault();
    destination.setCode(source.getCode());
    destination.setComponent(source.getComponentType().name());
    destination.setMessage(source.getMessage());

    final Throwable cause = source.getCause();
    final String innerException;
    final String innerMessage;
    if (cause == null) {
      innerException = "";
      innerMessage = "";
    } else {
      innerException = cause.getClass().getName();
      innerMessage = cause.getMessage();
    }
    destination.setInnerException(innerException);
    destination.setInnerMessage(innerMessage);

    return destination;
  }
}
