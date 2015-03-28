package com.alliander.osgp.dto.valueobjects;

import java.io.Serializable;
import java.util.List;

public class EventNotificationMessageDataContainer implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 4707772459625804068L;

    private List<EventNotificationType> eventNotifications;

    public EventNotificationMessageDataContainer(final List<EventNotificationType> eventNotifications) {
        this.eventNotifications = eventNotifications;
    }

    public List<EventNotificationType> getEventNotifications() {
        return this.eventNotifications;
    }
}
