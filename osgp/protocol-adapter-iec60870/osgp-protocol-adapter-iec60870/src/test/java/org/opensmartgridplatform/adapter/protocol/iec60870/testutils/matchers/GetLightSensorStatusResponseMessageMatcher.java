/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.testutils.matchers;

import org.mockito.ArgumentMatcher;
import org.opensmartgridplatform.dto.valueobjects.LightSensorStatusDto;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;

public class GetLightSensorStatusResponseMessageMatcher implements ArgumentMatcher<ProtocolResponseMessage> {

    private final ProtocolResponseMessage responseMessage;
    private final LightSensorStatusDto lightSensorStatusDto;

    public GetLightSensorStatusResponseMessageMatcher(final ProtocolResponseMessage responseMessage) {
        this.responseMessage = responseMessage;
        this.lightSensorStatusDto = (LightSensorStatusDto) responseMessage.getDataObject();
    }

    @Override
    public boolean matches(final ProtocolResponseMessage argument) {
        if (!argument.getDeviceIdentification().equals(this.responseMessage.getDeviceIdentification())) {
            return false;
        }
        if (!argument.getMessageType().equals(MessageType.GET_LIGHT_SENSOR_STATUS.name())) {
            return false;
        }
        if (!(argument.getDataObject() instanceof LightSensorStatusDto)) {
            return false;
        }
        final LightSensorStatusDto lightSensorStatusDto = (LightSensorStatusDto) argument.getDataObject();
        if (!lightSensorStatusDto.equals(this.lightSensorStatusDto)) {
            return false;
        }
        return argument.getResult() == ResponseMessageResultType.OK;
    }

}
