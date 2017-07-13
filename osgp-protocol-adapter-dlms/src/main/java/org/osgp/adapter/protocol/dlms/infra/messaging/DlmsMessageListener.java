package org.osgp.adapter.protocol.dlms.infra.messaging;

import org.openmuc.jdlms.RawMessageListener;

import com.alliander.osgp.shared.infra.jms.MessageMetadata;

public interface DlmsMessageListener extends RawMessageListener {

    void setMessageMetadata(MessageMetadata messageMetadata);

    void setDescription(String description);
}