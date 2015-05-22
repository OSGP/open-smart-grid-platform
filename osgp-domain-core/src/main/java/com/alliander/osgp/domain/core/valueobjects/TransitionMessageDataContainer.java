/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects;

import java.io.Serializable;

import org.joda.time.DateTime;

public class TransitionMessageDataContainer implements Serializable {

    /**
     * Serial version ID.
     */
    private static final long serialVersionUID = -6687122715307445705L;
    private TransitionType transitionType;
    private DateTime dateTime;

    public void setTransitionType(final TransitionType transitionType) {
        this.transitionType = transitionType;
    }

    public void setDateTime(final DateTime dateTime) {
        this.dateTime = dateTime;
    }

    public TransitionType getTransitionType() {
        return this.transitionType;
    }

    public DateTime getDateTime() {
        return this.dateTime;
    }
}
