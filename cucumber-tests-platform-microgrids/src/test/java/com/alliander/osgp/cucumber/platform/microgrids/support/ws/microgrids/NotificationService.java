/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.microgrids.support.ws.microgrids;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.ws.schema.microgrids.notification.Notification;

@Component
public class NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

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

    public void clearAllNotifications() {
        this.queue.clear();
    }
}
