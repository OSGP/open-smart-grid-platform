/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.integrationtests.domain.commands;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.LnClientConnection;
import org.osgp.adapter.protocol.dlms.domain.commands.AbstractMeterReadsScalerUnitCommandExecutor;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.springframework.stereotype.Component;

/**
 *
 * @author dev
 */
@Component
public class TestScalerUnitCommandExecutor
        extends AbstractMeterReadsScalerUnitCommandExecutor<TestChannelQuery, ScalerUnitTestResponse> {

    @Override
    public ScalerUnitTestResponse execute(LnClientConnection conn, TestChannelQuery object)
            throws IOException, TimeoutException, ProtocolAdapterException {
        final List<GetResult> getResultList = conn.get(getScalerUnitAttributeAddress(object));

        GetResult getResult = getResultList.get(0);
        AccessResultCode resultCode = getResult.resultCode();
        LOGGER.debug("AccessResultCode: {}", resultCode.name());
        return new ScalerUnitTestResponse(convert(getResult.resultData()));
    }

}
