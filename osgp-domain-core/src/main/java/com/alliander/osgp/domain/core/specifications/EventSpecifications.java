/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.specifications;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.Event;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.exceptions.ArgumentNullOrEmptyException;
import com.alliander.osgp.domain.core.valueobjects.EventType;

public interface EventSpecifications {
    Specification<Event> isCreatedAfter(final Date dateFrom) throws ArgumentNullOrEmptyException;

    Specification<Event> isCreatedBefore(final Date dateUntil) throws ArgumentNullOrEmptyException;

    Specification<Event> isFromDevice(final Device device) throws ArgumentNullOrEmptyException;

    Specification<Event> isAuthorized(final Organisation organisation) throws ArgumentNullOrEmptyException;

    Specification<Event> hasEventTypes(final List<EventType> eventTypes) throws ArgumentNullOrEmptyException;

}