// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.secretmanagement.application.services;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Timer.Builder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.shared.exceptionhandling.EncrypterException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SecretManagementMetrics {

  public static final String METRIC_REQUEST_TIMER_PREFIX = "request_timer_";
  public static final String TAG_NR_OF_KEYS = "NrOfKeys";

  public static final String HARDSERVER_COMMUNICATION_EXCEPTION =
      "hardserver.communication.exception";

  public static final String ENCRYPTER_EXCEPTION = "encrypter.exception";

  private final MeterRegistry meterRegistry;
  private final List<String> hardserverExceptionClasses;

  public SecretManagementMetrics(
      final MeterRegistry meterRegistry,
      @Value("${hardserver.exception.classes}") final String hardserverExceptionClasses) {
    this.meterRegistry = meterRegistry;
    this.hardserverExceptionClasses =
        Arrays.asList(StringUtils.split(hardserverExceptionClasses, ";"));
  }

  public Timer createTimer(final String name, final Map<String, String> tags) {
    final Builder builder = Timer.builder(name);
    tags.forEach(builder::tag);
    return builder.register(this.meterRegistry);
  }

  public void recordTimer(final Timer timer, final Long duration, final TimeUnit timeUnit) {
    timer.record(duration, timeUnit);
  }

  @SuppressWarnings("java:S1872")
  public void incrementEncrypterException(final EncrypterException encrypterException) {

    if (encrypterException.getCause() != null) {
      final String causeClassName = encrypterException.getCause().getClass().getName();
      if (this.hardserverExceptionClasses.stream().anyMatch(causeClassName::equals)) {
        this.meterRegistry.counter(HARDSERVER_COMMUNICATION_EXCEPTION).increment();
        log.info(
            "EncrypterException occurred with hardserver cause class: {}, message: {}",
            causeClassName,
            encrypterException.getMessage());
      } else {
        log.info(
            "EncrypterException occurred without hardserver cause class: {}, message: {}",
            causeClassName,
            encrypterException.getMessage());
      }
    } else {
      log.info(
          "EncrypterException occurred without cause class, message: {}",
          encrypterException.getMessage());
    }

    this.meterRegistry.counter(ENCRYPTER_EXCEPTION).increment();
  }

  public int countHardserverCommunicationExceptions() {
    return (int) this.meterRegistry.counter(HARDSERVER_COMMUNICATION_EXCEPTION).count();
  }
}
