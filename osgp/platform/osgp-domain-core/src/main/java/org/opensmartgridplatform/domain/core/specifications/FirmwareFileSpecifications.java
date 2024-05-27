// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.specifications;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.SetJoin;
import java.util.List;
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

    final List<String> modelCodes = deviceModels.stream().map(DeviceModel::getModelCode).toList();

    final SetJoin<FirmwareFile, DeviceModel> join =
        firmwareFileRoot.joinSet("deviceModels", JoinType.INNER);
    return cb.in(join.<List<String>>get("modelCode")).value(modelCodes);
  }
}
