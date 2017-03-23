/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.smartsocietyservices.osgp.adapter.ws.da.application.mapping;

import com.alliander.osgp.shared.mappers.XMLGregorianCalendarToDateTimeConverter;
import com.smartsocietyservices.osgp.domain.da.valueobjects.GetDataRequest;
import com.smartsocietyservices.osgp.domain.da.valueobjects.GetDataResponse;
import com.smartsocietyservices.osgp.domain.da.valueobjects.GetDataSystemIdentifier;
import com.smartsocietyservices.osgp.domain.da.valueobjects.Measurement;
import com.smartsocietyservices.osgp.domain.da.valueobjects.Profile;
import com.smartsocietyservices.osgp.domain.da.valueobjects.SetDataRequest;
import com.smartsocietyservices.osgp.domain.da.valueobjects.SetDataSystemIdentifier;
import com.smartsocietyservices.osgp.domain.da.valueobjects.SystemFilter;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

@Component
public class DistributionAutomationMapper extends ConfigurableMapper {

    private static final String SYSTEM = "system";
    private static final String SYSTEM_TYPE = "systemType";
    private static final String TYPE = "type";

    @Override
    public void configure( final MapperFactory mapperFactory ) {
        mapperFactory.getConverterFactory().registerConverter( new PassThroughConverter( DateTime.class ) );
        mapperFactory.getConverterFactory().registerConverter( new XMLGregorianCalendarToDateTimeConverter() );

        mapperFactory.classMap( com.smartsocietyservices.osgp.adapter.ws.da.application.config.PersistenceConfigWs.class, SystemFilter.class )
                .field( TYPE, SYSTEM_TYPE ).field( "measurementFilter", "measurementFilters" ).field( "profileFilter", "profileFilters" ).byDefault()
                .register();

        mapperFactory.classMap( com.smartsocietyservices.osgp.adapter.ws.schema.distributionautomation.adhocmanagement.GetDataRequest.class,
                GetDataRequest.class ).field( SYSTEM, "systemFilters" ).byDefault().register();

        mapperFactory.classMap( GetDataSystemIdentifier.class,
                com.smartsocietyservices.osgp.adapter.ws.schema.distributionautomation.adhocmanagement.GetDataSystemIdentifier.class )
                .field( SYSTEM_TYPE, TYPE ).field( "measurements", "measurement" ).field( "profiles", "profile" ).byDefault().register();

        mapperFactory.classMap( Profile.class, com.smartsocietyservices.osgp.adapter.ws.schema.distributionautomation.adhocmanagement.Profile.class )
                .field( "profileEntries", "profileEntry" ).byDefault().register();
        mapperFactory.classMap( Measurement.class,
                com.smartsocietyservices.osgp.adapter.ws.schema.distributionautomation.adhocmanagement.Measurement.class ).byDefault().register();

        mapperFactory.classMap( GetDataResponse.class,
                com.smartsocietyservices.osgp.adapter.ws.schema.distributionautomation.adhocmanagement.GetDataResponse.class )
                .field( "getDataSystemIdentifiers", SYSTEM ).byDefault().register();

        mapperFactory.classMap( com.smartsocietyservices.osgp.adapter.ws.schema.distributionautomation.adhocmanagement.SetDataRequest.class,
                SetDataRequest.class ).field( SYSTEM, "setDataSystemIdentifiers" ).byDefault().register();

        mapperFactory.classMap( com.smartsocietyservices.osgp.adapter.ws.schema.distributionautomation.adhocmanagement.SetDataSystemIdentifier.class,
                SetDataSystemIdentifier.class ).field( TYPE, SYSTEM_TYPE ).field( "setPoint", "setPoints" ).field( "profile", "profiles" ).byDefault()
                .register();
        mapperFactory.classMap( com.smartsocietyservices.osgp.adapter.ws.schema.distributionautomation.adhocmanagement.Profile.class, Profile.class )
                .field( "profileEntry", "profileEntries" ).byDefault().register();
    }
}
