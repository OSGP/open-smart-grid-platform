/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.entities;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.alliander.osgp.shared.domain.entities.AbstractEntity;

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
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final ProtocolInfo protocolInfo = (ProtocolInfo) o;
        if (!protocolInfo.getProtocol().equals(this.protocol)) {
            return false;
        }
        if (!protocolInfo.getProtocolVersion().equals(this.protocolVersion)) {
            return false;
        }
        if (!protocolInfo.getOutgoingProtocolRequestsQueue().equals(this.outgoingProtocolRequestsQueue)) {
            return false;
        }
        if (!protocolInfo.getIncomingProtocolResponsesQueue().equals(this.incomingProtocolResponsesQueue)) {
            return false;
        }
        if (!protocolInfo.getIncomingProtocolRequestsQueue().equals(this.incomingProtocolRequestsQueue)) {
            return false;
        }
        if (!protocolInfo.getOutgoingProtocolResponsesQueue().equals(this.outgoingProtocolResponsesQueue)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = this.protocol != null ? this.protocol.hashCode() : 0;
        result = 31 * result + (this.protocolVersion != null ? this.protocolVersion.hashCode() : 0);
        result = 31 * result
                + (this.outgoingProtocolRequestsQueue != null ? this.outgoingProtocolRequestsQueue.hashCode() : 0);
        result = 31 * result
                + (this.incomingProtocolResponsesQueue != null ? this.incomingProtocolResponsesQueue.hashCode() : 0);
        result = 31 * result
                + (this.incomingProtocolRequestsQueue != null ? this.incomingProtocolRequestsQueue.hashCode() : 0);
        result = 31 * result
                + (this.outgoingProtocolResponsesQueue != null ? this.outgoingProtocolResponsesQueue.hashCode() : 0);
        return result;
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
