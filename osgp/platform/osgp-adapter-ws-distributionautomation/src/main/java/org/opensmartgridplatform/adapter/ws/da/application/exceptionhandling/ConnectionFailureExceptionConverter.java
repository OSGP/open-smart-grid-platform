/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
