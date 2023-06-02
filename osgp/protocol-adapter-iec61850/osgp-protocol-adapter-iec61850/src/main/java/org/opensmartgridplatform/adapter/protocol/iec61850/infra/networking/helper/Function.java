//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper;

import org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects.DeviceMessageLog;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;

/**
 * An interface, containing a function that can be applied. <R> is the given return type. Can be
 * {@link Void}
 */
public interface Function<R> {

  /** The function, containing the business logic of the Function */
  R apply(DeviceMessageLog deviceMessageLog) throws ProtocolAdapterException;
}
