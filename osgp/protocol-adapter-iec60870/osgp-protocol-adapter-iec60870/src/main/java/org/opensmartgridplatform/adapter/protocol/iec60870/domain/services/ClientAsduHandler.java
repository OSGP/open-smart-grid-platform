//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
