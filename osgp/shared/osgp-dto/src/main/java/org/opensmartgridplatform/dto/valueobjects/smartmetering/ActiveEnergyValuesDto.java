// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class ActiveEnergyValuesDto implements Serializable {

  private static final long serialVersionUID = 5775074609152210861L;

  private final DlmsMeterValueDto activeEnergyImport;
  private final DlmsMeterValueDto activeEnergyExport;
  private final DlmsMeterValueDto activeEnergyImportTariffOne;
  private final DlmsMeterValueDto activeEnergyImportTariffTwo;
  private final DlmsMeterValueDto activeEnergyExportTariffOne;
  private final DlmsMeterValueDto activeEnergyExportTariffTwo;

  public ActiveEnergyValuesDto(
      final DlmsMeterValueDto activeEnergyImport,
      final DlmsMeterValueDto activeEnergyExport,
      final DlmsMeterValueDto activeEnergyImportTariffOne,
      final DlmsMeterValueDto activeEnergyImportTariffTwo,
      final DlmsMeterValueDto activeEnergyExportTariffOne,
      final DlmsMeterValueDto activeEnergyExportTariffTwo) {

    this.activeEnergyImport = activeEnergyImport;
    this.activeEnergyImportTariffOne = activeEnergyImportTariffOne;
    this.activeEnergyImportTariffTwo = activeEnergyImportTariffTwo;
    this.activeEnergyExport = activeEnergyExport;
    this.activeEnergyExportTariffOne = activeEnergyExportTariffOne;
    this.activeEnergyExportTariffTwo = activeEnergyExportTariffTwo;
  }

  @Override
  public String toString() {
    return "ActiveEnergyValues[import="
        + this.activeEnergyImport
        + ", export="
        + this.activeEnergyExport
        + ", importTariffOne="
        + this.activeEnergyImportTariffOne
        + ", importTariffTwo="
        + this.activeEnergyImportTariffTwo
        + ", exportTariffOne="
        + this.activeEnergyExportTariffOne
        + ", exportTariffTwo="
        + this.activeEnergyExportTariffTwo
        + "]";
  }

  public DlmsMeterValueDto getActiveEnergyImport() {
    return this.activeEnergyImport;
  }

  public DlmsMeterValueDto getActiveEnergyExport() {
    return this.activeEnergyExport;
  }

  public DlmsMeterValueDto getActiveEnergyImportTariffOne() {
    return this.activeEnergyImportTariffOne;
  }

  public DlmsMeterValueDto getActiveEnergyImportTariffTwo() {
    return this.activeEnergyImportTariffTwo;
  }

  public DlmsMeterValueDto getActiveEnergyExportTariffOne() {
    return this.activeEnergyExportTariffOne;
  }

  public DlmsMeterValueDto getActiveEnergyExportTariffTwo() {
    return this.activeEnergyExportTariffTwo;
  }
}
