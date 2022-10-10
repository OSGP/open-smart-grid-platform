/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters;

import org.opensmartgridplatform.adapter.domain.smartmetering.application.services.DomainHelperService;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetKeyOnGMeterRequestData;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SecretTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetKeyOnGMeterRequestDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetKeyOnGMeterDataConverter
    implements CustomValueToDtoConverter<SetKeyOnGMeterRequestData, SetKeyOnGMeterRequestDto> {

  @Autowired private DomainHelperService domainHelperService;

  @Override
  public SetKeyOnGMeterRequestDto convert(
      final SetKeyOnGMeterRequestData value, final SmartMeter smartMeter)
      throws FunctionalException {

    final SmartMeter gasDevice =
        this.domainHelperService.findSmartMeter(value.getMbusDeviceIdentification());

    final Device gatewayDevice = gasDevice.getGatewayDevice();
    if (gatewayDevice == null) {
      /*
       * For now throw a FunctionalException, based on the same reasoning
       * as with the channel a couple of lines up. As soon as we have
       * scenario's with direct communication with gas meters this will
       * have to be changed.
       */
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.DOMAIN_SMART_METERING,
          new AssertionError("Meter for gas reads should have an energy meter as gateway device."));
    }

    return new SetKeyOnGMeterRequestDto(
        gasDevice.getDeviceIdentification(),
        gasDevice.getChannel(),
        SecretTypeDto.values()[value.getSecretType().ordinal()],
        value.getCloseOpticalPort() != null ? value.getCloseOpticalPort() : false);
  }
}
