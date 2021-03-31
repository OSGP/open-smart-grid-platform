/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import java.util.GregorianCalendar;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.BitErrorRateDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CircuitSwitchedStatusDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetModemInfoRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetModemInfoResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ModemRegistrationStatusDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PacketSwitchedStatusDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SignalQualityDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class GetModemInfoCommandExecutor extends
        AbstractCommandExecutor<GetModemInfoRequestDto, GetModemInfoResponseDto> {

    @Autowired
    public GetModemInfoCommandExecutor() {
        super(GetModemInfoRequestDto.class);
    }

    @Override
    public ActionResponseDto asBundleResponse(final GetModemInfoResponseDto executionResult)
            throws ProtocolAdapterException {
        return executionResult;
    }

    @Override
    public GetModemInfoResponseDto execute(final DlmsConnectionManager conn, final DlmsDevice device,
            final GetModemInfoRequestDto getModemInfoRequestDto) throws ProtocolAdapterException {

        // TODO: Implement this
        return new GetModemInfoResponseDto("operator",
                ModemRegistrationStatusDto.REGISTERED_ROAMING,
                CircuitSwitchedStatusDto.ACTIVE,
                PacketSwitchedStatusDto.CDMA,
                "cellId".getBytes(),
                "locationId".getBytes(),
                SignalQualityDto.MINUS_61_DBM,
                BitErrorRateDto.RXQUAL_2,
                31L,
                00L,
                1L,
                3L,
                "adjacantCellId".getBytes(),
                SignalQualityDto.MINUS_83_DBM,
                new GregorianCalendar(2021, 2, 31, 4, 5, 6).getTime());
    }

}
