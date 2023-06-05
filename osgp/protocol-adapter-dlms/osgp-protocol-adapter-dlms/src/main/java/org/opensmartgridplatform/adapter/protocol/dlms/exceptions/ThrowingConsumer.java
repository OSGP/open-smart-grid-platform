// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.exceptions;

import java.util.function.Consumer;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;

public interface ThrowingConsumer<T> extends Consumer<T> {

  @Override
  default void accept(final T elem) {
    try {
      this.acceptThrows(elem);
    } catch (final OsgpException e) {
      throw new ConnectionTaskException(e);
    }
  }

  void acceptThrows(T elem) throws OsgpException;
}
