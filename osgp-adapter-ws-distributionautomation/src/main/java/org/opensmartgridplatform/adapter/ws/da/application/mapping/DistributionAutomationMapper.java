/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.da.application.mapping;

import org.opensmartgridplatform.shared.mappers.XMLGregorianCalendarToDateTimeConverter;
import org.opensmartgridplatform.domain.da.valueobjects.GetHealthStatusResponse;
import org.opensmartgridplatform.domain.da.valueobjects.GetDeviceModelResponse;
import org.opensmartgridplatform.domain.da.valueobjects.GetPQValuesResponse;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.joda.time.DateTime;
import org.opensmartgridplatform.domain.da.valueobjects.iec61850.DataSample;
import org.opensmartgridplatform.domain.da.valueobjects.iec61850.LogicalDevice;
import org.opensmartgridplatform.domain.da.valueobjects.iec61850.LogicalNode;
import org.opensmartgridplatform.domain.da.valueobjects.iec61850.PhysicalDevice;
import org.springframework.stereotype.Component;

@Component
public class DistributionAutomationMapper extends ConfigurableMapper {

    @Override
    public void configure(final MapperFactory mapperFactory) {
        mapperFactory.getConverterFactory().registerConverter(new PassThroughConverter(DateTime.class));
        mapperFactory.getConverterFactory().registerConverter(new XMLGregorianCalendarToDateTimeConverter());

        mapperFactory
                .classMap(GetHealthStatusResponse.class,
                        org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.GetHealthStatusResponse.class)
                .byDefault().register();

        mapperFactory
                .classMap(GetDeviceModelResponse.class,
                        org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.GetDeviceModelResponse.class)
                .field("physicalDevice", "physicalServer")
                .byDefault().register();
        mapperFactory
                .classMap(PhysicalDevice.class,
                        org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.PhysicalServerType.class)
                .field("logicalDevices", "logicalDevice")
                .byDefault().register();

        mapperFactory
                .classMap(GetPQValuesResponse.class,
                        org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.GetPQValuesResponse.class)
                .field("logicalDevices", "logicalDevice").byDefault().register();
        mapperFactory
                .classMap(LogicalDevice.class,
                        org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.LogicalDeviceType.class)
                .field("name", "id")
                .field("logicalNodes", "logicalNode")
                .byDefault().register();
        mapperFactory
                .classMap(LogicalNode.class,
                        org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.LogicalNodeType.class)
                .field("data", "dataSample").byDefault().register();
        mapperFactory
                .classMap(DataSample.class,
                        org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.DataSampleType.class)
                .field("sampleType", "type")
                .field("timestamp", "timestamp")
                .field("value", "value")
                .register();
    }
}
