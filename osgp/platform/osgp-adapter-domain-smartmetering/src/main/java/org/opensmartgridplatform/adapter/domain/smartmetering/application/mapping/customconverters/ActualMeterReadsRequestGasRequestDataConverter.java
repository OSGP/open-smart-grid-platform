// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters;

import org.opensmartgridplatform.adapter.domain.smartmetering.application.services.DomainHelperService;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActualMeterReadsGasRequestData;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualMeterReadsDataGasDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ChannelDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ActualMeterReadsRequestGasRequestDataConverter
    implements CustomValueToDtoConverter<
        ActualMeterReadsGasRequestData, ActualMeterReadsDataGasDto> {

  @Autowired private DomainHelperService domainHelperService;

  @Override
  public ActualMeterReadsDataGasDto convert(
      final ActualMeterReadsGasRequestData value, final SmartMeter smartMeter)
      throws FunctionalException {

    final SmartMeter gasMeter =
        this.domainHelperService.findSmartMeter(value.getDeviceIdentification());

    if (gasMeter.getChannel() == null) {
      /*
       * For now, throw a FunctionalException. As soon as we can
       * communicate with some types of gas meters directly, and not
       * through an M-Bus port of an energy meter, this will have to be
       * changed.
       */
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.DOMAIN_SMART_METERING,
          new AssertionError("Meter for gas reads should have a channel configured."));
    }
    final Device gatewayDevice = gasMeter.getGatewayDevice();
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

    return new ActualMeterReadsDataGasDto(ChannelDto.fromNumber(gasMeter.getChannel()));
  }
}
