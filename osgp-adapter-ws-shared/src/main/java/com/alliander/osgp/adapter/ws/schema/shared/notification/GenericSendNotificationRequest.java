/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.schema.shared.notification;

public class GenericSendNotificationRequest {

    private GenericNotification notification;

    /**
     * @return the notification
     */
    public GenericNotification getNotification() {
        return this.notification;
    }

    /**
     * @param notification
     *            the notification to set
     */
    public void setNotification(final GenericNotification notification) {
        this.notification = notification;
    }

}
