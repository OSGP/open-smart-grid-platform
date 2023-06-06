// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.da.application.exceptionhandling;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.common.TechnicalFault;
import org.opensmartgridplatform.shared.exceptionhandling.ConnectionFailureException;

public class ConnectionFailureExceptionConverter
    extends CustomConverter<ConnectionFailureException, TechnicalFault> {

  @Override
  public TechnicalFault convert(
      final ConnectionFailureException source,
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
