//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.valueobjects;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.domain.core.entities.FirmwareModule;
import org.opensmartgridplatform.domain.core.repositories.FirmwareModuleRepository;

public class FirmwareModuleData implements Serializable {

  /**
   * Description of the FirmwareModule for which the module version in a FirmwareFile should equal
   * {@link #moduleVersionComm}.
   */
  public static final String MODULE_DESCRIPTION_COMM = "communication_module_active_firmware";
  /**
   * Description of the FirmwareModule for which the module version in a FirmwareFile should equal
   * {@link #moduleVersionFunc} with devices other than smart meters.
   */
  public static final String MODULE_DESCRIPTION_FUNC = "functional";
  /**
   * Description of the FirmwareModule for which the module version in a FirmwareFile should equal
   * {@link #moduleVersionFunc} with smart meter devices.
   */
  public static final String MODULE_DESCRIPTION_FUNC_SMART_METERING = "active_firmware";
  /**
   * Description of the FirmwareModule for which the module version in a FirmwareFile should equal
   * {@link #moduleVersionMa}.
   */
  public static final String MODULE_DESCRIPTION_MA = "module_active_firmware";
  /**
   * Description of the FirmwareModule for which the module version in a FirmwareFile should equal
   * {@link #moduleVersionMbus}.
   */
  public static final String MODULE_DESCRIPTION_MBUS = "m_bus";
  /**
   * Description of the FirmwareModule for which the module version in a FirmwareFile should equal
   * {@link #moduleVersionSec}.
   */
  public static final String MODULE_DESCRIPTION_SEC = "security";
  /**
   * Description of the FirmwareModule for which the module version in a FirmwareFile should equal
   * {@link #moduleVersionMBusDriverActive}.
   */
  public static final String MODULE_DESCRIPTION_MBUS_DRIVER_ACTIVE = "m_bus_driver_active_firmware";
  /**
   * Description of the FirmwareModule for which the module version in a FirmwareFile should equal
   * {@link #moduleVersionSimple}.
   */
  public static final String MODULE_DESCRIPTION_SIMPLE_VERSION_INFO = "simple_version_info";

  private static final long serialVersionUID = 3479817852183883103L;
  private final String moduleVersionComm;
  private final String moduleVersionFunc;
  private final String moduleVersionMa;
  private final String moduleVersionMbus;
  private final String moduleVersionSec;
  private final String moduleVersionMBusDriverActive;
  private final String moduleVersionSimple;

  public FirmwareModuleData(
      final String moduleVersionComm,
      final String moduleVersionFunc,
      final String moduleVersionMa,
      final String moduleVersionMbus,
      final String moduleVersionSec,
      final String moduleVersionMBusDriverActive,
      final String moduleVersionSimple) {
    this.moduleVersionComm = moduleVersionComm;
    this.moduleVersionFunc = moduleVersionFunc;
    this.moduleVersionMa = moduleVersionMa;
    this.moduleVersionMbus = moduleVersionMbus;
    this.moduleVersionSec = moduleVersionSec;
    this.moduleVersionMBusDriverActive = moduleVersionMBusDriverActive;
    this.moduleVersionSimple = moduleVersionSimple;
  }

  public String getModuleVersionComm() {
    return this.moduleVersionComm;
  }

  public String getModuleVersionFunc() {
    return this.moduleVersionFunc;
  }

  public String getModuleVersionMa() {
    return this.moduleVersionMa;
  }

  public String getModuleVersionMbus() {
    return this.moduleVersionMbus;
  }

  public String getModuleVersionSec() {
    return this.moduleVersionSec;
  }

  public String getModuleVersionMBusDriverActive() {
    return this.moduleVersionMBusDriverActive;
  }

  public String getModuleVersionSimple() {
    return this.moduleVersionSimple;
  }

  /**
   * Returns the FirmwareModuleData as a map of FirmwareModule to version String.
   *
   * <p>This should probably be a temporary workaround, until the use of the FirmwareModuleData is
   * replaced by some other code that better matches the generic firmware module set up that does
   * not assume a fixed number of module types.
   *
   * @param firmwareModuleRepository a repository to be able to retrieve the known firmware modules
   *     from the FirmwareModuleData from the database.
   * @param isForSmartMeters if {@code true} {@link #moduleVersionFunc} is mapped to the {@value
   *     #MODULE_DESCRIPTION_FUNC_SMART_METERING} firmware module; otherwise it is mapped to the
   *     {@value #MODULE_DESCRIPTION_FUNC} firmware module.
   * @return firmware module versions by module.
   */
  public Map<FirmwareModule, String> getVersionsByModule(
      final FirmwareModuleRepository firmwareModuleRepository, final boolean isForSmartMeters) {

    final Map<FirmwareModule, String> versionsByModule = new TreeMap<>();

    this.addVersionForModuleIfNonBlank(
        versionsByModule,
        firmwareModuleRepository,
        this.moduleVersionComm,
        MODULE_DESCRIPTION_COMM);
    if (isForSmartMeters) {
      this.addVersionForModuleIfNonBlank(
          versionsByModule,
          firmwareModuleRepository,
          this.moduleVersionFunc,
          MODULE_DESCRIPTION_FUNC_SMART_METERING);
    } else {
      this.addVersionForModuleIfNonBlank(
          versionsByModule,
          firmwareModuleRepository,
          this.moduleVersionFunc,
          MODULE_DESCRIPTION_FUNC);
    }
    this.addVersionForModuleIfNonBlank(
        versionsByModule, firmwareModuleRepository, this.moduleVersionMa, MODULE_DESCRIPTION_MA);
    this.addVersionForModuleIfNonBlank(
        versionsByModule,
        firmwareModuleRepository,
        this.moduleVersionMbus,
        MODULE_DESCRIPTION_MBUS);
    this.addVersionForModuleIfNonBlank(
        versionsByModule, firmwareModuleRepository, this.moduleVersionSec, MODULE_DESCRIPTION_SEC);
    this.addVersionForModuleIfNonBlank(
        versionsByModule,
        firmwareModuleRepository,
        this.moduleVersionMBusDriverActive,
        MODULE_DESCRIPTION_MBUS_DRIVER_ACTIVE);
    this.addVersionForModuleIfNonBlank(
        versionsByModule,
        firmwareModuleRepository,
        this.moduleVersionSimple,
        MODULE_DESCRIPTION_SIMPLE_VERSION_INFO);
    return versionsByModule;
  }

  private void addVersionForModuleIfNonBlank(
      final Map<FirmwareModule, String> versionsByModule,
      final FirmwareModuleRepository firmwareModuleRepository,
      final String moduleVersion,
      final String moduleDescription) {
    if (!StringUtils.isEmpty(moduleVersion)) {
      versionsByModule.put(
          firmwareModuleRepository.findByDescriptionIgnoreCase(moduleDescription), moduleVersion);
    }
  }

  @Override
  public String toString() {
    return "FirmwareModuleData [moduleVersionComm="
        + this.moduleVersionComm
        + ", moduleVersionFunc="
        + this.moduleVersionFunc
        + ", moduleVersionMa="
        + this.moduleVersionMa
        + ", moduleVersionMbus="
        + this.moduleVersionMbus
        + ", moduleVersionSec="
        + this.moduleVersionSec
        + ", moduleVersionMBusDriverActive="
        + this.moduleVersionMBusDriverActive
        + ", moduleVersionSimple="
        + this.moduleVersionSimple
        + "]";
  }
}
