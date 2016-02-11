/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

public enum Channel {

    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    NONE(-1);

    private final int channelNumber;

    private Channel(final int channelNumber) {
        this.channelNumber = channelNumber;
    }

    public int getChannelNumber() {
        return this.channelNumber;
    }

}
