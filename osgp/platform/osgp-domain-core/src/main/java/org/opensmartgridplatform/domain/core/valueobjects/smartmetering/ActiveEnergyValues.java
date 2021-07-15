/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class ActiveEnergyValues implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = 2837927976042182726L;

  private final OsgpMeterValue activeEnergyImport;
  private final OsgpMeterValue activeEnergyExport;
  private final OsgpMeterValue activeEnergyImportTariffOne;
  // may be null
  private final OsgpMeterValue activeEnergyImportTariffTwo;
  private final OsgpMeterValue activeEnergyExportTariffOne;
  // may be null
  private final OsgpMeterValue activeEnergyExportTariffTwo;

  public ActiveEnergyValues(
      final OsgpMeterValue activeEnergyImport,
      final OsgpMeterValue activeEnergyExport,
      final OsgpMeterValue activeEnergyImportTariffOne,
      final OsgpMeterValue activeEnergyImportTariffTwo,
      final OsgpMeterValue activeEnergyExportTariffOne,
      final OsgpMeterValue activeEnergyExportTariffTwo) {
    this.activeEnergyImport = activeEnergyImport;
    this.activeEnergyExport = activeEnergyExport;
    this.activeEnergyImportTariffOne = activeEnergyImportTariffOne;
    this.activeEnergyImportTariffTwo = activeEnergyImportTariffTwo;
    this.activeEnergyExportTariffOne = activeEnergyExportTariffOne;
    this.activeEnergyExportTariffTwo = activeEnergyExportTariffTwo;
  }

  public OsgpMeterValue getActiveEnergyImport() {
    return this.activeEnergyImport;
  }

  public OsgpMeterValue getActiveEnergyExport() {
    return this.activeEnergyExport;
  }

  public OsgpMeterValue getActiveEnergyImportTariffOne() {
    return this.activeEnergyImportTariffOne;
  }

  public OsgpMeterValue getActiveEnergyImportTariffTwo() {
    return this.activeEnergyImportTariffTwo;
  }

  public OsgpMeterValue getActiveEnergyExportTariffOne() {
    return this.activeEnergyExportTariffOne;
  }

  public OsgpMeterValue getActiveEnergyExportTariffTwo() {
    return this.activeEnergyExportTariffTwo;
  }
}
