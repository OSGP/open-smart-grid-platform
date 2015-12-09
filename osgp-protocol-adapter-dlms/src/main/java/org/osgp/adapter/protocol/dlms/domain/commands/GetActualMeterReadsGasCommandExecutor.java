package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.ClientConnection;
import org.openmuc.jdlms.DataObject;
import org.openmuc.jdlms.GetRequestParameter;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReadsRequest;
import com.alliander.osgp.dto.valueobjects.smartmetering.MeterReadsGas;

@Component()
public class GetActualMeterReadsGasCommandExecutor implements CommandExecutor<ActualMeterReadsRequest, MeterReadsGas> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetActualMeterReadsGasCommandExecutor.class);

    private static final int CLASS_ID_MBUS = 4;
    private static final byte ATTRIBUTE_ID_VALUE = 2;
    private static final byte ATTRIBUTE_ID_TIME = 5;
    private static final ObisCode OBIS_CODE_MBUS_MASTER_VALUE_1 = new ObisCode("0.1.24.2.1.255");
    private static final ObisCode OBIS_CODE_MBUS_MASTER_VALUE_2 = new ObisCode("0.2.24.2.1.255");
    private static final ObisCode OBIS_CODE_MBUS_MASTER_VALUE_3 = new ObisCode("0.3.24.2.1.255");
    private static final ObisCode OBIS_CODE_MBUS_MASTER_VALUE_4 = new ObisCode("0.4.24.2.1.255");

    @Autowired
    private DlmsHelperService dlmsHelperService;

    @Override
    public MeterReadsGas execute(final ClientConnection conn, final ActualMeterReadsRequest actualMeterReadsRequest)
            throws IOException, ProtocolAdapterException {

        final GetRequestParameter mbusValue = new GetRequestParameter(CLASS_ID_MBUS,
                this.masterValueForChannel(actualMeterReadsRequest.getChannel()), ATTRIBUTE_ID_VALUE);

        LOGGER.debug("Retrieving current MBUS master value for class id: {}, obis code: {}, attribute id: {}",
                mbusValue.classId(), mbusValue.obisCode(), mbusValue.attributeId());

        final GetRequestParameter mbusTime = new GetRequestParameter(CLASS_ID_MBUS,
                this.masterValueForChannel(actualMeterReadsRequest.getChannel()), ATTRIBUTE_ID_TIME);

        LOGGER.debug("Retrieving current MBUS master capture time for class id: {}, obis code: {}, attribute id: {}",
                mbusTime.classId(), mbusTime.obisCode(), mbusTime.attributeId());

        final List<GetResult> getResultList = conn.get(mbusValue, mbusTime);

        checkResultList(getResultList);

        GetResult getResult = getResultList.get(0);
        AccessResultCode resultCode = getResult.resultCode();
        LOGGER.debug("AccessResultCode: {}({})", resultCode.name(), resultCode.value());
        final DataObject value = getResult.resultData();
        LOGGER.debug(this.dlmsHelperService.getDebugInfo(value));

        getResult = getResultList.get(1);
        resultCode = getResult.resultCode();
        LOGGER.debug("AccessResultCode: {}({})", resultCode.name(), resultCode.value());
        final DataObject time = getResult.resultData();
        LOGGER.debug(this.dlmsHelperService.getDebugInfo(time));

        return new MeterReadsGas(new Date(), (Long) value.value(), this.dlmsHelperService.fromDateTimeValue(
                (byte[]) time.value()).toDate());
    }

    private static void checkResultList(final List<GetResult> getResultList) throws ProtocolAdapterException {
        if (getResultList.isEmpty()) {
            throw new ProtocolAdapterException(
                    "No GetResult received while retrieving current MBUS master capture time.");
        }

        if (getResultList.size() > 1) {
            LOGGER.info("Expected 2 GetResult while retrieving current MBUS master capture time, got "
                    + getResultList.size());
        }
    }

    private ObisCode masterValueForChannel(final int channel) throws ProtocolAdapterException {
        switch (channel) {
        case 1:
            return OBIS_CODE_MBUS_MASTER_VALUE_1;
        case 2:
            return OBIS_CODE_MBUS_MASTER_VALUE_2;
        case 3:
            return OBIS_CODE_MBUS_MASTER_VALUE_3;
        case 4:
            return OBIS_CODE_MBUS_MASTER_VALUE_4;
        default:
            throw new ProtocolAdapterException(String.format("channel %s not supported", channel));
        }
    }

}
