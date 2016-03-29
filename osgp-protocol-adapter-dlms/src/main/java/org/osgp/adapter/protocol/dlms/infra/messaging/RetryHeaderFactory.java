package org.osgp.adapter.protocol.dlms.infra.messaging;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alliander.osgp.shared.infra.jms.RetryHeader;

@Component
public class RetryHeaderFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(RetryHeaderFactory.class);

    private int retryDelay = 30000;

    private int maximumRetries = 5;

    private int backoffMultiplier = 2;

    private boolean exponentialBackoff = true;

    public RetryHeader createRetryHeader(final int retry) {
        final Calendar retryTime = Calendar.getInstance();
        retryTime.add(Calendar.MILLISECOND, this.calculateDelay(retry));

        LOGGER.info("Scheduling retry for {}.", retryTime.getTime());
        return new RetryHeader(retry, this.maximumRetries, retryTime.getTime());
    }

    public RetryHeader createEmtpyRetryHeader() {
        return new RetryHeader();
    }

    private int calculateDelay(final int retry) {
        int delay = this.retryDelay;
        if (this.exponentialBackoff) {
            final double exponent = Math.pow(this.backoffMultiplier, retry);
            delay *= exponent;
        }
        return delay;
    }
}
