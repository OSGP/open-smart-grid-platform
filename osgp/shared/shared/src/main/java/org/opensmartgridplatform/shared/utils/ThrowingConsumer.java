/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.shared.utils;

import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface ThrowingConsumer<T> extends Consumer<T> {
  Logger LOGGER = LoggerFactory.getLogger(ThrowingConsumer.class);

  @Override
  default void accept(final T elem) {
    try {
      this.acceptThrows(elem);
    } catch (final Exception e) {
      LOGGER.error("Exception: {}", e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }

  void acceptThrows(T elem) throws Exception;
}
