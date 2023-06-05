// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.admin.application.exceptionhandling;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.adapter.ws.schema.admin.common.TechnicalFault;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;

public class TechnicalExceptionConverter
    extends CustomConverter<TechnicalException, TechnicalFault> {

  @Override
  public TechnicalFault convert(
      final TechnicalException source,
      final Type<? extends TechnicalFault> destinationType,
      final MappingContext context) {
    if (source == null) {
      return null;
    }
    final TechnicalFault destination = new TechnicalFault();
    destination.setComponent(source.getComponentType().name());
    destination.setMessage(source.getMessage());
    if (source.getCause() != null) {
      destination.setInnerException(source.getCause().getClass().getName());
      destination.setInnerMessage(source.getCause().getMessage());
    }

    return destination;
  }
}
