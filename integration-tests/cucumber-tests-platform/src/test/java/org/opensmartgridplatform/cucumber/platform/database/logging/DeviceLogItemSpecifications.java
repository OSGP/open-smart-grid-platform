// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.database.logging;

import static org.opensmartgridplatform.shared.utils.SearchUtil.replaceAndEscapeWildcards;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.opensmartgridplatform.logging.domain.entities.DeviceLogItem;
import org.springframework.data.jpa.domain.Specification;

public class DeviceLogItemSpecifications {

  private static final String DEVICE_IDENTIFICATION = "deviceIdentification";
  private static final String DECODED_MESSAGE = "decodedMessage";

  public static Specification<DeviceLogItem> hasDeviceIdentification(
      final String deviceIdentification) {
    return (final Root<DeviceLogItem> r, final CriteriaQuery<?> q, final CriteriaBuilder cb) ->
        cb.like(
            cb.upper(r.<String>get(DEVICE_IDENTIFICATION)),
            replaceAndEscapeWildcards(deviceIdentification.toUpperCase()));
  }

  public static Specification<DeviceLogItem> hasDecodedMessageContaining(final String messagePart) {
    return (final Root<DeviceLogItem> r, final CriteriaQuery<?> q, final CriteriaBuilder cb) ->
        cb.like(cb.upper(r.<String>get(DECODED_MESSAGE)), addWildcards(messagePart).toUpperCase());
  }

  private static String addWildcards(final String searchString) {
    return "%" + searchString + "%";
  }
}
