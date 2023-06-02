//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.infra.specifications;

import java.util.Date;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import org.opensmartgridplatform.domain.core.entities.DeviceAuthorization;
import org.opensmartgridplatform.domain.core.entities.Event;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.specifications.EventSpecifications;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunctionGroup;
import org.opensmartgridplatform.domain.core.valueobjects.EventType;
import org.springframework.data.jpa.domain.Specification;

public class JpaEventSpecifications implements EventSpecifications {

  private static final String DEVICE = "device";
  private static final String DEVICE_IDENTIFICATION = "deviceIdentification";
  private static final String DESCRIPTION = "description";
  private static final Specification<Event> NO_FILTER = (eventRoot, query, cb) -> cb.and();

  @Override
  public Specification<Event> isCreatedAfter(final Date dateFrom) {

    if (dateFrom == null) {
      return NO_FILTER;
    }

    return ((eventRoot, query, cb) ->
        cb.greaterThanOrEqualTo(eventRoot.<Date>get("dateTime"), dateFrom));
  }

  @Override
  public Specification<Event> isCreatedBefore(final Date dateUntil) {

    if (dateUntil == null) {
      return NO_FILTER;
    }

    return ((eventRoot, query, cb) ->
        cb.lessThanOrEqualTo(eventRoot.<Date>get("dateTime"), dateUntil));
  }

  @Override
  public Specification<Event> isFromDevice(final String deviceIdentification) {

    if (deviceIdentification == null) {
      return NO_FILTER;
    }

    return ((eventRoot, query, cb) ->
        cb.equal(eventRoot.<String>get(DEVICE_IDENTIFICATION), deviceIdentification));
  }

  @Override
  public Specification<Event> isAuthorized(final Organisation organisation) {

    if (organisation == null) {
      return NO_FILTER;
    }

    return ((eventRoot, query, cb) ->
        this.createPredicateForIsAuthorized(eventRoot, query, cb, organisation));
  }

  private Predicate createPredicateForIsAuthorized(
      final Root<Event> eventRoot,
      final CriteriaQuery<?> query,
      final CriteriaBuilder cb,
      final Organisation organisation) {

    final Subquery<String> subquery = query.subquery(String.class);
    final Root<DeviceAuthorization> deviceAuthorizationRoot =
        subquery.from(DeviceAuthorization.class);
    subquery.select(
        deviceAuthorizationRoot.get(DEVICE).get(DEVICE_IDENTIFICATION).as(String.class));
    subquery.where(
        cb.and(
            cb.equal(deviceAuthorizationRoot.get("organisation"), organisation.getId()),
            cb.or(
                cb.equal(
                    deviceAuthorizationRoot.get("functionGroup"),
                    DeviceFunctionGroup.OWNER.ordinal()),
                cb.equal(
                    deviceAuthorizationRoot.get("functionGroup"),
                    DeviceFunctionGroup.MANAGEMENT.ordinal()))));

    return cb.in(eventRoot.get(DEVICE_IDENTIFICATION)).value(subquery);
  }

  @Override
  public Specification<Event> hasEventTypes(final List<EventType> eventTypes) {
    if (eventTypes == null || eventTypes.isEmpty()) {
      return NO_FILTER;
    }

    return ((eventRoot, query, cb) -> this.createPredicateForHasEventTypes(eventRoot, eventTypes));
  }

  private Predicate createPredicateForHasEventTypes(
      final Root<Event> eventRoot, final List<EventType> eventTypes) {

    final Path<Event> eventType = eventRoot.<Event>get("eventType");

    return eventType.in(eventTypes);
  }

  @Override
  public Specification<Event> withDescription(final String description) {
    if (description == null) {
      return NO_FILTER;
    }

    return ((eventRoot, query, cb) -> cb.equal(eventRoot.<String>get(DESCRIPTION), description));
  }

  @Override
  public Specification<Event> startsWithDescription(final String descriptionStartsWith) {
    if (descriptionStartsWith == null) {
      return NO_FILTER;
    }

    return ((eventRoot, query, cb) ->
        cb.like(eventRoot.<String>get(DESCRIPTION), descriptionStartsWith + "%"));
  }
}
