// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.time.Instant;

public class MeterReads extends ActionResponse implements Serializable {
  private static final long serialVersionUID = -297320204916085999L;

  private final Instant logTime;

  private final ActiveEnergyValues activeEnergyValues;

  public MeterReads(final Instant logTime, final ActiveEnergyValues activeEnergyValues) {
    super();
    this.logTime = logTime;
    this.activeEnergyValues = activeEnergyValues;
  }

  public Instant getLogTime() {
    return this.logTime;
  }

  public OsgpMeterValue getActiveEnergyImportTariffOne() {
    return this.activeEnergyValues.getActiveEnergyImportTariffOne();
  }

  public OsgpMeterValue getActiveEnergyImportTariffTwo() {
    return this.activeEnergyValues.getActiveEnergyImportTariffTwo();
  }

  public OsgpMeterValue getActiveEnergyExportTariffOne() {
    return this.activeEnergyValues.getActiveEnergyExportTariffOne();
  }

  public OsgpMeterValue getActiveEnergyExportTariffTwo() {
    return this.activeEnergyValues.getActiveEnergyExportTariffTwo();
  }

  public OsgpMeterValue getActiveEnergyImport() {
    return this.activeEnergyValues.getActiveEnergyImport();
  }

  public OsgpMeterValue getActiveEnergyExport() {
    return this.activeEnergyValues.getActiveEnergyExport();
  }

  @Override
  public String toString() {
    return "MeterReads [logTime="
        + this.logTime
        + ", activeEnergyImport="
        + this.activeEnergyValues.getActiveEnergyImport()
        + ", activeEnergyExport="
        + this.activeEnergyValues.getActiveEnergyExport()
        + ", activeEnergyImportTariffOne="
        + this.activeEnergyValues.getActiveEnergyImportTariffOne()
        + ", activeEnergyImportTariffTwo="
        + this.activeEnergyValues.getActiveEnergyImportTariffTwo()
        + ", activeEnergyExportTariffOne="
        + this.activeEnergyValues.getActiveEnergyExportTariffOne()
        + ", activeEnergyExportTariffTwo="
        + this.activeEnergyValues.getActiveEnergyExportTariffTwo()
        + "]";
  }
}
