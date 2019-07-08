/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil.GetResultImpl;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ChannelElementValuesDto;

public class DeviceChannelsHelperTest {

    private DlmsHelper dlmsHelper = new DlmsHelper();
    private DeviceChannelsHelper deviceChannelsHelper = new DeviceChannelsHelper(dlmsHelper);

    private static final short PRIMARY_ADDRESS = 1;
    private static final long IDENTIFICATION_NUMBER = 16137489L;
    private static final int MANUFACTURER_IDENTIFICATION = 1057;
    private static final String MANUFACTURER_IDENTIFICATION_AS_TEXT = "AAA";
    private static final short VERSION = 2;
    private static final short DEVICE_TYPE = 3;

    private GetResult primaryAddress = new GetResultImpl(DataObject.newUInteger8Data(PRIMARY_ADDRESS));
    private GetResult identificationNumber = new GetResultImpl(DataObject.newUInteger32Data(IDENTIFICATION_NUMBER));
    private GetResult manufacturerIdentification = new GetResultImpl(DataObject.newUInteger16Data(MANUFACTURER_IDENTIFICATION));
    private GetResult version = new GetResultImpl(DataObject.newUInteger8Data(VERSION));
    private GetResult deviceType = new GetResultImpl(DataObject.newUInteger8Data(DEVICE_TYPE));

    @Test
    public void testMakeChannelElementValues() throws Exception {

        List<GetResult> resultList = new ArrayList<>(Arrays.asList(primaryAddress, identificationNumber,
                manufacturerIdentification, version, deviceType));

        ChannelElementValuesDto values = deviceChannelsHelper.makeChannelElementValues((short)1, resultList);

        assertThat(values.getPrimaryAddress()).isEqualTo(PRIMARY_ADDRESS);
        assertThat(values.getIdentificationNumber()).isEqualTo(String.valueOf(IDENTIFICATION_NUMBER));
        assertThat(values.getManufacturerIdentification()).isEqualTo(MANUFACTURER_IDENTIFICATION_AS_TEXT);
        assertThat(values.getVersion()).isEqualTo(VERSION);
        assertThat(values.getDeviceTypeIdentification()).isEqualTo(DEVICE_TYPE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMakeChannelElementValuesInvalidManufacturerId() throws Exception {

        GetResult manufacturerIdentificationInvalid = new GetResultImpl(DataObject.newUInteger16Data(123));

        List<GetResult> resultList = new ArrayList<>(Arrays.asList(primaryAddress, identificationNumber,
                manufacturerIdentificationInvalid, version, deviceType));

        deviceChannelsHelper.makeChannelElementValues((short) 1, resultList);
    }

    @Test
    public void testMakeChannelElementValuesIdenfiticationNumberNull() throws Exception {

        GetResult identificationNumberNull = new GetResultImpl(null);

        List<GetResult> resultList = new ArrayList<>(Arrays.asList(primaryAddress, identificationNumberNull,
                manufacturerIdentification, version, deviceType));

        ChannelElementValuesDto values = deviceChannelsHelper.makeChannelElementValues((short) 1, resultList);

        assertThat(values.getIdentificationNumber()).isEqualTo(null);
    }
}

