package com.alliander.osgp.adapter.ws.microgrids.application.mapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.ConfigurableMapper;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import com.alliander.osgp.domain.microgrids.valueobjects.DataRequest;
import com.alliander.osgp.domain.microgrids.valueobjects.DataResponse;
import com.alliander.osgp.domain.microgrids.valueobjects.Measurement;
import com.alliander.osgp.domain.microgrids.valueobjects.MeasurementResultSystemIdentifier;
import com.alliander.osgp.domain.microgrids.valueobjects.SetPointSystemIdentifier;
import com.alliander.osgp.domain.microgrids.valueobjects.SetPointsRequest;
import com.alliander.osgp.domain.microgrids.valueobjects.SystemFilter;
import com.alliander.osgp.shared.mappers.XMLGregorianCalendarToDateTimeConverter;

@Component
public class MicrogridsMapper extends ConfigurableMapper {

    private static final String SYSTEM = "system";
    private static final String SYSTEM_TYPE = "systemType";
    private static final String TYPE = "type";

    @Override
    public void configure(final MapperFactory mapperFactory) {
        mapperFactory.getConverterFactory().registerConverter(new PassThroughConverter(DateTime.class));

        mapperFactory
        .classMap(com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.SystemFilter.class,
                SystemFilter.class).field(TYPE, SYSTEM_TYPE).field("measurementFilter", "measurementFilters")
                .byDefault().register();

        mapperFactory
                .classMap(com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.GetDataRequest.class,
                        DataRequest.class).field(SYSTEM, "systemFilters").byDefault().register();

        mapperFactory
        .classMap(
                        MeasurementResultSystemIdentifier.class,
                com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.MeasurementResultSystemIdentifier.class)
                .field(SYSTEM_TYPE, TYPE).field("measurements", "measurement").byDefault().register();

        mapperFactory
        .classMap(Measurement.class,
                com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.Measurement.class).byDefault()
                .register();

        mapperFactory
        .classMap(DataResponse.class,
                com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.GetDataResponse.class)
                .field("measurementResultSystemIdentifiers", SYSTEM).byDefault().register();

        mapperFactory
        .classMap(com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.SetSetPointsRequest.class,
                SetPointsRequest.class).field(SYSTEM, "setPointSystemIdentifiers").byDefault().register();

        mapperFactory
                .classMap(
                        com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.SystemSetPointIdentifier.class,
                        SetPointSystemIdentifier.class).field(TYPE, SYSTEM_TYPE).byDefault().register();

        mapperFactory.getConverterFactory().registerConverter(new XMLGregorianCalendarToDateTimeConverter());
    }
}
