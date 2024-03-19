// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.application.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Timer.Builder;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProtocolAdapterMetrics {
  public static final String METRIC_REQUEST_TIMER_PREFIX = "request_timer_";
  public static final String TAG_NR_OF_KEYS = "NrOfKeys";
  private final MeterRegistry meterRegistry;

  public ProtocolAdapterMetrics(final MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;
  }

  public Timer createTimer(final String name, final Map<String, String> tags) {
    final Builder builder = Timer.builder(name);
    tags.forEach(builder::tag);
    return builder.register(this.meterRegistry);
  }

  public void recordTimer(final Timer timer, final Long duration, final TimeUnit timeUnit) {
    timer.record(duration, timeUnit);
  }
}
