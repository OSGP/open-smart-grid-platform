/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters;

import java.util.Date;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActiveEnergyValues;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AmrProfileStatusCode;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.OsgpMeterValue;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PeriodicMeterReads;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AmrProfileStatusCodeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsMeterValueDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsResponseItemDto;

public class PeriodicMeterReadsResponseItemDtoConverter
    extends CustomConverter<PeriodicMeterReadsResponseItemDto, PeriodicMeterReads> {

  private final MapperFactory mapperFactory;

  public PeriodicMeterReadsResponseItemDtoConverter(final MapperFactory mapperFactory) {
    this.mapperFactory = mapperFactory;
  }

  @Override
  public PeriodicMeterReads convert(
      final PeriodicMeterReadsResponseItemDto source,
      final Type<? extends PeriodicMeterReads> destinationType,
      final MappingContext mappingContext) {
    final Date logTime = source.getLogTime();
    final ActiveEnergyValues activeEnergyValues =
        new ActiveEnergyValues(
            this.toOsgpMeterValue(source.getActiveEnergyImport()),
            this.toOsgpMeterValue(source.getActiveEnergyExport()),
            this.toOsgpMeterValue(source.getActiveEnergyImportTariffOne()),
            this.toOsgpMeterValue(source.getActiveEnergyImportTariffTwo()),
            this.toOsgpMeterValue(source.getActiveEnergyExportTariffOne()),
            this.toOsgpMeterValue(source.getActiveEnergyExportTariffTwo()));
    final AmrProfileStatusCode amrProfileStatusCode =
        this.toAmrProfileStatusCode(source.getAmrProfileStatusCode());
    return new PeriodicMeterReads(logTime, activeEnergyValues, amrProfileStatusCode);
  }

  private OsgpMeterValue toOsgpMeterValue(final DlmsMeterValueDto source) {
    return this.mapperFactory.getMapperFacade().map(source, OsgpMeterValue.class);
  }

  private AmrProfileStatusCode toAmrProfileStatusCode(final AmrProfileStatusCodeDto source) {
    return this.mapperFactory.getMapperFacade().map(source, AmrProfileStatusCode.class);
  }
}
