// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.util.Date;

public class PeriodicMeterReadsResponseItemDto extends MeterReadsResponseDto {

  private static final long serialVersionUID = 2123390296585369208L;

  final AmrProfileStatusCodeDto amrProfileStatusCode;

  /**
   * Constructor taking all data. Use for conversion purposes, when all fields need to be copied.
   *
   * @param logTime the time the meter reads are logged on the device
   * @param activeEnergyValues the active energy values
   * @param amrProfileStatusCode the value of amrProfileStatusCode
   */
  public PeriodicMeterReadsResponseItemDto(
      final Date logTime,
      final ActiveEnergyValuesDto activeEnergyValues,
      final AmrProfileStatusCodeDto amrProfileStatusCode) {

    super(logTime, activeEnergyValues);

    this.amrProfileStatusCode = amrProfileStatusCode;
  }

  /**
   * Constructor for monthly reads. Does not hold a AMR profile status.
   *
   * @param logTime the time the meter reads are logged on the device
   * @param activeEnergyImportTariffOne the value of activeEnergyImportTariffOne
   * @param activeEnergyImportTariffTwo the value of activeEnergyImportTariffTwo
   * @param activeEnergyExportTariffOne the value of activeEnergyExportTariffOne
   * @param activeEnergyExportTariffTwo the value of activeEnergyExportTariffTwo
   */
  public PeriodicMeterReadsResponseItemDto(
      final Date logTime,
      final DlmsMeterValueDto activeEnergyImportTariffOne,
      final DlmsMeterValueDto activeEnergyImportTariffTwo,
      final DlmsMeterValueDto activeEnergyExportTariffOne,
      final DlmsMeterValueDto activeEnergyExportTariffTwo) {

    this(
        logTime,
        new ActiveEnergyValuesDto(
            null,
            null,
            activeEnergyImportTariffOne,
            activeEnergyImportTariffTwo,
            activeEnergyExportTariffOne,
            activeEnergyExportTariffTwo),
        null);
  }

  /**
   * Constructor for daily reads. Holds tariff values and AMR profile status.
   *
   * @param logTime the time the meter reads are logged on the device
   * @param activeEnergyImportTariffOne the value of activeEnergyImportTariffOne
   * @param activeEnergyImportTariffTwo the value of activeEnergyImportTariffTwo
   * @param activeEnergyExportTariffOne the value of activeEnergyExportTariffOne
   * @param activeEnergyExportTariffTwo the value of activeEnergyExportTariffTwo
   * @param amrProfileStatusCode the value of amrProfileStatusCode
   */
  public PeriodicMeterReadsResponseItemDto(
      final Date logTime,
      final DlmsMeterValueDto activeEnergyImportTariffOne,
      final DlmsMeterValueDto activeEnergyImportTariffTwo,
      final DlmsMeterValueDto activeEnergyExportTariffOne,
      final DlmsMeterValueDto activeEnergyExportTariffTwo,
      final AmrProfileStatusCodeDto amrProfileStatusCode) {
    this(
        logTime,
        new ActiveEnergyValuesDto(
            null,
            null,
            activeEnergyImportTariffOne,
            activeEnergyImportTariffTwo,
            activeEnergyExportTariffOne,
            activeEnergyExportTariffTwo),
        amrProfileStatusCode);
  }

  /**
   * Constructor for interval reads.
   *
   * @param logTime the time the meter reads are logged on the device
   * @param activeEnergyImport the value of activeEnergyImport
   * @param activeEnergyExport the value of activeEnergyExport
   * @param amrProfileStatusCode the value of amrProfileStatusCode
   */
  public PeriodicMeterReadsResponseItemDto(
      final Date logTime,
      final DlmsMeterValueDto activeEnergyImport,
      final DlmsMeterValueDto activeEnergyExport,
      final AmrProfileStatusCodeDto amrProfileStatusCode) {

    super(
        logTime,
        new ActiveEnergyValuesDto(activeEnergyImport, activeEnergyExport, null, null, null, null));

    this.amrProfileStatusCode = amrProfileStatusCode;
  }

  public AmrProfileStatusCodeDto getAmrProfileStatusCode() {
    return this.amrProfileStatusCode;
  }
}
