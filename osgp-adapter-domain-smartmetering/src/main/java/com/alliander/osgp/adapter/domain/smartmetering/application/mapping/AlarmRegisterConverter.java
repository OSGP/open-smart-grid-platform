/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import java.util.Set;
import java.util.TreeSet;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import org.springframework.stereotype.Component;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmRegister;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmType;
import com.alliander.osgp.dto.valueobjects.smartmetering.AlarmRegisterDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AlarmTypeDto;

@Component
public class AlarmRegisterConverter extends BidirectionalConverter<AlarmRegisterDto, AlarmRegister> {

    @Override
    public AlarmRegister convertTo(final AlarmRegisterDto source, final Type<AlarmRegister> destinationType) {
        if (source == null) {
            return null;
        }

        final Set<AlarmType> alarmTypes = new TreeSet<AlarmType>();

        final Set<AlarmTypeDto> sourceAlarmTypes = source.getAlarmTypes();
        for (final AlarmTypeDto sourceAlarmType : sourceAlarmTypes) {
            alarmTypes.add(AlarmType.valueOf(sourceAlarmType.name()));
        }

        return new AlarmRegister(alarmTypes);
    }

    @Override
    public AlarmRegisterDto convertFrom(final AlarmRegister source, final Type<AlarmRegisterDto> destinationType) {
        if (source == null) {
            return null;
        }

        final Set<AlarmTypeDto> alarmTypes = new TreeSet<AlarmTypeDto>();

        final Set<AlarmType> sourceAlarmTypes = source.getAlarmTypes();

        for (final AlarmType sourceAlarmType : sourceAlarmTypes) {
            alarmTypes.add(AlarmTypeDto.valueOf(sourceAlarmType.name()));
        }

        return new AlarmRegisterDto(alarmTypes);
    }
}