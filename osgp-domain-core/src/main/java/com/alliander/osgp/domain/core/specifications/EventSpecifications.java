package com.alliander.osgp.domain.core.specifications;

import java.util.Date;

import org.springframework.data.jpa.domain.Specification;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.Event;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.exceptions.ArgumentNullOrEmptyException;

public interface EventSpecifications {
    Specification<Event> isCreatedAfter(final Date dateFrom) throws ArgumentNullOrEmptyException;

    Specification<Event> isCreatedBefore(final Date dateUntil) throws ArgumentNullOrEmptyException;

    Specification<Event> isFromDevice(final Device device) throws ArgumentNullOrEmptyException;

    Specification<Event> isAuthorized(final Organisation organisation) throws ArgumentNullOrEmptyException;
}