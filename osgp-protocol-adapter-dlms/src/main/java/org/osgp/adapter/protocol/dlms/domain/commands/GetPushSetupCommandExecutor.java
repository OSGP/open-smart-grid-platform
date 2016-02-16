package org.osgp.adapter.protocol.dlms.domain.commands;

import java.util.List;

import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetPushSetupCommandExecutor {

    protected static final Logger LOGGER = LoggerFactory.getLogger(GetPushSetupCommandExecutor.class);

    protected static final int CLASS_ID = 40;
    protected static final int ATTRIBUTE_ID_PUSH_OBJECT_LIST = 2;
    protected static final int ATTRIBUTE_ID_SEND_DESTINATION_AND_METHOD = 3;
    protected static final int ATTRIBUTE_ID_COMMUNICATION_WINDOW = 4;
    protected static final int ATTRIBUTE_ID_RANDOMISATION_START_INTERVAL = 5;
    protected static final int ATTRIBUTE_ID_NUMBER_OF_RETRIES = 6;
    private static final int ATTRIBUTE_ID_REPETITION_DELAY = 7;

    protected static final AttributeAddress[] ATTRIBUTE_ADDRESSES = new AttributeAddress[6];

    protected static final int INDEX_PUSH_OBJECT_LIST = 0;
    protected static final int INDEX_SEND_DESTINATION_AND_METHOD = 1;
    protected static final int INDEX_COMMUNICATION_WINDOW = 2;
    protected static final int INDEX_RANDOMISATION_START_INTERVAL = 3;
    protected static final int INDEX_NUMBER_OF_RETRIES = 4;
    protected static final int INDEX_REPETITION_DELAY = 5;

    protected GetPushSetupCommandExecutor(final ObisCode obisCode) {
        ATTRIBUTE_ADDRESSES[0] = new AttributeAddress(CLASS_ID, obisCode, ATTRIBUTE_ID_PUSH_OBJECT_LIST);
        ATTRIBUTE_ADDRESSES[1] = new AttributeAddress(CLASS_ID, obisCode, ATTRIBUTE_ID_SEND_DESTINATION_AND_METHOD);
        ATTRIBUTE_ADDRESSES[2] = new AttributeAddress(CLASS_ID, obisCode, ATTRIBUTE_ID_COMMUNICATION_WINDOW);
        ATTRIBUTE_ADDRESSES[3] = new AttributeAddress(CLASS_ID, obisCode, ATTRIBUTE_ID_RANDOMISATION_START_INTERVAL);
        ATTRIBUTE_ADDRESSES[4] = new AttributeAddress(CLASS_ID, obisCode, ATTRIBUTE_ID_NUMBER_OF_RETRIES);
        ATTRIBUTE_ADDRESSES[5] = new AttributeAddress(CLASS_ID, obisCode, ATTRIBUTE_ID_REPETITION_DELAY);
    }

    protected static void checkResultList(final List<GetResult> getResultList) throws ProtocolAdapterException {
        if (getResultList.isEmpty()) {
            throw new ProtocolAdapterException("No GetResult received while retrieving Push Setup table.");
        }

        if (getResultList.size() != ATTRIBUTE_ADDRESSES.length) {
            throw new ProtocolAdapterException("Expected " + ATTRIBUTE_ADDRESSES.length
                    + " GetResults while retrieving Push Setup table, got " + getResultList.size());
        }
    }
}
