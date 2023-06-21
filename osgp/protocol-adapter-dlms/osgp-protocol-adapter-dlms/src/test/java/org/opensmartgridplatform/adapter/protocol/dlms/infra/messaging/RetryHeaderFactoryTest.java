// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.opensmartgridplatform.shared.infra.jms.RetryHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RetryHeaderFactory.class)
@TestPropertySource(
    properties = {
      "schedule.retry.dlms.backoff.multiplier=0.5",
      "schedule.retry.dlms.delay=7200000",
      "schedule.retry.dlms.backoff.exponential=true",
      "schedule.retry.dlms.retries=10"
    })
class RetryHeaderFactoryTest {
  @Autowired RetryHeaderFactory retryHeaderFactory;

  @Test
  void testMultiplierSmallerThanOneShouldResultInFasterScheduledRetryTimeWithMultipleRetries() {
    RetryHeader retryHeaderWithOneRetry = this.retryHeaderFactory.createRetryHeader(1);
    RetryHeader retryHeaderWithTwoRetries = this.retryHeaderFactory.createRetryHeader(2);
    RetryHeader retryHeaderWithThreeRetries = this.retryHeaderFactory.createRetryHeader(3);

    assertNotNull(retryHeaderWithOneRetry);
    assertNotNull(retryHeaderWithTwoRetries);
    assertNotNull(retryHeaderWithThreeRetries);

    long timeDifferenceBetweenOneAndTwo =
        retryHeaderWithOneRetry.getScheduledRetryTime().getTime()
            - retryHeaderWithTwoRetries.getScheduledRetryTime().getTime();
    long timeDifferenceBetweenTwoAndThree =
        retryHeaderWithTwoRetries.getScheduledRetryTime().getTime()
            - retryHeaderWithThreeRetries.getScheduledRetryTime().getTime();

    assertTrue(
        retryHeaderWithThreeRetries
            .getScheduledRetryTime()
            .before(retryHeaderWithTwoRetries.getScheduledRetryTime()));
    assertTrue(
        retryHeaderWithTwoRetries
            .getScheduledRetryTime()
            .before(retryHeaderWithOneRetry.getScheduledRetryTime()));
    assertTrue(timeDifferenceBetweenOneAndTwo > timeDifferenceBetweenTwoAndThree);
  }
}
