//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.iec60870;

import java.util.EnumMap;
import java.util.Map;
import org.openmuc.j60870.ASduType;
import org.opensmartgridplatform.iec60870.exceptions.Iec60870AsduHandlerNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Registry of ASdu handlers, used to look up the corresponding handler for a specific ASdu type.
 * Each ASdu handler should register itself to this registry.
 */
@Component
public class Iec60870AsduHandlerRegistry {

  private static final Logger LOGGER = LoggerFactory.getLogger(Iec60870AsduHandlerRegistry.class);

  private final Map<ASduType, Iec60870AsduHandler> handlers = new EnumMap<>(ASduType.class);

  public Iec60870AsduHandler getHandler(final ASduType asduType)
      throws Iec60870AsduHandlerNotFoundException {
    if (!this.handlers.containsKey(asduType)) {
      LOGGER.error("No ASDU handler found for ASDU type {}", asduType);
      throw new Iec60870AsduHandlerNotFoundException(asduType);
    }
    return this.handlers.get(asduType);
  }

  public void registerHandler(final ASduType asduType, final Iec60870AsduHandler handler) {
    this.handlers.put(asduType, handler);
  }

  public void clearHandlers() {
    this.handlers.clear();
  }
}
