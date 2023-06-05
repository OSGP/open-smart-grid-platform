// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services;

import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.LogItem;

@FunctionalInterface
public interface LoggingService {
  /**
   * Log.
   *
   * @param logItem The {@link LogItem} to log.
   */
  void log(LogItem logItem);
}
