/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.database.logging;

import static org.opensmartgridplatform.shared.utils.SearchUtil.replaceAndEscapeWildcards;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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
