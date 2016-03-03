package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;
import java.util.List;

import org.openmuc.jdlms.ClientConnection;
import org.openmuc.jdlms.MethodParameter;
import org.openmuc.jdlms.MethodResult;
import org.openmuc.jdlms.MethodResultCode;
import org.openmuc.jdlms.ObisCode;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ConnectionException;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component()
public class SetActivityCalendarCommandActivationExecutor implements CommandExecutor<Void, MethodResultCode> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetActivityCalendarCommandActivationExecutor.class);

    private static final int CLASS_ID = 20;
    private static final ObisCode OBIS_CODE = new ObisCode("0.0.13.0.0.255");
    private static final int METHOD_ID_ACTIVATE_PASSIVE_CALENDAR = 1;

    @Override
    public MethodResultCode execute(final ClientConnection conn, final DlmsDevice device, final Void v)
            throws ProtocolAdapterException {

        LOGGER.info("ACTIVATING PASSIVE CALENDAR");
        final MethodParameter method = new MethodParameter(CLASS_ID, OBIS_CODE, METHOD_ID_ACTIVATE_PASSIVE_CALENDAR);
        List<MethodResult> methodResultCode;
        try {
            methodResultCode = conn.action(method);
        } catch (final IOException e) {
            throw new ConnectionException(e);
        }
        if (methodResultCode == null || methodResultCode.isEmpty() || methodResultCode.get(0) == null) {
            throw new ProtocolAdapterException(
                    "action method for ClientConnection should return a list with one MethodResult");
        }
        return methodResultCode.get(0).resultCode();
    }
}
