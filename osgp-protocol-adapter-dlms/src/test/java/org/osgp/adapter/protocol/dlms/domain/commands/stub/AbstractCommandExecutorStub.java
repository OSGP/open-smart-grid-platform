/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands.stub;

import org.openmuc.jdlms.DlmsConnection;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;

public abstract class AbstractCommandExecutorStub {

    private ActionResponseDto actionResponse;
    private ProtocolAdapterException protocolAdapterException;
    private RuntimeException runtimeException;

    protected ActionResponseDto doExecute(final DlmsConnection conn, final DlmsDevice device,
            final ActionRequestDto object) throws ProtocolAdapterException {

        if (this.runtimeException != null) {
            throw this.runtimeException;
        } else if (this.protocolAdapterException != null) {
            throw this.protocolAdapterException;
        } else if (this.actionResponse == null) {
            return new ActionResponseDto();
        } else {
            return this.actionResponse;
        }
    }

    public ActionResponseDto getActionResponse() {
        return this.actionResponse;
    }

    public void setActionResponse(final ActionResponseDto actionResponse) {
        this.actionResponse = actionResponse;
    }

    public void failWith(final ProtocolAdapterException protocolAdapterException) {
        this.protocolAdapterException = protocolAdapterException;
    }

    public void failWithRuntimeException(final RuntimeException runtimeException) {
        this.runtimeException = runtimeException;
    }

    public ProtocolAdapterException getProtocolAdapterException() {
        return this.protocolAdapterException;
    }

    public void setProtocolAdapterException(final ProtocolAdapterException protocolAdapterException) {
        this.protocolAdapterException = protocolAdapterException;
    }

}
