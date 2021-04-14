package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters;

import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

@FunctionalInterface
public interface CustomValueToDtoConverter<T, R> {
  R convert(T type, SmartMeter smartmeter) throws FunctionalException;
}
