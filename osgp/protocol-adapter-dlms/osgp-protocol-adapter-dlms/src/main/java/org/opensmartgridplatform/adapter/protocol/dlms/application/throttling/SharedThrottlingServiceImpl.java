/*
 * SPDX-FileCopyrightText: Copyright Contributors to the GXF project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensmartgridplatform.adapter.protocol.dlms.application.throttling;

import org.opensmartgridplatform.adapter.protocol.dlms.application.config.ThrottlingClientConfig;
import org.opensmartgridplatform.adapter.protocol.dlms.application.config.annotation.SharedThrottlingServiceCondition;
import org.opensmartgridplatform.throttling.api.Permit;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

@Component
@Conditional(SharedThrottlingServiceCondition.class)
public class SharedThrottlingServiceImpl implements ThrottlingService {

  private final ThrottlingClientConfig throttlingClientConfig;

  public SharedThrottlingServiceImpl(final ThrottlingClientConfig throttlingClientConfig) {
    this.throttlingClientConfig = throttlingClientConfig;
  }

  @Override
  public Permit requestPermit(final Integer baseTransceiverStationId, final Integer cellId) {
    return this.throttlingClientConfig
        .throttlingClient()
        .requestPermitUsingNetworkSegmentIfIdsAreAvailable(baseTransceiverStationId, cellId);
  }

  @Override
  public void releasePermit(final Permit permit) {
    this.throttlingClientConfig.throttlingClient().releasePermit(permit);
  }
}
