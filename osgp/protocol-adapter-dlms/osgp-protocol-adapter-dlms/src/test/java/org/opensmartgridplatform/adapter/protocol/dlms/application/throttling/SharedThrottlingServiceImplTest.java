/*
 * SPDX-FileCopyrightText: Copyright Contributors to the GXF project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensmartgridplatform.adapter.protocol.dlms.application.throttling;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.dlms.application.config.ThrottlingClientConfig;
import org.opensmartgridplatform.throttling.ThrottlingClient;
import org.opensmartgridplatform.throttling.api.Permit;

@ExtendWith(MockitoExtension.class)
class SharedThrottlingServiceImplTest {
  private final Integer BTS_ID = 2;
  private final Integer CELL_ID = 3;
  private final Integer PRIORITY = 4;

  @Mock private ThrottlingClientConfig throttlingClientConfig;
  @Mock private ThrottlingClient throttlingClient;

  @InjectMocks SharedThrottlingServiceImpl throttlingService;

  @Test
  void testOpenConnection() {
    final Permit permit = mock(Permit.class);
    when(this.throttlingClientConfig.throttlingClient()).thenReturn(this.throttlingClient);
    when(this.throttlingClient.requestPermitUsingNetworkSegmentIfIdsAreAvailable(
            this.BTS_ID, this.CELL_ID, this.PRIORITY))
        .thenReturn(permit);

    final Permit result =
        this.throttlingService.requestPermit(this.BTS_ID, this.CELL_ID, this.PRIORITY);

    assertThat(result).isEqualTo(permit);
  }

  @Test
  void testCloseConnection() {
    final Permit permit = mock(Permit.class);
    when(this.throttlingClientConfig.throttlingClient()).thenReturn(this.throttlingClient);

    this.throttlingService.releasePermit(permit);

    verify(this.throttlingClient).releasePermit(permit);
  }
}
