/*
 * SPDX-FileCopyrightText: Copyright Contributors to the GXF project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensmartgridplatform.adapter.protocol.dlms.application.throttling;

import org.opensmartgridplatform.throttling.api.Permit;

public interface ThrottlingService {

  public Permit openConnection(final Integer baseTransceiverStationId, final Integer cellId);

  public void closeConnection(Permit permit);
}
