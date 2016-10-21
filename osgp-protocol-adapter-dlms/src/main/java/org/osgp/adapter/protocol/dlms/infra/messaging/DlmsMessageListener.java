package org.osgp.adapter.protocol.dlms.infra.messaging;

import org.openmuc.jdlms.RawMessageListener;

public interface DlmsMessageListener extends RawMessageListener {

    void setMessageMetadata(DlmsDeviceMessageMetadata messageMetadata);

    void setDescription(String description);
}