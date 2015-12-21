package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;
import java.util.List;

import org.openmuc.jdlms.ClientConnection;
import org.openmuc.jdlms.MethodRequestParameter;
import org.openmuc.jdlms.MethodResult;
import org.openmuc.jdlms.MethodResultCode;
import org.openmuc.jdlms.ObisCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component()
public class SetActivityCalendarCommandActivationExecutor implements CommandExecutor<Void, MethodResultCode> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetActivityCalendarCommandActivationExecutor.class);

    private static final int CLASS_ID = 20;
    private static final ObisCode OBIS_CODE = new ObisCode("0.0.13.0.0.255");

    @Override
    public MethodResultCode execute(final ClientConnection conn, final Void v) throws IOException {

        LOGGER.info("ACTIVATING");
        final MethodRequestParameter method = new MethodRequestParameter(CLASS_ID, OBIS_CODE, 1);
        final List<MethodResult> methodResultCode = conn.action(method);
        return methodResultCode.get(0).resultCode();
    }
}
