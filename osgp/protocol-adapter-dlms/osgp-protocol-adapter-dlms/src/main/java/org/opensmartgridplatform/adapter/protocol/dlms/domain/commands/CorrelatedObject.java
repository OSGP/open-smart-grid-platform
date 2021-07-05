/**
 * Copyright 2021 Alliander N.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands;

import java.io.Serializable;

import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.RequestWithMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

import lombok.Getter;

@Getter
public class CorrelatedObject<T> {
  private static final long serialVersionUID = -6205572886092338803L;
  private final String correlationUid;
  private final T object;

  private CorrelatedObject(final String correlationUid, final T object) {
    this.correlationUid = correlationUid;
    this.object = object;
  }

  public static <T> CorrelatedObject<T> from(
      final CorrelatedObject<?> correlationSource, final T object) {
    return from(correlationSource.correlationUid, object);
  }

  public static <T extends Serializable> CorrelatedObject<T> from(
      final RequestWithMetadata<T> request) {
    return from(request.getMetadata(), request.getRequestObject());
  }

  public static <T> CorrelatedObject<T> from(final RequestWithMetadata<?> request, final T object) {
    return from(request.getMetadata(), object);
  }

  public static <T> CorrelatedObject<T> from(final MessageMetadata metadata, final T object) {
    return from(metadata.getCorrelationUid(), object);
  }

  public static <T> CorrelatedObject<T> from(final String correlationUid, final T object) {
    return new CorrelatedObject<>(correlationUid, object);
  }
}
