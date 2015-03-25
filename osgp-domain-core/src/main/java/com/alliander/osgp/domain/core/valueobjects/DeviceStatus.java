package com.alliander.osgp.domain.core.valueobjects;

import java.util.ArrayList;
import java.util.List;

public class DeviceStatus implements java.io.Serializable {

    private static final long serialVersionUID = -6922074543798047230L;

    private List<LightValue> lightValues;
    private LinkType preferredLinkType;
    private LinkType actualLinkType;
    private LightType lightType;
    private int eventNotificationsMask;

    public DeviceStatus(final List<LightValue> lightValues, final LinkType preferredLinkType,
            final LinkType actualLinkType, final LightType lightType, final int eventNotificationsMask) {
        this.lightValues = lightValues;
        this.preferredLinkType = preferredLinkType;
        this.actualLinkType = actualLinkType;
        this.lightType = lightType;
        this.eventNotificationsMask = eventNotificationsMask;
    }

    public List<LightValue> getLightValues() {
        return this.lightValues;
    }

    public LinkType getPreferredLinkType() {
        return this.preferredLinkType;
    }

    public LinkType getActualLinkType() {
        return this.actualLinkType;
    }

    public LightType getLightType() {
        return this.lightType;
    }

    public int getEventNotificationsMask() {
        return this.eventNotificationsMask;
    }

    public List<EventNotificationType> getEventNotifications() {
        final List<EventNotificationType> events = new ArrayList<EventNotificationType>();
        if (this.eventNotificationsMask > 0) {
            for (final EventNotificationType event : EventNotificationType.values()) {
                if ((this.eventNotificationsMask & (1 << event.ordinal())) != 0) {
                    events.add(event);
                }
            }
        }
        return events;
    }

    public void updateLightValues(final List<LightValue> lightValues) {
        this.lightValues = lightValues;
    }
}