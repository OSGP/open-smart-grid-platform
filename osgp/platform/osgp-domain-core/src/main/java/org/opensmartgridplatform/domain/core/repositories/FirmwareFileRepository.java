/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.repositories;

import java.util.List;
import org.opensmartgridplatform.domain.core.entities.DeviceModel;
import org.opensmartgridplatform.domain.core.entities.FirmwareFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FirmwareFileRepository extends JpaRepository<FirmwareFile, Long> {

  /**
   * Returns a list of firmware files applicable for {@code deviceModel} which contain firmware
   * modules for {@code moduleDescription} at {@code moduleVersion}.
   *
   * <p>To find firmware files that have a combination of modules at specific versions, one can
   * combine the results of the separate lists of firmware files for the different versions, or just
   * retrieve the list for one given version (which is not expected to contain many results) and
   * examine that in code to filter it further.
   *
   * <p>This seems simpler and more maintainable than trying to provide a (conceptually cleaner)
   * single repository method to retrieve the single firmware file identified by the device model
   * and the combination of versions for all included modules.
   *
   * @param deviceModel a device model with which the firmware file is usable.
   * @param moduleDescription a unique description of a type of firmware module that is contained in
   *     the firmware file.
   * @param moduleVersion the specific version for which a firmware module of the type according to
   *     {@code moduleDescription} is in the firmware file.
   * @return a list of firmware files satisfying the given parameters.
   */
  @Query(
      "SELECT DISTINCT ff FROM FirmwareFile ff JOIN FETCH ff.firmwareModules fffm JOIN FETCH fffm.firmwareModule fm "
          + "WHERE ff.id IN (SELECT ff2.id FROM FirmwareFile ff2 JOIN ff2.firmwareModules fffm2 JOIN fffm2.firmwareModule fm2 "
          + "WHERE fffm2.moduleVersion = :moduleVersion AND fm2.description = :description "
          + "AND :deviceModel MEMBER OF ff2.deviceModels)")
  List<FirmwareFile> findFirmwareFilesForDeviceModelContainingModuleWithVersion(
      @Param("deviceModel") DeviceModel deviceModel,
      @Param("description") String moduleDescription,
      @Param("moduleVersion") String moduleVersion);

  List<FirmwareFile> findByFilename(String filename);

  @Query(
      "SELECT ff FROM FirmwareFile ff JOIN FETCH ff.firmwareModules fffm JOIN FETCH fffm.firmwareModule fm "
          + "WHERE ff.identification = :identification")
  FirmwareFile findByIdentification(@Param("identification") String identification);

  @Query("SELECT ff FROM FirmwareFile ff WHERE :deviceModel MEMBER OF ff.deviceModels")
  List<FirmwareFile> findByDeviceModel(@Param("deviceModel") DeviceModel deviceModel);
}
