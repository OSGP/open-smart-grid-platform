package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging;

import java.io.Serializable;

import lombok.Getter;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@Getter
public class RequestWithMetadata<S extends Serializable> {
    private final MessageMetadata metadata;
    private final S requestObject;

    RequestWithMetadata(final MessageMetadata metadata, final S requestObject) {
        this.metadata = metadata;
        this.requestObject = requestObject;
    }
}
