/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.admin.application.services;

import static org.opensmartgridplatform.shared.utils.WildcardUtil.replaceWildcards;

import java.time.ZonedDateTime;
import java.util.Date;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.logging.domain.entities.DeviceLogItem;
import org.springframework.data.jpa.domain.Specification;

public final class DeviceLogItemSpecifications {

    private static final String DEVICE_IDENTIFICATION = "deviceIdentification";
    private static final String ORGANISATION_IDENTIFICATION = "organisationIdentification";
    private static final String MODIFICATION_TIME = "modificationTime";

    private static final Specification<DeviceLogItem> ALL = (final Root<DeviceLogItem> r, final CriteriaQuery<?> q,
            final CriteriaBuilder cb) -> cb.and();

    private DeviceLogItemSpecifications() {
        // Prevents creation of DeviceLogItemSpecifications objects
    }

    public static Specification<DeviceLogItem> hasDeviceIdentification(final String deviceIdentification) {
        if (StringUtils.isAllBlank(deviceIdentification)) {
            return ALL;
        } else {
            return (final Root<DeviceLogItem> r, final CriteriaQuery<?> q, final CriteriaBuilder cb) -> cb.like(
                    cb.upper(r.<String> get(DEVICE_IDENTIFICATION)),
                    replaceWildcards(deviceIdentification.toUpperCase()));
        }
    }

    public static Specification<DeviceLogItem> hasOrganisationIdentification(final String organisationIdentification) {
        if (StringUtils.isAllBlank(organisationIdentification)) {
            return ALL;
        } else {
            return (final Root<DeviceLogItem> r, final CriteriaQuery<?> q, final CriteriaBuilder cb) -> cb.like(
                    cb.upper(r.<String> get(ORGANISATION_IDENTIFICATION)),
                    replaceWildcards(organisationIdentification.toUpperCase()));
        }
    }

    public static Specification<DeviceLogItem> hasStartDate(final ZonedDateTime startDate) {
        if (startDate == null) {
            return ALL;
        } else {
            return (final Root<DeviceLogItem> r, final CriteriaQuery<?> q, final CriteriaBuilder cb) -> cb
                    .greaterThanOrEqualTo(r.<Date> get(MODIFICATION_TIME), Date.from(startDate.toInstant()));
        }
    }

    public static Specification<DeviceLogItem> hasEndDate(final ZonedDateTime endDate) {
        if (endDate == null) {
            return ALL;
        } else {
            return (final Root<DeviceLogItem> r, final CriteriaQuery<?> q, final CriteriaBuilder cb) -> cb
                    .lessThanOrEqualTo(r.<Date> get(MODIFICATION_TIME), Date.from(endDate.toInstant()));
        }
    }

}
