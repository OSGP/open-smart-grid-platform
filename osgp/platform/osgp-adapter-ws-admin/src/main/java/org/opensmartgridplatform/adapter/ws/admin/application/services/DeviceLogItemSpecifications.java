/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.admin.application.services;

import static org.opensmartgridplatform.shared.utils.WildcardUtil.hasWildcards;
import static org.opensmartgridplatform.shared.utils.WildcardUtil.replaceWildcards;

import java.util.Date;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.opensmartgridplatform.logging.domain.entities.DeviceLogItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

public final class DeviceLogItemSpecifications {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceLogItemSpecifications.class);

    private static final String DEVICE_IDENTIFICATION = "deviceIdentification";
    private static final String ORGANISATION_IDENTIFICATION = "organisationIdentification";
    private static final String MODIFICATION_TIME = "modificationTime";

    private DeviceLogItemSpecifications() {
        // Prevents creation of DeviceLogItemSpecifications objects
    }

    public static final Specification<DeviceLogItem> NONE = (final Root<DeviceLogItem> r, final CriteriaQuery<?> q,
            final CriteriaBuilder cb) -> cb.or();

    public static final Specification<DeviceLogItem> ALL = (final Root<DeviceLogItem> r, final CriteriaQuery<?> q,
            final CriteriaBuilder cb) -> cb.and();

    public static Specification<DeviceLogItem> hasDeviceIdentification(final String deviceIdentification) {
        if (hasWildcards(deviceIdentification)) {
            LOGGER.info("Device identification contains wildcards");
            return (final Root<DeviceLogItem> r, final CriteriaQuery<?> q, final CriteriaBuilder cb) -> cb.like(
                    cb.upper(r.<String> get(DEVICE_IDENTIFICATION)),
                    replaceWildcards(deviceIdentification.toUpperCase()));
        } else {
            LOGGER.info("Device identification does not contain wildcards");
            return (final Root<DeviceLogItem> r, final CriteriaQuery<?> q, final CriteriaBuilder cb) -> cb.equal(
                    cb.upper(r.<String> get(DEVICE_IDENTIFICATION)),
                    replaceWildcards(deviceIdentification.toUpperCase()));
        }
    }

    public static Specification<DeviceLogItem> hasOrganisationIdentification(final String organisationIdentification) {
        if (hasWildcards(organisationIdentification)) {
            LOGGER.info("Organisation identification contains wildcards");
            return (final Root<DeviceLogItem> r, final CriteriaQuery<?> q, final CriteriaBuilder cb) -> cb.like(
                    cb.upper(r.<String> get(ORGANISATION_IDENTIFICATION)),
                    replaceWildcards(organisationIdentification.toUpperCase()));
        } else {
            LOGGER.info("Organisation identification does not contain wildcards");
            return (final Root<DeviceLogItem> r, final CriteriaQuery<?> q, final CriteriaBuilder cb) -> cb.equal(
                    cb.upper(r.<String> get(ORGANISATION_IDENTIFICATION)),
                    replaceWildcards(organisationIdentification.toUpperCase()));
        }

    }

    public static Specification<DeviceLogItem> hasStartDate(final Date startDate) {
        return (final Root<DeviceLogItem> r, final CriteriaQuery<?> q, final CriteriaBuilder cb) -> cb
                .greaterThanOrEqualTo(r.<Date> get(MODIFICATION_TIME), startDate);
    }

    public static Specification<DeviceLogItem> hasEndDate(final Date endDate) {
        return (final Root<DeviceLogItem> r, final CriteriaQuery<?> q, final CriteriaBuilder cb) -> cb
                .lessThanOrEqualTo(r.<Date> get(MODIFICATION_TIME), endDate);
    }

}
