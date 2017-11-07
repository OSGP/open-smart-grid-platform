/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import org.openmuc.jdlms.MethodResultCode;
import org.osgp.adapter.protocol.dlms.application.models.ProtocolMeterInfo;
import org.osgp.adapter.protocol.dlms.application.services.BundleService;
import org.osgp.adapter.protocol.dlms.application.services.ConfigurationService;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionHolder;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GMeterInfoDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetMbusUserKeyByChannelRequestDataDto;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;

/**
 * Executor that sets the M-Bus User key for an M-Bus device on a given channel
 * on a gateway device.
 * <p>
 * This executor delegates meter communication to the
 * {@link SetEncryptionKeyExchangeOnGMeterCommandExecutor} for which the actual
 * M-Bus device (with its M-Bus master key) needs to be known ahead of
 * execution.
 * <p>
 * This is implemented as a command executor in order to be able to link it to a
 * {@link SetMbusUserKeyByChannelRequestDataDto} from a bundle, as there does
 * not appear to be a simple way to use the
 * {@link SetEncryptionKeyExchangeOnGMeterCommandExecutor} from the
 * {@link BundleService} for both the {@link GMeterInfoDto} and the
 * {@link SetMbusUserKeyByChannelRequestDataDto} (where in the latter case the
 * M-Bus device has to be retrieved by the channel and the gateway device, while
 * in the former case it can be looked up by device identification).
 */
@Component()
public class SetMbusUserKeyByChannelCommandExecutor
        extends AbstractCommandExecutor<ProtocolMeterInfo, MethodResultCode> {

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private SetEncryptionKeyExchangeOnGMeterCommandExecutor setEncryptionKeyExchangeOnGMeterCommandExecutor;

    public SetMbusUserKeyByChannelCommandExecutor() {
        super(SetMbusUserKeyByChannelRequestDataDto.class);
    }

    @Override
    public ActionResponseDto executeBundleAction(final DlmsConnectionHolder conn, final DlmsDevice device,
            final ActionRequestDto actionRequestDto) throws ProtocolAdapterException, FunctionalException {

        this.checkActionRequestType(actionRequestDto);
        final SetMbusUserKeyByChannelRequestDataDto setMbusUserKeyByChannelRequestData = (SetMbusUserKeyByChannelRequestDataDto) actionRequestDto;
        final ProtocolMeterInfo mbusKeyExchangeData = this.configurationService.getMbusKeyExchangeData(conn, device,
                setMbusUserKeyByChannelRequestData);
        final MethodResultCode executionResult = this.execute(conn, device, mbusKeyExchangeData);
        final ActionResponseDto bundleResponse = this.asBundleResponse(executionResult);
        return bundleResponse;
    }

    @Override
    public ActionResponseDto asBundleResponse(final MethodResultCode executionResult) throws ProtocolAdapterException {
        this.checkMethodResultCode(executionResult);
        return new ActionResponseDto("Setting M-Bus User key by channel was successful");
    }

    @Override
    public MethodResultCode execute(final DlmsConnectionHolder conn, final DlmsDevice device,
            final ProtocolMeterInfo protocolMeterInfo) throws ProtocolAdapterException, FunctionalException {
        return this.setEncryptionKeyExchangeOnGMeterCommandExecutor.execute(conn, device, protocolMeterInfo);
    }
}
