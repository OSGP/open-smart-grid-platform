/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.protocol.dlms.exceptions;

import java.util.function.Consumer;
import javax.jms.JMSException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;

public interface ThrowingConsumer<T> extends Consumer<T> {

  @Override
  default void accept(final T elem) {
    try {
      this.acceptThrows(elem);
    } catch (final Exception e) {
      throw new ConnectionTaskException(e);
    }
  }

  void acceptThrows(T elem) throws JMSException, OsgpException;
}
