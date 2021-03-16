package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands;

import java.io.Serializable;

import lombok.Getter;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.RequestWithMetadata;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@Getter
public class CorrelatedObject<T> implements ActionRequestDto {
    private static final long serialVersionUID = -6205572886092338803L;
    private final String correlationUid;
    private final T object;

    private CorrelatedObject(final String correlationUid, final T object) {
        this.correlationUid = correlationUid;
        this.object = object;
    }

    public static <T> CorrelatedObject<T> from(final CorrelatedObject<?> correlationSource, final T object) {
        return from(correlationSource.correlationUid, object);
    }

    public static <T extends Serializable> CorrelatedObject<T> from(final RequestWithMetadata<T> request) {
        return from(request.getMetadata(), request.getRequestObject());
    }

    public static <T> CorrelatedObject<T> from(final RequestWithMetadata<?> request, final T object) {
        return from(request.getMetadata(), object);
    }

    public static <T> CorrelatedObject<T> from(final MessageMetadata metadata, final T object) {
        return from(metadata.getCorrelationUid(), object);
    }

    public static <T> CorrelatedObject<T> from(final String correlationUid, final T object) {
        return new CorrelatedObject<>(correlationUid, object);
    }
}
