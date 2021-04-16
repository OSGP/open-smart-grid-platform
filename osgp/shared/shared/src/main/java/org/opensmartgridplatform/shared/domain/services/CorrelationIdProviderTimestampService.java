/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.domain.services;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class CorrelationIdProviderTimestampService implements CorrelationIdProviderService {

  private static final String SEPARATOR = "|||";

  @Override
  public String getCorrelationId(
      final String organisationIdentification, final String deviceIdentification) {

    return organisationIdentification
        + SEPARATOR
        + deviceIdentification
        + SEPARATOR
        + this.getCurrentDateString()
        + SEPARATOR
        + UUID.randomUUID();
  }

  private String getCurrentDateString() {
    final Date now = new Date();
    final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddkkmmssSSS");
    return sdf.format(now);
  }
}
