/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.device.da.rtu;

import com.alliander.osgp.adapter.protocol.iec61850.device.DeviceResponseHandler;
import com.alliander.osgp.adapter.protocol.iec61850.device.rtu.requests.GetDataDeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.infra.messaging.DaRtuDeviceRequestMessageProcessor;
import com.alliander.osgp.dto.valueobjects.microgrids.GetDataRequestDto;

import javax.jms.JMSException;

public interface DaRtuDeviceService {
    /**
     * Reads the {@link GetDataRequestDto} from the device.
     *
     * @returns a {@link <T>} Response DTO via the message processor callback function
     */
    <T> void getData(GetDataDeviceRequest deviceRequest, DeviceResponseHandler deviceResponseHandler, DaRtuDeviceRequestMessageProcessor messageProcessor) throws JMSException;

}
