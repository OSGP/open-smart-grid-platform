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
    @Autowired
    RetryHeaderFactory retryHeaderFactory;

    @Test
    void testMultiplierSmallerThanOneShouldResultInFasterScheduledRetryTimeWithMultipleRetries(){
        RetryHeader retryHeaderWithOneRetry = this.retryHeaderFactory.createRetryHeader(1);
        RetryHeader retryHeaderWithThreeRetries = this.retryHeaderFactory.createRetryHeader(3);

        assertNotNull(retryHeaderWithOneRetry);
        assertNotNull(retryHeaderWithThreeRetries);

        assertTrue(retryHeaderWithThreeRetries.getScheduledRetryTime().before(retryHeaderWithOneRetry.getScheduledRetryTime()));
    }

}