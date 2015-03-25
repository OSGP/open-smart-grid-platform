package com.alliander.osgp.dto.valueobjects;

import java.io.Serializable;

import org.joda.time.DateTime;

public class TransitionMessageDataContainer implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 5491018613060059335L;

    private TransitionType transitionType;
    private DateTime dateTime;

    public TransitionMessageDataContainer(final TransitionType transitionType, final DateTime dateTime) {
        this.transitionType = transitionType;
        this.dateTime = dateTime;
    }

    public TransitionType getTransitionType() {
        return this.transitionType;
    }

    public DateTime getDateTime() {
        return this.dateTime;
    }
}
