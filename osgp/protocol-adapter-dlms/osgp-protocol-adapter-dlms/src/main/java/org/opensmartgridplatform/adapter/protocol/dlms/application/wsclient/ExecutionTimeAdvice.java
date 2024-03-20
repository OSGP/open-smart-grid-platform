/*
 * SPDX-FileCopyrightText: Copyright Contributors to the GXF project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensmartgridplatform.adapter.protocol.dlms.application.wsclient;

import static org.opensmartgridplatform.adapter.protocol.dlms.application.metrics.ProtocolAdapterMetrics.METRIC_REQUEST_TIMER_PREFIX;
import static org.opensmartgridplatform.adapter.protocol.dlms.application.metrics.ProtocolAdapterMetrics.TAG_NR_OF_KEYS;

import io.micrometer.core.instrument.Timer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.opensmartgridplatform.adapter.protocol.dlms.application.metrics.ProtocolAdapterMetrics;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExecutionTimeAdvice {
  private final ProtocolAdapterMetrics protocolAdapterMetrics;

  public ExecutionTimeAdvice(final ProtocolAdapterMetrics protocolAdapterMetrics) {
    this.protocolAdapterMetrics = protocolAdapterMetrics;
  }

  @Around("@annotation(trackExecutionTime)")
  public Object executionTime(
      final ProceedingJoinPoint point, final TrackExecutionTime trackExecutionTime)
      throws Throwable {
    final Timer timer = this.createTimer(trackExecutionTime.timerName(), 2);
    final long starttime = System.currentTimeMillis();

    final Object object = point.proceed();
    this.recordTimer(timer, starttime);

    return object;
  }

  private Timer createTimer(final String timerName, final int nrOfKeys) {
    final Map<String, String> tags = new HashMap<>();
    tags.put(TAG_NR_OF_KEYS, String.valueOf(nrOfKeys));
    System.out.println("---- createTimer " + timerName + " with " + nrOfKeys + " keys");
    return this.protocolAdapterMetrics.createTimer(METRIC_REQUEST_TIMER_PREFIX + timerName, tags);
  }

  private void recordTimer(final Timer timer, final long starttime) {
    System.out.println(
        "---- RECORD " + timer + " in " + (System.currentTimeMillis() - starttime) + "ms");
    this.protocolAdapterMetrics.recordTimer(
        timer, System.currentTimeMillis() - starttime, TimeUnit.MILLISECONDS);
  }
}
