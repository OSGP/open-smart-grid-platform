package com.alliander.osgp.adapter.domain.core.application.mapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.Configuration;
import com.alliander.osgp.domain.core.valueobjects.DaliConfiguration;
import com.alliander.osgp.domain.core.valueobjects.LightType;
import com.alliander.osgp.domain.core.valueobjects.LinkType;
import com.alliander.osgp.domain.core.valueobjects.LongTermIntervalType;
import com.alliander.osgp.domain.core.valueobjects.MeterType;
import com.alliander.osgp.domain.core.valueobjects.RelayConfiguration;

public class ConfigurationConverter extends
        CustomConverter<com.alliander.osgp.dto.valueobjects.Configuration, Configuration> {

    @Override
    public Configuration convert(final com.alliander.osgp.dto.valueobjects.Configuration source,
            final Type<? extends Configuration> destinationType) {

        final LightType lightType = this.mapperFacade.map(source.getLightType(), LightType.class);

        final DaliConfiguration daliConfiguration = this.mapperFacade.map(source.getDaliConfiguration(),
                DaliConfiguration.class);

        final RelayConfiguration relayConfiguration = this.mapperFacade.map(source.getRelayConfiguration(),
                RelayConfiguration.class);

        final Integer shortTermHistoryIntervalMinutes = this.mapperFacade.map(
                source.getShortTermHistoryIntervalMinutes(), Integer.class);

        final LinkType preferredLinkType = this.mapperFacade.map(source.getPreferredLinkType(), LinkType.class);

        final MeterType meterType = this.mapperFacade.map(source.getMeterType(), MeterType.class);

        final Integer longTermHistoryInterval = this.mapperFacade.map(source.getLongTermHistoryInterval(),
                Integer.class);

        final LongTermIntervalType longTermHistoryIntervalType = this.mapperFacade.map(
                source.getLongTermHistoryIntervalType(), LongTermIntervalType.class);

        return new Configuration(lightType, daliConfiguration, relayConfiguration, shortTermHistoryIntervalMinutes,
                preferredLinkType, meterType, longTermHistoryInterval, longTermHistoryIntervalType);
    }
}
