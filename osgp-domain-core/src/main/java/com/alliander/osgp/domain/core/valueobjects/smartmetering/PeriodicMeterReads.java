/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

 import java.util.Date;

 public class PeriodicMeterReads extends MeterReads {

     private static final long serialVersionUID = -7981853503300669899L;

     private final AmrProfileStatusCode amrProfileStatusCode;

     public PeriodicMeterReads(final Date logTime, final long activeEnergyImportTariffOne,
             final Long activeEnergyImportTariffTwo, final long activeEnergyExportTariffOne,
             final Long activeEnergyExportTariffTwo) {
         this(logTime, activeEnergyImportTariffOne, activeEnergyImportTariffTwo, activeEnergyExportTariffOne,
                 activeEnergyExportTariffTwo, null);
     }

     public PeriodicMeterReads(final Date logTime, final long activeEnergyImportTariffOne,
             final Long activeEnergyImportTariffTwo, final long activeEnergyExportTariffOne,
             final Long activeEnergyExportTariffTwo, final AmrProfileStatusCode amrProfileStatusCode) {
         super(logTime, activeEnergyImportTariffOne, activeEnergyImportTariffTwo, activeEnergyExportTariffOne,
                 activeEnergyExportTariffTwo);
         this.amrProfileStatusCode = amrProfileStatusCode;
     }

     public AmrProfileStatusCode getAmrProfileStatusCode() {
         return this.amrProfileStatusCode;
     }

 }
