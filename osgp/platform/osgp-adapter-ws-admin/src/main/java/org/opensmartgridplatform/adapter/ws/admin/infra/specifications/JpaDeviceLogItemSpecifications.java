// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.admin.infra.specifications;

import static org.opensmartgridplatform.shared.utils.SearchUtil.replaceAndEscapeWildcards;

import java.time.Instant;
import java.time.ZonedDateTime;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.adapter.ws.admin.application.specifications.DeviceLogItemSpecifications;
import org.opensmartgridplatform.logging.domain.entities.DeviceLogItem;
import org.springframework.data.jpa.domain.Specification;

public class JpaDeviceLogItemSpecifications implements DeviceLogItemSpecifications {

  private static final String DEVICE_IDENTIFICATION = "deviceIdentification";
  private static final String ORGANISATION_IDENTIFICATION = "organisationIdentification";
  private static final String MODIFICATION_TIME = "modificationTime";

  private static final Specification<DeviceLogItem> ALL =
      (final Root<DeviceLogItem> r, final CriteriaQuery<?> q, final CriteriaBuilder cb) -> cb.and();

  @Override
  public Specification<DeviceLogItem> hasDeviceIdentification(final String deviceIdentification) {
    if (StringUtils.isAllBlank(deviceIdentification)) {
      return ALL;
    } else {
      return (final Root<DeviceLogItem> r, final CriteriaQuery<?> q, final CriteriaBuilder cb) ->
          cb.like(
              cb.upper(r.<String>get(DEVICE_IDENTIFICATION)),
              replaceAndEscapeWildcards(deviceIdentification.toUpperCase()));
    }
  }

  @Override
  public Specification<DeviceLogItem> hasOrganisationIdentification(
      final String organisationIdentification) {
    if (StringUtils.isAllBlank(organisationIdentification)) {
      return ALL;
    } else {
      return (final Root<DeviceLogItem> r, final CriteriaQuery<?> q, final CriteriaBuilder cb) ->
          cb.like(
              cb.upper(r.<String>get(ORGANISATION_IDENTIFICATION)),
              replaceAndEscapeWildcards(organisationIdentification.toUpperCase()));
    }
  }

  @Override
  public Specification<DeviceLogItem> hasStartDate(final ZonedDateTime startDate) {
    if (startDate == null) {
      return ALL;
    } else {
      return (final Root<DeviceLogItem> r, final CriteriaQuery<?> q, final CriteriaBuilder cb) ->
          cb.greaterThanOrEqualTo(r.<Instant>get(MODIFICATION_TIME), startDate.toInstant());
    }
  }

  @Override
  public Specification<DeviceLogItem> hasEndDate(final ZonedDateTime endDate) {
    if (endDate == null) {
      return ALL;
    } else {
      return (final Root<DeviceLogItem> r, final CriteriaQuery<?> q, final CriteriaBuilder cb) ->
          cb.lessThanOrEqualTo(r.<Instant>get(MODIFICATION_TIME), endDate.toInstant());
    }
  }
}
