package org.opensmartgridplatform.core.infra.jms.protocol;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ProtocolRequestMessageSenderTestConfig.class })
public class ProtocolRequestMessageSenderTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtocolRequestMessageSenderTest.class);

    @Autowired
    private ProtocolRequestMessageSender messageSender;

    @Autowired
    private int messageGroupCacheSize;

    @Test
    public void messageGroupIdShouldBeBetween0AndCacheSize() {
        for (int i = 0; i < 10000; i++) {
            final String uuid = UUID.randomUUID().toString();
            final String messageGroupId = this.messageSender.getMessageGroupId(uuid);
            final int actual = Integer.valueOf(messageGroupId);

            assertTrue("Message group id should be between 0 and cache size " + this.messageGroupCacheSize,
                    actual >= 0 && actual < this.messageGroupCacheSize);
        }
    }

    @Test
    public void numberOfMessageGroupsShouldNotExceedCacheSize() {
        final Set<String> groupIds = new HashSet<>();

        for (int i = 0; i < 10000; i++) {
            final String uuid = UUID.randomUUID().toString();
            groupIds.add(this.messageSender.getMessageGroupId(uuid));
        }
        final int actual = groupIds.size();
        assertTrue("Number of message groups should not exceed cache size " + this.messageGroupCacheSize,
                actual <= this.messageGroupCacheSize);
    }

    @Test
    public void messageGroupIdShouldBeSameForSameDevice() {
        final String deviceId = "test-device-001";
        final Set<String> groupIds = new HashSet<>();

        for (int i = 0; i < 100; i++) {
            groupIds.add(this.messageSender.getMessageGroupId(deviceId));
        }
        final int actual = groupIds.size();
        assertTrue("Message group id should be the same for same device", actual == 1);
    }
}
