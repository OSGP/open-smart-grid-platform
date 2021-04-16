/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.util.Date;

public class PeriodicMeterReads extends MeterReads {

  private static final long serialVersionUID = -7981853503300669899L;

  private final AmrProfileStatusCode amrProfileStatusCode;

  public PeriodicMeterReads(
      final Date logTime,
      final ActiveEnergyValues activeEnergyValues,
      final AmrProfileStatusCode amrProfileStatusCode) {
    super(logTime, activeEnergyValues);
    this.amrProfileStatusCode = amrProfileStatusCode;
  }

  /** Constructor for interval reads. */
  public PeriodicMeterReads(
      final Date logTime,
      final OsgpMeterValue activeEnergyImport,
      final OsgpMeterValue activeEnergyExport,
      final AmrProfileStatusCode amrProfileStatusCode) {
    this(
        logTime,
        new ActiveEnergyValues(activeEnergyImport, activeEnergyExport, null, null, null, null),
        amrProfileStatusCode);
  }

  /** Constructor for monthly reads. Does not hold a AMR profile status. */
  public PeriodicMeterReads(
      final Date logTime,
      final OsgpMeterValue activeEnergyImportTariffOne,
      final OsgpMeterValue activeEnergyImportTariffTwo,
      final OsgpMeterValue activeEnergyExportTariffOne,
      final OsgpMeterValue activeEnergyExportTariffTwo) {
    this(
        logTime,
        activeEnergyImportTariffOne,
        activeEnergyImportTariffTwo,
        activeEnergyExportTariffOne,
        activeEnergyExportTariffTwo,
        null);
  }

  /** Constructor for daily reads. Holds tariff values and AMR profile status. */
  public PeriodicMeterReads(
      final Date logTime,
      final OsgpMeterValue activeEnergyImportTariffOne,
      final OsgpMeterValue activeEnergyImportTariffTwo,
      final OsgpMeterValue activeEnergyExportTariffOne,
      final OsgpMeterValue activeEnergyExportTariffTwo,
      final AmrProfileStatusCode amrProfileStatusCode) {
    super(
        logTime,
        new ActiveEnergyValues(
            null,
            null,
            activeEnergyImportTariffOne,
            activeEnergyImportTariffTwo,
            activeEnergyExportTariffOne,
            activeEnergyExportTariffTwo));
    this.amrProfileStatusCode = amrProfileStatusCode;
  }

  public AmrProfileStatusCode getAmrProfileStatusCode() {
    return this.amrProfileStatusCode;
  }
}
