/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.entities;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;

@Entity
public class ProtocolInfo extends AbstractEntity {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 3159641350358660380L;

    @Column(nullable = false, length = 255)
    private String protocol;

    @Column(nullable = false, length = 255)
    private String protocolVersion;

    @Column(nullable = false, length = 255)
    private String outgoingProtocolRequestsQueue;

    @Column(nullable = false, length = 255)
    private String incomingProtocolResponsesQueue;

    @Column(nullable = false, length = 255)
    private String incomingProtocolRequestsQueue;

    @Column(nullable = false, length = 255)
    private String outgoingProtocolResponsesQueue;

    public ProtocolInfo() {
        // Default constructor
    }

    public ProtocolInfo(final String protocol, final String protocolVersion,
            final String outgoingProtocolRequestsQueue, final String incomingProtocolResponsesQueue,
            final String incomingProtocolRequestsQueue, final String outgoingProtocolResponsesQueue) {
        this.protocol = protocol;
        this.protocolVersion = protocolVersion;
        this.outgoingProtocolRequestsQueue = outgoingProtocolRequestsQueue;
        this.incomingProtocolResponsesQueue = incomingProtocolResponsesQueue;
        this.incomingProtocolRequestsQueue = incomingProtocolRequestsQueue;
        this.outgoingProtocolResponsesQueue = outgoingProtocolResponsesQueue;
    }

    public static String getKey(final String protocol, final String protocolVersion) {
        return createKey(protocol, protocolVersion);
    }

    public String getKey() {
        return createKey(this.protocol, this.protocolVersion);
    }

    private static String createKey(final String protocol, final String version) {
        return protocol + "-" + version;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProtocolInfo)) {
            return false;
        }
        final ProtocolInfo protocolInfo = (ProtocolInfo) o;
        final boolean isProtocolEqual = Objects.equals(this.protocol, protocolInfo.protocol);
        final boolean isProtocolVersionEqual = Objects.equals(this.protocolVersion, protocolInfo.protocolVersion);

        return isProtocolEqual && isProtocolVersionEqual;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.protocol, this.protocolVersion);
    }

    public String getProtocol() {
        return this.protocol;
    }

    public String getProtocolVersion() {
        return this.protocolVersion;
    }

    public String getOutgoingProtocolRequestsQueue() {
        return this.outgoingProtocolRequestsQueue;
    }

    public String getIncomingProtocolResponsesQueue() {
        return this.incomingProtocolResponsesQueue;
    }

    public String getIncomingProtocolRequestsQueue() {
        return this.incomingProtocolRequestsQueue;
    }

    public String getOutgoingProtocolResponsesQueue() {
        return this.outgoingProtocolResponsesQueue;
    }
}
