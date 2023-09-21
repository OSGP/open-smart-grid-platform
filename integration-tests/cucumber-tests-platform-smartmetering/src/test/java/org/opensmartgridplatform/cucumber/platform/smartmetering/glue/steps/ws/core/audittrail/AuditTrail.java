// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.core.audittrail;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import io.cucumber.java.en.Then;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.opensmartgridplatform.cucumber.core.RetryableAssert;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.logging.domain.entities.DeviceLogItem;
import org.opensmartgridplatform.logging.domain.repositories.DeviceLogItemPagingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class AuditTrail {
  private static final String PATTERN_RETRY_OPERATION = "retry count= .*, correlationuid= .*";

  @Autowired private DeviceLogItemPagingRepository deviceLogItemRepository;

  @Then("^the audit trail contains a retry log records$")
  public void theAuditTrailContainsMultipleRetryLogRecords(final Map<String, String> settings)
      throws Throwable {
    final String deviceIdentification =
        getString(
            settings,
            PlatformKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION);

    final int minimumNumberReturned = 1;
    final Predicate<DeviceLogItem> filter =
        dli -> Pattern.matches(PATTERN_RETRY_OPERATION, dli.getDecodedMessage());

    final Runnable assertion =
        () -> {
          final Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
          final Page<DeviceLogItem> deviceLogPage =
              this.deviceLogItemRepository.findByDeviceIdentification(
                  deviceIdentification, pageable);
          final List<DeviceLogItem> filteredDeviceLogItems =
              deviceLogPage.getContent().stream().filter(filter).collect(Collectors.toList());

          assertThat(filteredDeviceLogItems.size() < minimumNumberReturned)
              .as(
                  "Number of matching DeviceLogItems for device "
                      + deviceIdentification
                      + " must be at least "
                      + minimumNumberReturned
                      + ", but was "
                      + filteredDeviceLogItems.size())
              .isFalse();
        };
    final int numberOfRetries = 600;
    final long delay = 1;
    final TimeUnit unit = TimeUnit.SECONDS;
    try {
      RetryableAssert.assertWithRetries(assertion, numberOfRetries, delay, unit);
    } catch (final AssertionError e) {
      throw new AssertionError(
          "Failed to find at least "
              + minimumNumberReturned
              + " retry log items for device "
              + deviceIdentification
              + " within "
              + RetryableAssert.describeMaxDuration(numberOfRetries, delay, unit),
          e);
    }
  }
}
