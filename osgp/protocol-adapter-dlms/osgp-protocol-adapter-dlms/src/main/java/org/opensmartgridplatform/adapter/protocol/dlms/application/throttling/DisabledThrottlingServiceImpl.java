/*
 * SPDX-FileCopyrightText: Copyright Contributors to the GXF project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensmartgridplatform.adapter.protocol.dlms.application.throttling;

import org.opensmartgridplatform.adapter.protocol.dlms.application.config.annotation.DisabledThrottlingServiceCondition;
import org.opensmartgridplatform.throttling.api.Permit;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

@Component
@Conditional(DisabledThrottlingServiceCondition.class)
public class DisabledThrottlingServiceImpl implements ThrottlingService {

  @Override
  public Permit openConnection(final Integer baseTransceiverStationId, final Integer cellId) {
    return null;
  }

  @Override
  public void closeConnection(final Permit permit) {}
}
