package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import com.alliander.osgp.domain.core.entities.SmartMeter;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;

public interface CustomValueToDtoConverter<T, R> {
    R convert(T type, SmartMeter smartmeter) throws FunctionalException;
}
