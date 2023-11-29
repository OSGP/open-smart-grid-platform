/*
 * SPDX-FileCopyrightText: Copyright Contributors to the GXF project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensmartgridplatform.adapter.protocol.dlms.application.throttling;

import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.adapter.protocol.dlms.application.config.annotation.DisabledThrottlingServiceCondition;
import org.opensmartgridplatform.throttling.api.Permit;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Conditional(DisabledThrottlingServiceCondition.class)
public class DisabledThrottlingServiceImpl implements ThrottlingService {

  @Override
  public Permit requestPermit(final Integer baseTransceiverStationId, final Integer cellId) {
    log.debug("Throttling is disabled, do nothing on openConnection");
    return null;
  }

  @Override
  public void releasePermit(final Permit permit) {
    log.debug("Throttling is disabled, do nothing on closeConnection");
  }
}
