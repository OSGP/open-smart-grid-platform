/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.microgrids.application.mapping;

import org.joda.time.DateTime;
import org.opensmartgridplatform.domain.microgrids.valueobjects.GetDataRequest;
import org.opensmartgridplatform.domain.microgrids.valueobjects.GetDataResponse;
import org.opensmartgridplatform.domain.microgrids.valueobjects.GetDataSystemIdentifier;
import org.opensmartgridplatform.domain.microgrids.valueobjects.Measurement;
import org.opensmartgridplatform.domain.microgrids.valueobjects.Profile;
import org.opensmartgridplatform.domain.microgrids.valueobjects.SetDataRequest;
import org.opensmartgridplatform.domain.microgrids.valueobjects.SetDataSystemIdentifier;
import org.opensmartgridplatform.domain.microgrids.valueobjects.SystemFilter;
import org.opensmartgridplatform.shared.mappers.XMLGregorianCalendarToDateTimeConverter;
import org.opensmartgridplatform.shared.mappers.XMLGregorianCalendarToInstantConverter;
import org.springframework.stereotype.Component;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.ConfigurableMapper;

@Component
public class MicrogridsMapper extends ConfigurableMapper {

    private static final String SYSTEM = "system";
    private static final String SYSTEM_TYPE = "systemType";
    private static final String TYPE = "type";

    @Override
    public void configure(final MapperFactory mapperFactory) {
        mapperFactory.getConverterFactory().registerConverter(new PassThroughConverter(DateTime.class));
        mapperFactory.getConverterFactory().registerConverter(new XMLGregorianCalendarToDateTimeConverter());
        mapperFactory.getConverterFactory().registerConverter(new XMLGregorianCalendarToInstantConverter());

        mapperFactory
                .classMap(org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.SystemFilter.class,
                        SystemFilter.class)
                .field(TYPE, SYSTEM_TYPE)
                .field("measurementFilter", "measurementFilters")
                .field("profileFilter", "profileFilters")
                .byDefault()
                .register();

        mapperFactory
                .classMap(org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.GetDataRequest.class,
                        GetDataRequest.class)
                .field(SYSTEM, "systemFilters")
                .byDefault()
                .register();

        mapperFactory.classMap(GetDataSystemIdentifier.class,
                org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.GetDataSystemIdentifier.class)
                .field(SYSTEM_TYPE, TYPE)
                .field("measurements", "measurement")
                .field("profiles", "profile")
                .byDefault()
                .register();

        mapperFactory
                .classMap(Profile.class,
                        org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.Profile.class)
                .field("profileEntries", "profileEntry")
                .byDefault()
                .register();
        mapperFactory
                .classMap(Measurement.class,
                        org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.Measurement.class)
                .byDefault()
                .register();

        mapperFactory
                .classMap(GetDataResponse.class,
                        org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.GetDataResponse.class)
                .field("getDataSystemIdentifiers", SYSTEM)
                .byDefault()
                .register();

        mapperFactory
                .classMap(org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.SetDataRequest.class,
                        SetDataRequest.class)
                .field(SYSTEM, "setDataSystemIdentifiers")
                .byDefault()
                .register();

        mapperFactory.classMap(
                org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.SetDataSystemIdentifier.class,
                SetDataSystemIdentifier.class)
                .field(TYPE, SYSTEM_TYPE)
                .field("setPoint", "setPoints")
                .field("profile", "profiles")
                .byDefault()
                .register();
        mapperFactory
                .classMap(org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.Profile.class,
                        Profile.class)
                .field("profileEntry", "profileEntries")
                .byDefault()
                .register();
    }
}
