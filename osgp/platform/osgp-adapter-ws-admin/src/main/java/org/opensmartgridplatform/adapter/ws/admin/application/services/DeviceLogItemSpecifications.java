package org.opensmartgridplatform.adapter.ws.admin.application.services;

import java.util.Date;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.opensmartgridplatform.logging.domain.entities.DeviceLogItem;
import org.springframework.data.jpa.domain.Specification;

public class DeviceLogItemSpecifications {

    private static final String DEVICE_IDENTIFICATION = "deviceIdentification";
    private static final String ORGANISATION_IDENTIFICATION = "organisationIdentification";
    private static final String CREATION_TIME = "creationTime";

    public static final Specification<DeviceLogItem> NONE = (final Root<DeviceLogItem> r, final CriteriaQuery<?> q,
            final CriteriaBuilder cb) -> cb.or();

    public static final Specification<DeviceLogItem> ALL = (final Root<DeviceLogItem> r, final CriteriaQuery<?> q,
            final CriteriaBuilder cb) -> cb.and();

    public static Specification<DeviceLogItem> hasDeviceIdentification(final String deviceIdentification) {
        return (final Root<DeviceLogItem> r, final CriteriaQuery<?> q, final CriteriaBuilder cb) -> cb
                .like(cb.upper(r.<String> get(DEVICE_IDENTIFICATION)), deviceIdentification.toUpperCase());
    }

    public static Specification<DeviceLogItem> hasOrganisationIdentification(final String organisationIdentification) {
        return (final Root<DeviceLogItem> r, final CriteriaQuery<?> q, final CriteriaBuilder cb) -> cb
                .like(cb.upper(r.<String> get(ORGANISATION_IDENTIFICATION)), organisationIdentification.toUpperCase());
    }

    public static Specification<DeviceLogItem> hasStartDate(final Date startDate) {
        return (final Root<DeviceLogItem> r, final CriteriaQuery<?> q, final CriteriaBuilder cb) -> cb
                .greaterThanOrEqualTo(r.<Date> get(CREATION_TIME), startDate);
    }

    public static Specification<DeviceLogItem> hasEndDate(final Date endDate) {
        return (final Root<DeviceLogItem> r, final CriteriaQuery<?> q, final CriteriaBuilder cb) -> cb
                .lessThanOrEqualTo(r.<Date> get(CREATION_TIME), endDate);
    }

}
