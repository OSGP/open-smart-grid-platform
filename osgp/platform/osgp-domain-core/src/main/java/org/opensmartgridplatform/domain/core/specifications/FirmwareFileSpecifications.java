/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.specifications;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;
import org.opensmartgridplatform.domain.core.entities.DeviceModel;
import org.opensmartgridplatform.domain.core.entities.FirmwareFile;
import org.springframework.data.jpa.domain.Specification;

public class FirmwareFileSpecifications {
  private static final Specification<FirmwareFile> ALWAYS_TRUE =
      (firmwareFileRoot, query, cb) -> cb.conjunction();
  private static final Specification<FirmwareFile> ALWAYS_FALSE =
      (firmwareFileRoot, query, cb) -> cb.disjunction();

  private FirmwareFileSpecifications() {
    // Utility classes should not have public constructors (java:S1118)
  }

  public static Specification<FirmwareFile> forDeviceModel(final DeviceModel deviceModel) {
    if (deviceModel == null) {
      return ALWAYS_FALSE;
    }
    return (firmwareFileRoot, query, cb) ->
        getFirmwareFilesForDeviceModel(firmwareFileRoot, cb, deviceModel);
  }

  public static Specification<FirmwareFile> forDeviceModels(final List<DeviceModel> deviceModels) {
    if (deviceModels == null || deviceModels.isEmpty()) {
      return ALWAYS_FALSE;
    }
    return (firmwareFileRoot, query, cb) ->
        getFirmwareFilesForDeviceModels(firmwareFileRoot, cb, deviceModels);
  }

  public static Specification<FirmwareFile> forActiveFirmwareFilesOnly(final Boolean active) {
    if (active == null) {
      return ALWAYS_TRUE;
    }
    return (firmwareFileRoot, query, cb) ->
        cb.equal(firmwareFileRoot.<Boolean>get("active"), active);
  }

  private static Predicate getFirmwareFilesForDeviceModel(
      final Root<FirmwareFile> firmwareFileRoot,
      final CriteriaBuilder cb,
      final DeviceModel deviceModel) {

    final SetJoin<FirmwareFile, DeviceModel> join =
        firmwareFileRoot.joinSet("deviceModels", JoinType.INNER);
    return cb.equal(join.<String>get("modelCode"), deviceModel.getModelCode());
  }

  private static Predicate getFirmwareFilesForDeviceModels(
      final Root<FirmwareFile> firmwareFileRoot,
      final CriteriaBuilder cb,
      final List<DeviceModel> deviceModels) {

    final List<String> modelCodes =
        deviceModels.stream().map(DeviceModel::getModelCode).collect(Collectors.toList());

    final SetJoin<FirmwareFile, DeviceModel> join =
        firmwareFileRoot.joinSet("deviceModels", JoinType.INNER);
    return cb.in(join.<List<String>>get("modelCode")).value(modelCodes);
  }
}
