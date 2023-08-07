// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.da.application.mapping;

import java.time.ZonedDateTime;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.BitmaskMeasurementElement;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.FloatMeasurementElement;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.Measurement;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.MeasurementGroup;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.MeasurementReport;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.TimestampMeasurementElement;
import org.opensmartgridplatform.domain.da.valueobjects.GetDeviceModelResponse;
import org.opensmartgridplatform.domain.da.valueobjects.GetHealthStatusResponse;
import org.opensmartgridplatform.domain.da.valueobjects.GetPQValuesResponse;
import org.opensmartgridplatform.domain.da.valueobjects.iec61850.DataSample;
import org.opensmartgridplatform.domain.da.valueobjects.iec61850.LogicalDevice;
import org.opensmartgridplatform.domain.da.valueobjects.iec61850.LogicalNode;
import org.opensmartgridplatform.domain.da.valueobjects.iec61850.PhysicalDevice;
import org.opensmartgridplatform.shared.mappers.XMLGregorianCalendarToDateTimeConverter;
import org.opensmartgridplatform.shared.mappers.XMLGregorianCalendarToInstantConverter;
import org.springframework.stereotype.Component;

@Component
public class DistributionAutomationMapper extends ConfigurableMapper {

  @Override
  public void configure(final MapperFactory mapperFactory) {
    mapperFactory
        .getConverterFactory()
        .registerConverter(new PassThroughConverter(ZonedDateTime.class));
    mapperFactory
        .getConverterFactory()
        .registerConverter(new XMLGregorianCalendarToDateTimeConverter());
    mapperFactory
        .getConverterFactory()
        .registerConverter(new XMLGregorianCalendarToInstantConverter());

    mapperFactory
        .classMap(
            GetHealthStatusResponse.class,
            org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic
                .GetHealthStatusResponse.class)
        .byDefault()
        .register();

    mapperFactory
        .classMap(
            GetDeviceModelResponse.class,
            org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic
                .GetDeviceModelResponse.class)
        .field("physicalDevice", "physicalServer")
        .byDefault()
        .register();
    mapperFactory
        .classMap(
            PhysicalDevice.class,
            org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic
                .PhysicalServerType.class)
        .field("logicalDevices", "logicalDevice")
        .byDefault()
        .register();

    mapperFactory
        .classMap(
            GetPQValuesResponse.class,
            org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic
                .GetPQValuesResponse.class)
        .field("logicalDevices", "logicalDevice")
        .byDefault()
        .register();
    mapperFactory
        .classMap(
            LogicalDevice.class,
            org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic
                .LogicalDeviceType.class)
        .field("name", "id")
        .field("logicalNodes", "logicalNode")
        .byDefault()
        .register();
    mapperFactory
        .classMap(
            LogicalNode.class,
            org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic
                .LogicalNodeType.class)
        .field("data", "dataSample")
        .byDefault()
        .register();
    mapperFactory
        .classMap(
            DataSample.class,
            org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic
                .DataSampleType.class)
        .field("sampleType", "type")
        .field("timestamp", "timestamp")
        .field("value", "value")
        .register();
    mapperFactory
        .classMap(
            BitmaskMeasurementElement.class,
            org.opensmartgridplatform.domain.da.measurements.elements.BitmaskMeasurementElement
                .class)
        .byDefault()
        .register();
    mapperFactory
        .classMap(
            FloatMeasurementElement.class,
            org.opensmartgridplatform.domain.da.measurements.elements.FloatMeasurementElement.class)
        .byDefault()
        .register();
    mapperFactory
        .classMap(
            TimestampMeasurementElement.class,
            org.opensmartgridplatform.domain.da.measurements.elements.TimestampMeasurementElement
                .class)
        .byDefault()
        .register();
    mapperFactory
        .classMap(
            Measurement.class, org.opensmartgridplatform.domain.da.measurements.Measurement.class)
        .field("measurementElements.measurementElementList", "measurementElements")
        .byDefault()
        .register();
    mapperFactory
        .classMap(
            MeasurementGroup.class,
            org.opensmartgridplatform.domain.da.measurements.MeasurementGroup.class)
        .field("measurements.measurementList", "measurements")
        .byDefault()
        .register();
    mapperFactory
        .classMap(
            MeasurementReport.class,
            org.opensmartgridplatform.domain.da.measurements.MeasurementReport.class)
        .field("measurementGroups.measurementGroupList", "measurementGroups")
        .byDefault()
        .register();
  }
}
