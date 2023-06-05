// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.iec60870;

import java.io.IOException;
import javax.annotation.PostConstruct;
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Abstract class providing an abstract method for ASdu handling (each type of ASdu will have a
 * specific ASdu Handler implementation) and automatic registration of implementation classes to
 * {@link Iec60870AsduHandlerRegistry}
 */
public abstract class Iec60870AsduHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(Iec60870AsduHandler.class);

  @Autowired private Iec60870AsduHandlerRegistry iec60870AsduHandlerRegistry;

  private final ASduType asduType;

  public Iec60870AsduHandler(final ASduType asduType) {
    this.asduType = asduType;
  }

  public abstract void handleAsdu(Connection t, ASdu u) throws IOException;

  public ASduType getAsduType() {
    return this.asduType;
  }

  @PostConstruct
  protected void register() {
    LOGGER.info("Registering ASDU Handler {}", this.getClass().getSimpleName());
    this.iec60870AsduHandlerRegistry.registerHandler(this.asduType, this);
  }
}
