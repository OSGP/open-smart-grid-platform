// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.throttling.services;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.BucketProxy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.distributed.proxy.RemoteBucketBuilder;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.throttling.model.ThrottlingSettings;

@ExtendWith(MockitoExtension.class)
class Bucket4JRateLimitServiceTest {

  private static final int BTS_ID = -1;
  private static final int CELL_ID = -1;

  @Mock private ProxyManager<String> proxyManager;

  @Mock private RemoteBucketBuilder<String> bucketBuilder;

  @Mock private BucketProxy bucket;

  @Mock private ConsumptionProbe consumptionProbe;

  private Bucket4JRateLimitService service;

  @BeforeEach
  public void setup() {
    this.service = new Bucket4JRateLimitService(this.proxyManager);
  }

  @Test
  public void newConnectionRequestAllowed() {
    this.prepareBucket(true);
    final ThrottlingSettings throttlingSettings = this.createSettings(30);

    final boolean allowed =
        this.service.isNewConnectionRequestAllowed(BTS_ID, CELL_ID, throttlingSettings);

    assertThat(allowed).isTrue();
  }

  @Test
  public void newConnectionRequestNotAllowed() {
    this.prepareBucket(false);
    final ThrottlingSettings throttlingSettings = this.createSettings(1);

    final boolean allowed =
        this.service.isNewConnectionRequestAllowed(BTS_ID, CELL_ID, throttlingSettings);

    assertThat(allowed).isFalse();
  }

  @Test
  public void newConnectionsUnlimited() {
    final ThrottlingSettings throttlingSettings = this.createSettings(-1);

    final boolean allowed =
        this.service.isNewConnectionRequestAllowed(BTS_ID, CELL_ID, throttlingSettings);

    assertThat(allowed).isTrue();
  }

  @Test
  public void newConnectionsSetToZero() {
    final ThrottlingSettings throttlingSettings = this.createSettings(0);

    final boolean allowed =
        this.service.isNewConnectionRequestAllowed(BTS_ID, CELL_ID, throttlingSettings);

    assertThat(allowed).isFalse();
  }

  private void prepareBucket(final boolean isConsumed) {
    when(this.proxyManager.builder()).thenReturn(this.bucketBuilder);
    when(this.bucketBuilder.build(anyString(), any(Supplier.class))).thenReturn(this.bucket);
    when(this.bucket.tryConsumeAndReturnRemaining(anyLong())).thenReturn(this.consumptionProbe);
    when(this.consumptionProbe.isConsumed()).thenReturn(isConsumed);
  }

  private ThrottlingSettings createSettings(final int maxNewConnections) {
    return new ThrottlingSettings() {
      @Override
      public int getMaxConcurrency() {
        return 0;
      }

      @Override
      public int getMaxNewConnections() {
        return maxNewConnections;
      }

      @Override
      public long getMaxNewConnectionsResetTimeInMs() {
        return 0;
      }

      @Override
      public long getMaxNewConnectionsWaitTimeInMs() {
        return 0;
      }
    };
  }
}
