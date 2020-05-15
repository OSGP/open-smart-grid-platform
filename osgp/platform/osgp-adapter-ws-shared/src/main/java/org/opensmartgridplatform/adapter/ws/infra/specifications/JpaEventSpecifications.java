/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.infra.specifications;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceAuthorization;
import org.opensmartgridplatform.domain.core.entities.Event;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.exceptions.ArgumentNullOrEmptyException;
import org.opensmartgridplatform.domain.core.specifications.EventSpecifications;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunctionGroup;
import org.opensmartgridplatform.domain.core.valueobjects.EventType;
import org.springframework.data.jpa.domain.Specification;

public class JpaEventSpecifications implements EventSpecifications {

    private static final String DEVICE = "device";
    private static final String DESCRIPTION = "description";

    @Override
    public Specification<Event> isCreatedAfter(final Date dateFrom) throws ArgumentNullOrEmptyException {

        if (dateFrom == null) {
            throw new ArgumentNullOrEmptyException("dateFrom");
        }

        return ((eventRoot, query, cb) -> cb.greaterThanOrEqualTo(eventRoot.<Date> get("dateTime"), dateFrom));
    }

    @Override
    public Specification<Event> isCreatedBefore(final Date dateUntil) throws ArgumentNullOrEmptyException {

        if (dateUntil == null) {
            throw new ArgumentNullOrEmptyException("dateUntil");
        }

        return ((eventRoot, query, cb) -> cb.lessThanOrEqualTo(eventRoot.<Date> get("dateTime"), dateUntil));
    }

    @Override
    public Specification<Event> isFromDevice(final Device device) throws ArgumentNullOrEmptyException {
        if (device == null) {
            throw new ArgumentNullOrEmptyException(DEVICE);
        }

        return ((eventRoot, query, cb) -> cb.equal(eventRoot.<Integer> get(DEVICE), device.getId()));
    }

    @Override
    public Specification<Event> isAuthorized(final Organisation organisation) throws ArgumentNullOrEmptyException {

        if (organisation == null) {
            throw new ArgumentNullOrEmptyException("organisation");
        }

        return ((eventRoot, query, cb) -> this.createPredicateForIsAuthorized(eventRoot, query, cb, organisation));
    }

    private Predicate createPredicateForIsAuthorized(final Root<Event> eventRoot, final CriteriaQuery<?> query,
            final CriteriaBuilder cb, final Organisation organisation) {

        final Subquery<Long> subquery = query.subquery(Long.class);
        final Root<DeviceAuthorization> deviceAuthorizationRoot = subquery.from(DeviceAuthorization.class);
        subquery.select(deviceAuthorizationRoot.get(DEVICE).get("id").as(Long.class));
        subquery.where(cb.and(cb.equal(deviceAuthorizationRoot.get("organisation"), organisation.getId()), cb.or(
                cb.equal(deviceAuthorizationRoot.get("functionGroup"), DeviceFunctionGroup.OWNER.ordinal()),
                cb.equal(deviceAuthorizationRoot.get("functionGroup"), DeviceFunctionGroup.MANAGEMENT.ordinal()))));

        return cb.in(eventRoot.get(DEVICE)).value(subquery);
    }

    @Override
    public Specification<Event> hasEventTypes(final List<EventType> eventTypes) throws ArgumentNullOrEmptyException {
        if (eventTypes == null || eventTypes.isEmpty()) {
            throw new ArgumentNullOrEmptyException("eventTypes");
        }

        return ((eventRoot, query, cb) -> this.createPredicateForHasEventTypes(eventRoot, eventTypes));
    }

    private Predicate createPredicateForHasEventTypes(final Root<Event> eventRoot, final List<EventType> eventTypes) {

        final Path<Event> eventType = eventRoot.<Event> get("eventType");

        return eventType.in(eventTypes);
    }

    @Override
    public Specification<Event> withDescription(final String description) throws ArgumentNullOrEmptyException {
        if (description == null) {
            throw new ArgumentNullOrEmptyException(DESCRIPTION);
        }

        return ((eventRoot, query, cb) -> cb.equal(eventRoot.<String> get(DESCRIPTION), description));
    }

    @Override
    public Specification<Event> startsWithDescription(final String descriptionStartsWith)
            throws ArgumentNullOrEmptyException {
        if (descriptionStartsWith == null) {
            throw new ArgumentNullOrEmptyException("descriptionStartsWith");
        }

        return ((eventRoot, query, cb) -> cb.like(eventRoot.<String> get(DESCRIPTION), descriptionStartsWith + "%"));
    }
}
