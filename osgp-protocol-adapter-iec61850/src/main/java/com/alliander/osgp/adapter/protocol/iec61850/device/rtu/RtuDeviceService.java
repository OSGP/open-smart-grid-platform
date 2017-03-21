/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.device.rtu;

import javax.jms.JMSException;

import com.alliander.osgp.adapter.protocol.iec61850.device.DeviceMessageStatus;
import com.alliander.osgp.adapter.protocol.iec61850.device.DeviceResponseHandler;
import com.alliander.osgp.adapter.protocol.iec61850.device.rtu.requests.GetDataDeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.rtu.requests.SetDataDeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.ssld.responses.GetDataDeviceResponse;
import com.alliander.osgp.dto.valueobjects.microgrids.GetDataRequestDto;
import com.alliander.osgp.dto.valueobjects.microgrids.SetDataRequestDto;

public interface RtuDeviceService {
    /**
     * Reads the {@link GetDataRequestDto} from the device.
     *
     * @returns a {@link GetDataDeviceResponse} via the deviceResponseHandler's
     *          callback.
     */
    void getData(GetDataDeviceRequest deviceRequest, DeviceResponseHandler deviceResponseHandler) throws JMSException;

    /**
     * Writes the {@link SetDataRequestDto} to the device.
     * 
     * @throws JMSException
     *
     * @returns a {@link DeviceMessageStatus} via the deviceResponseHandler's
     *          callback.
     */
    void setData(SetDataDeviceRequest deviceRequest, DeviceResponseHandler deviceResponseHandler) throws JMSException;
}
