package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openmuc.jdlms.ClientConnection;
import org.openmuc.jdlms.GetRequestParameter;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.RequestParameterFactory;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.AlarmRegister;
import com.alliander.osgp.dto.valueobjects.smartmetering.AlarmType;
import com.alliander.osgp.dto.valueobjects.smartmetering.ReadAlarmRegisterRequest;

@Component
public class ReadAlarmRegisterCommandExecutor implements CommandExecutor<ReadAlarmRegisterRequest, AlarmRegister> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReadAlarmRegisterCommandExecutor.class);

    private static final int CLASS_ID = 1;
    private static final ObisCode OBIS_CODE = new ObisCode("0.0.97.98.00.255");
    private static final int ATTRIBUTE_ID = 2;

    @Override
    public AlarmRegister execute(final ClientConnection conn, final ReadAlarmRegisterRequest object)
            throws IOException, ProtocolAdapterException {

        this.retrieveAlarmRegister(conn);

        // Mock a return value for read alarm register
        final Set<AlarmType> types = new HashSet<>();
        types.add(AlarmType.CLOCK_INVALID);

        final AlarmRegister AlarmRegister = new AlarmRegister(types);

        return AlarmRegister;
    }

    private void retrieveAlarmRegister(final ClientConnection conn) throws IOException, ProtocolAdapterException {
        final RequestParameterFactory factory = new RequestParameterFactory(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID);

        final GetRequestParameter getRequestParameter = factory.createGetRequestParameter();

        final List<GetResult> getResultList = conn.get(getRequestParameter);

        if (getResultList.isEmpty()) {
            throw new ProtocolAdapterException("No GetResult received while retrieving alarm register.");
        }

        if (getResultList.size() > 1) {
            throw new ProtocolAdapterException("Expected 1 GetResult while retrieving alarm register, got "
                    + getResultList.size());
        }

        for (final GetResult result : getResultList) {
            if (result.resultData().isNumber()) {
                LOGGER.info("Result: {} --> {}", result.resultCode().value(),
                        Long.toBinaryString((Long) result.resultData().value()));
            } else {
                LOGGER.info("Result: {} --> {}", result.resultCode().value(), result.resultData());
            }
        }
    }
}
