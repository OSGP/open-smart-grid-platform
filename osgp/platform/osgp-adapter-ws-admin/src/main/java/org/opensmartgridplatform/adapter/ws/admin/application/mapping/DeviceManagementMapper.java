// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.admin.application.mapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.opensmartgridplatform.shared.mappers.XMLGregorianCalendarToDateTimeConverter;
import org.opensmartgridplatform.shared.mappers.XMLGregorianCalendarToZonedDateTimeConverter;
import org.springframework.stereotype.Component;

@Component(value = "adminDeviceManagementMapper")
public class DeviceManagementMapper extends ConfigurableMapper {

  @Override
  public void configure(final MapperFactory mapperFactory) {
    mapperFactory.registerClassMap(
        mapperFactory
            .classMap(
                org.opensmartgridplatform.logging.domain.entities.DeviceLogItem.class,
                org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.MessageLog.class)
            .field("modificationTime", "timestamp")
            .byDefault()
            .toClassMap());

    mapperFactory.registerClassMap(
        mapperFactory
            .classMap(
                org.opensmartgridplatform.domain.core.entities.DeviceAuthorization.class,
                org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement
                    .DeviceAuthorisation.class)
            .field("device.deviceIdentification", "deviceIdentification")
            .field("organisation.organisationIdentification", "organisationIdentification")
            .byDefault()
            .toClassMap());

    mapperFactory.registerClassMap(
        mapperFactory
            .classMap(
                org.opensmartgridplatform.domain.core.entities.ProtocolInfo.class,
                org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.ProtocolInfo
                    .class)
            .exclude("outgoingRequestsPropertyPrefix")
            .exclude("incomingResponsesPropertyPrefix")
            .exclude("incomingRequestsPropertyPrefix")
            .exclude("outgoingResponsesPropertyPrefix")
            .byDefault()
            .toClassMap());

    mapperFactory.registerClassMap(
        mapperFactory
            .classMap(
                org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.MessageLogFilter
                    .class,
                org.opensmartgridplatform.adapter.ws.admin.application.valueobjects
                    .WsMessageLogFilter.class)
            .field("page", "pageRequested")
            .byDefault()
            .toClassMap());

    mapperFactory
        .getConverterFactory()
        .registerConverter(new XMLGregorianCalendarToDateTimeConverter());
    mapperFactory
        .getConverterFactory()
        .registerConverter(new XMLGregorianCalendarToZonedDateTimeConverter());
  }
}
