/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.util.List;

public class EventMessageDataContainerDto extends ActionResponseDto {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -6307300080258613848L;

    private List<EventDto> events;

    public EventMessageDataContainerDto(final List<EventDto> events) {
        this.events = events;
    }

    public List<EventDto> getEvents() {
        return this.events;
    }
}
