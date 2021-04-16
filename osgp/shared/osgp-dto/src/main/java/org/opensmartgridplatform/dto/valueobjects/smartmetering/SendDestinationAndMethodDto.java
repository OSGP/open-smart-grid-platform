/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Objects;

public class SendDestinationAndMethodDto implements Serializable {

  private static final long serialVersionUID = -5374019711302780358L;

  private final TransportServiceTypeDto transportService;
  private final String destination;
  private final MessageTypeDto message;

  public SendDestinationAndMethodDto(
      final TransportServiceTypeDto transportService,
      final String destination,
      final MessageTypeDto message) {
    Objects.requireNonNull(transportService, "transportService must not be null");
    Objects.requireNonNull(destination, "destination must not be null");
    Objects.requireNonNull(message, "message must not be null");
    this.transportService = transportService;
    this.destination = destination;
    this.message = message;
  }

  @Override
  public String toString() {
    return "SendDestinationAndMethod[service="
        + this.transportService
        + ", destination="
        + this.destination
        + ", message="
        + this.message
        + "]";
  }

  public TransportServiceTypeDto getTransportService() {
    return this.transportService;
  }

  public String getDestination() {
    return this.destination;
  }

  public MessageTypeDto getMessage() {
    return this.message;
  }
}
