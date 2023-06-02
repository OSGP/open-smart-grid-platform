//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters;

import org.opensmartgridplatform.adapter.domain.smartmetering.application.services.DomainHelperService;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PeriodicMeterReadsGasRequestData;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ChannelDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsGasRequestDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PeriodicReadsRequestGasDataConverter
    implements CustomValueToDtoConverter<
        PeriodicMeterReadsGasRequestData, PeriodicMeterReadsGasRequestDto> {

  @Autowired private DomainHelperService domainHelperService;

  @Override
  public PeriodicMeterReadsGasRequestDto convert(
      final PeriodicMeterReadsGasRequestData value, final SmartMeter smartMeter)
      throws FunctionalException {

    final SmartMeter gasMeter =
        this.domainHelperService.findSmartMeter(value.getDeviceIdentification());

    if (gasMeter.getChannel() != null
        && gasMeter.getGatewayDevice() != null
        && gasMeter.getGatewayDevice().getDeviceIdentification() != null
        && gasMeter
            .getGatewayDevice()
            .getDeviceIdentification()
            .equals(smartMeter.getDeviceIdentification())) {

      return new PeriodicMeterReadsGasRequestDto(
          PeriodTypeDto.valueOf(value.getPeriodType().name()),
          value.getBeginDate(),
          value.getEndDate(),
          ChannelDto.fromNumber(gasMeter.getChannel()));
    }
    /*
     * For now, throw a FunctionalException. As soon as we can communicate
     * with some types of gas meters directly, and not through an M-Bus port
     * of an energy meter, this will have to be changed.
     */
    throw new FunctionalException(
        FunctionalExceptionType.VALIDATION_ERROR,
        ComponentType.DOMAIN_SMART_METERING,
        new AssertionError("Meter for gas reads should have a channel configured."));
  }
}
