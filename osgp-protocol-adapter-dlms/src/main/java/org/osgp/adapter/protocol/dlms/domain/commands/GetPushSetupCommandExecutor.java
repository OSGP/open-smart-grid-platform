package org.osgp.adapter.protocol.dlms.domain.commands;

import java.util.List;

import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;

public class GetPushSetupCommandExecutor {

    protected static final int CLASS_ID = 40;
    protected static final int ATTRIBUTE_ID_PUSH_OBJECT_LIST = 2;
    protected static final int ATTRIBUTE_ID_SEND_DESTINATION_AND_METHOD = 3;
    protected static final int ATTRIBUTE_ID_COMMUNICATION_WINDOW = 4;
    protected static final int ATTRIBUTE_ID_RANDOMISATION_START_INTERVAL = 5;
    protected static final int ATTRIBUTE_ID_NUMBER_OF_RETRIES = 6;
    protected static final int ATTRIBUTE_ID_REPETITION_DELAY = 7;

    protected static final int INDEX_PUSH_OBJECT_LIST = 0;
    protected static final int INDEX_SEND_DESTINATION_AND_METHOD = 1;
    protected static final int INDEX_COMMUNICATION_WINDOW = 2;
    protected static final int INDEX_RANDOMISATION_START_INTERVAL = 3;
    protected static final int INDEX_NUMBER_OF_RETRIES = 4;
    protected static final int INDEX_REPETITION_DELAY = 5;

    protected GetPushSetupCommandExecutor() {
        // hide public contructor, but keep this accessable by subclasses
    }

    protected static void checkResultList(final List<GetResult> getResultList,
            final AttributeAddress[] attributeAddresses) throws ProtocolAdapterException {
        if (getResultList.isEmpty()) {
            throw new ProtocolAdapterException("No GetResult received while retrieving Push Setup table.");
        }

        if (getResultList.size() != attributeAddresses.length) {
            throw new ProtocolAdapterException("Expected " + attributeAddresses.length
                    + " GetResults while retrieving Push Setup table, got " + getResultList.size());
        }
    }
}
