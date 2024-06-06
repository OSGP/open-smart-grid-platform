// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.metrics;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Service for naming metrics. Assures a given prefix is included in the name. */
@Component
public class MetricsNameService {
  private static final Logger LOGGER = LoggerFactory.getLogger(MetricsNameService.class);

  @Value("${metrics.prometheus.applicationname:}")
  private String applicationName;

  /**
   * Creates a proper metric name given the requested metricName.
   *
   * <p>The naming convention says a metric name should include the application name. Generic
   * components can't have the application name hardcoded, in which case this class will prepend it
   * to the requested name. Configure the application using the property <code>
   * metrics.prometheus.applicationname</code>
   *
   * @param metricName requested name for the metric
   * @return the final metric name, guaranteed to start with the application name if configured.
   *     Returned as is otherwise.
   */
  public String createName(final String metricName) {
    if (StringUtils.isNotBlank(this.applicationName)
        && !StringUtils.startsWithIgnoreCase(metricName, this.applicationName)) {
      LOGGER.debug("Prepending {} to the metric name {}", this.applicationName, metricName);
      return this.applicationName + "." + metricName;
    }
    return metricName;
  }
}
