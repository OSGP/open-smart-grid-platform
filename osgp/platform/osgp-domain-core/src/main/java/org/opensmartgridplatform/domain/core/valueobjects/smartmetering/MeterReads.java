/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Date;

public class MeterReads extends ActionResponse implements Serializable {
  private static final long serialVersionUID = -297320204916085999L;

  private final Date logTime;

  private final ActiveEnergyValues activeEnergyValues;

  public MeterReads(final Date logTime, final ActiveEnergyValues activeEnergyValues) {
    super();
    this.logTime = new Date(logTime.getTime());
    this.activeEnergyValues = activeEnergyValues;
  }

  public Date getLogTime() {
    return new Date(this.logTime.getTime());
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
