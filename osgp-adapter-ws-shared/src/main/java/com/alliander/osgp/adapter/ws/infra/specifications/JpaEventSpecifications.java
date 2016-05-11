/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.infra.specifications;

import java.util.Date;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.data.jpa.domain.Specification;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.entities.Event;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.exceptions.ArgumentNullOrEmptyException;
import com.alliander.osgp.domain.core.specifications.EventSpecifications;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup;

public class JpaEventSpecifications implements EventSpecifications {

    private static final String DEVICE = "device";

    @Override
    public Specification<Event> isCreatedAfter(final Date dateFrom) throws ArgumentNullOrEmptyException {

        if (dateFrom == null) {
            throw new ArgumentNullOrEmptyException("dateFrom");
        }

        return new Specification<Event>() {
            @Override
            public Predicate toPredicate(final Root<Event> eventRoot, final CriteriaQuery<?> query,
                    final CriteriaBuilder cb) {
                return cb.greaterThanOrEqualTo(eventRoot.<Date> get("dateTime"), dateFrom);
            }
        };
    }

    @Override
    public Specification<Event> isCreatedBefore(final Date dateUntil) throws ArgumentNullOrEmptyException {

        if (dateUntil == null) {
            throw new ArgumentNullOrEmptyException("dateUntil");
        }
        return new Specification<Event>() {
            @Override
            public Predicate toPredicate(final Root<Event> eventRoot, final CriteriaQuery<?> query,
                    final CriteriaBuilder cb) {
                return cb.lessThanOrEqualTo(eventRoot.<Date> get("dateTime"), dateUntil);
            }
        };
    }

    @Override
    public Specification<Event> isFromDevice(final Device device) throws ArgumentNullOrEmptyException {
        if (device == null) {
            throw new ArgumentNullOrEmptyException(DEVICE);
        }
        return new Specification<Event>() {
            @Override
            public Predicate toPredicate(final Root<Event> eventRoot, final CriteriaQuery<?> query,
                    final CriteriaBuilder cb) {
                return cb.equal(eventRoot.<Integer> get(DEVICE), device.getId());
            }
        };
    }

    @Override
    public Specification<Event> isAuthorized(final Organisation organisation) throws ArgumentNullOrEmptyException {

        if (organisation == null) {
            throw new ArgumentNullOrEmptyException("organisation");
        }

        return new Specification<Event>() {
            @Override
            public Predicate toPredicate(final Root<Event> eventRoot, final CriteriaQuery<?> query,
                    final CriteriaBuilder cb) {

                final Subquery<Integer> subquery = query.subquery(Integer.class);
                final Root<DeviceAuthorization> deviceAuthorizationRoot = subquery.from(DeviceAuthorization.class);
                subquery.select(deviceAuthorizationRoot.get(DEVICE).as(Integer.class));
                subquery.where(cb.and(
                        cb.equal(deviceAuthorizationRoot.get("organisation"), organisation.getId()),
                        cb.or(cb.equal(deviceAuthorizationRoot.get("functionGroup"),
                                DeviceFunctionGroup.OWNER.ordinal()),
                                cb.equal(deviceAuthorizationRoot.get("functionGroup"),
                                        DeviceFunctionGroup.MANAGEMENT.ordinal()))));

                return cb.in(eventRoot.get(DEVICE)).value(subquery);
            }
        };
    }
}
