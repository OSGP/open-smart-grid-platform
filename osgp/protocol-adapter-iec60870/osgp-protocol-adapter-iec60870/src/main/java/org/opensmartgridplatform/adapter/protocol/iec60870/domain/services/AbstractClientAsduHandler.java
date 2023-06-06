// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services;

import javax.annotation.PostConstruct;
import org.openmuc.j60870.ASduType;
import org.opensmartgridplatform.iec60870.Iec60870AsduHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Abstract class providing an abstract method for ASDU handling (each type of ASDU will have a
 * specific ASDU Handler implementation) and automatic registration of implementation classes to
 * {@link Iec60870AsduHandlerRegistry}
 */
public abstract class AbstractClientAsduHandler implements ClientAsduHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractClientAsduHandler.class);

  @Autowired private ClientAsduHandlerRegistry iec60870ClientAsduHandlerRegistry;

  private final ASduType asduType;

  protected AbstractClientAsduHandler(final ASduType asduType) {
    this.asduType = asduType;
  }

  public ASduType getAsduType() {
    return this.asduType;
  }

  @PostConstruct
  protected void register() {
    LOGGER.info(
        "Registering ASDU Handler {} with ASDU type {}",
        this.getClass().getSimpleName(),
        this.asduType);
    this.iec60870ClientAsduHandlerRegistry.registerHandler(this.asduType, this);
  }
}
