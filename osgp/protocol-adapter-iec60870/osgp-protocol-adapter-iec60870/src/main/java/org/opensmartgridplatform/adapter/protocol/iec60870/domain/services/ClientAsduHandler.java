/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services;

import org.openmuc.j60870.ASdu;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ResponseMetadata;

@FunctionalInterface
public interface ClientAsduHandler {
  /**
   * Handle an ASDU.
   *
   * @param asdu The {@link ASdu} instance.
   * @param responseMetadata The {@link ResponseMetadata} instance.
   * @throws AsduHandlerException
   */
  void handleAsdu(ASdu asdu, ResponseMetadata responseMetadata);
}
