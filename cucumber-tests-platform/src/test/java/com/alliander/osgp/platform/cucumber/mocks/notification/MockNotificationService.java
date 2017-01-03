package com.alliander.osgp.platform.cucumber.mocks.notification;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.ws.schema.microgrids.notification.Notification;

@Component
public class MockNotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockNotificationService.class);

    private ConcurrentLinkedQueue<Notification> queue = new ConcurrentLinkedQueue<>();

    public void handleNotification(final Notification notification, final String organisationIdentification) {
        LOGGER.info("Notification received: {}", notification.getCorrelationUid());
        this.queue.add(notification);
    }

    public boolean receivedNotification() {
        return !this.queue.isEmpty();
    }

    public Notification getNotification() {
        return this.queue.poll();
    }
}
