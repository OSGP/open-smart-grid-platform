//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters;

import org.opensmartgridplatform.adapter.domain.smartmetering.application.services.DomainHelperService;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetFirmwareVersionGasRequestData;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ChannelDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetFirmwareVersionGasRequestDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetFirmwareVersionGasRequestDataConverter
    implements CustomValueToDtoConverter<
        GetFirmwareVersionGasRequestData, GetFirmwareVersionGasRequestDto> {

  @Autowired private DomainHelperService domainHelperService;

  /**
   * This overridden convert is used from the bundle flow where the deviceIdentification from the
   * bundle is used to fetch and supply the SmartMeter, which is an E meter. Since the channel is
   * needed to enrich the DTO, the G meter is fetched with the deviceIdentification supplied as
   * field of the request.
   *
   * @param gasRequestData request containing the deviceIdentification of the G meter
   * @param eMeter E meter supplied by the bundle process, not used for G meter requests
   * @return DTO containing the channel of the G meter
   * @throws FunctionalException
   */
  @Override
  public GetFirmwareVersionGasRequestDto convert(
      final GetFirmwareVersionGasRequestData gasRequestData, final SmartMeter eMeter)
      throws FunctionalException {

    final SmartMeter gasMeter =
        this.domainHelperService.findSmartMeter(gasRequestData.getDeviceIdentification());

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
          new AssertionError("Retrieving firmware version for gas meter. No channel configured."));
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
          new AssertionError(
              "Retrieving firmware version for gas meter. No gateway device found."));
    }

    return new GetFirmwareVersionGasRequestDto(
        ChannelDto.fromNumber(gasMeter.getChannel()), gasRequestData.getDeviceIdentification());
  }
}
