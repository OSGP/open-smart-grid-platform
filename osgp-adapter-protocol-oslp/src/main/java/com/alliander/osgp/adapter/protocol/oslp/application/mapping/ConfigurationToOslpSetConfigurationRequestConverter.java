package com.alliander.osgp.adapter.protocol.oslp.application.mapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.dto.valueobjects.Configuration;
import com.alliander.osgp.oslp.Oslp;

public class ConfigurationToOslpSetConfigurationRequestConverter extends
        CustomConverter<Configuration, Oslp.SetConfigurationRequest> {
    @Override
    public Oslp.SetConfigurationRequest convert(final Configuration source,
            final Type<? extends Oslp.SetConfigurationRequest> destinationType) {
        final Oslp.SetConfigurationRequest.Builder setConfigurationRequest = Oslp.SetConfigurationRequest.newBuilder();

        if (source.getLightType() != null) {
            setConfigurationRequest.setLightType(this.mapperFacade.map(source.getLightType(), Oslp.LightType.class));
        }

        if (source.getDaliConfiguration() != null) {
            setConfigurationRequest.setDaliConfiguration(this.mapperFacade.map(source.getDaliConfiguration(),
                    Oslp.DaliConfiguration.class));
        }

        if (source.getRelayConfiguration() != null) {
            setConfigurationRequest.setRelayConfiguration(this.mapperFacade.map(source.getRelayConfiguration(),
                    Oslp.RelayConfiguration.class));
        }

        if (source.getShortTermHistoryIntervalMinutes() != null) {
            setConfigurationRequest.setShortTermHistoryIntervalMinutes(this.mapperFacade.map(
                    source.getShortTermHistoryIntervalMinutes(), Integer.class));
        }

        if (source.getLongTermHistoryInterval() != null) {
            setConfigurationRequest.setLongTermHistoryInterval(this.mapperFacade.map(
                    source.getLongTermHistoryInterval(), Integer.class));
        }

        if (source.getLongTermHistoryIntervalType() != null) {
            setConfigurationRequest.setLongTermHistoryIntervalType(this.mapperFacade.map(
                    source.getLongTermHistoryIntervalType(), Oslp.LongTermIntervalType.class));
        }

        if (source.getPreferredLinkType() != null) {
            setConfigurationRequest.setPreferredLinkType(this.mapperFacade.map(source.getPreferredLinkType(),
                    Oslp.LinkType.class));
        }

        if (source.getMeterType() != null) {
            setConfigurationRequest.setMeterType(this.mapperFacade.map(source.getMeterType(), Oslp.MeterType.class));
        }

        return setConfigurationRequest.build();
    }
}
