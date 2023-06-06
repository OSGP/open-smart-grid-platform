// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.da.application.mapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.joda.time.DateTime;
import org.opensmartgridplatform.domain.da.measurements.Measurement;
import org.opensmartgridplatform.domain.da.measurements.MeasurementGroup;
import org.opensmartgridplatform.domain.da.measurements.MeasurementReport;
import org.opensmartgridplatform.domain.da.measurements.MeasurementReportHeader;
import org.opensmartgridplatform.domain.da.measurements.elements.BitmaskMeasurementElement;
import org.opensmartgridplatform.domain.da.measurements.elements.FloatMeasurementElement;
import org.opensmartgridplatform.domain.da.measurements.elements.TimestampMeasurementElement;
import org.opensmartgridplatform.domain.da.valueobjects.GetDeviceModelRequest;
import org.opensmartgridplatform.domain.da.valueobjects.GetHealthStatusRequest;
import org.opensmartgridplatform.domain.da.valueobjects.GetPQValuesPeriodicRequest;
import org.opensmartgridplatform.domain.da.valueobjects.GetPQValuesRequest;
import org.opensmartgridplatform.dto.da.GetDeviceModelRequestDto;
import org.opensmartgridplatform.dto.da.GetHealthStatusRequestDto;
import org.opensmartgridplatform.dto.da.GetPQValuesPeriodicRequestDto;
import org.opensmartgridplatform.dto.da.GetPQValuesRequestDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementGroupDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementReportDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementReportHeaderDto;
import org.opensmartgridplatform.dto.da.measurements.elements.BitmaskMeasurementElementDto;
import org.opensmartgridplatform.dto.da.measurements.elements.FloatMeasurementElementDto;
import org.opensmartgridplatform.dto.da.measurements.elements.TimestampMeasurementElementDto;
import org.springframework.stereotype.Component;

@Component
public class DomainDistributionAutomationMapper extends ConfigurableMapper {

  @Override
  protected void configure(final MapperFactory factory) {
    factory.getConverterFactory().registerConverter(new PassThroughConverter(DateTime.class));
    factory
        .classMap(GetDeviceModelRequest.class, GetDeviceModelRequestDto.class)
        .byDefault()
        .register();
    factory
        .classMap(GetHealthStatusRequest.class, GetHealthStatusRequestDto.class)
        .byDefault()
        .register();
    factory.classMap(GetPQValuesRequest.class, GetPQValuesRequestDto.class).byDefault().register();
    factory
        .classMap(GetPQValuesPeriodicRequest.class, GetPQValuesPeriodicRequestDto.class)
        .byDefault()
        .register();

    factory.getConverterFactory().registerConverter(new StringToMeasurementTypeConverter());
    factory
        .classMap(BitmaskMeasurementElementDto.class, BitmaskMeasurementElement.class)
        .byDefault()
        .register();
    factory
        .classMap(FloatMeasurementElementDto.class, FloatMeasurementElement.class)
        .byDefault()
        .register();
    factory
        .classMap(TimestampMeasurementElementDto.class, TimestampMeasurementElement.class)
        .byDefault()
        .register();
    factory.classMap(MeasurementDto.class, Measurement.class).byDefault().register();
    factory.classMap(MeasurementGroupDto.class, MeasurementGroup.class).byDefault().register();

    factory
        .classMap(MeasurementReportHeaderDto.class, MeasurementReportHeader.class)
        .field("reason", "reasonType")
        .byDefault()
        .register();
    factory.classMap(MeasurementReportDto.class, MeasurementReport.class).byDefault().register();
  }
}
