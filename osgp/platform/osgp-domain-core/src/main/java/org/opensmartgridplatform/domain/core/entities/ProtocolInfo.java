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
    private String outgoingRequestsPropertyPrefix;

    @Column(nullable = false, length = 255)
    private String incomingResponsesPropertyPrefix;

    @Column(nullable = false, length = 255)
    private String incomingRequestsPropertyPrefix;

    @Column(nullable = false, length = 255)
    private String outgoingResponsesPropertyPrefix;

    @Column
    private boolean parallelRequestsAllowed;

    protected ProtocolInfo() {
        // Default constructor
    }

    private ProtocolInfo(final Builder builder) {
        this.protocol = builder.protocol;
        this.protocolVersion = builder.protocolVersion;
        this.outgoingRequestsPropertyPrefix = builder.outgoingRequestsPropertyPrefix;
        this.incomingResponsesPropertyPrefix = builder.incomingResponsesPropertyPrefix;
        this.incomingRequestsPropertyPrefix = builder.incomingRequestsPropertyPrefix;
        this.outgoingResponsesPropertyPrefix = builder.outgoingResponsesPropertyPrefix;
        this.parallelRequestsAllowed = builder.parallelRequestsAllowed;
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

    public String getOutgoingRequestsPropertyPrefix() {
        return this.outgoingRequestsPropertyPrefix;
    }

    public String getIncomingResponsesPropertyPrefix() {
        return this.incomingResponsesPropertyPrefix;
    }

    public String getIncomingRequestsPropertyPrefix() {
        return this.incomingRequestsPropertyPrefix;
    }

    public String getOutgoingResponsesPropertyPrefix() {
        return this.outgoingResponsesPropertyPrefix;
    }

    public boolean isParallelRequestsAllowed() {
        return this.parallelRequestsAllowed;
    }

    public static class Builder {
        private String protocol;
        private String protocolVersion;
        private String outgoingRequestsPropertyPrefix;
        private String incomingResponsesPropertyPrefix;
        private String incomingRequestsPropertyPrefix;
        private String outgoingResponsesPropertyPrefix;
        private Boolean parallelRequestsAllowed;

        public Builder() {
            // Default constructor.
        }

        public Builder withProtocol(final String protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder withProtocolVersion(final String protocolVersion) {
            this.protocolVersion = protocolVersion;
            return this;
        }

        public Builder withOutgoingRequestsPropertyPrefix(final String propertyPrefix) {
            this.outgoingRequestsPropertyPrefix = propertyPrefix;
            return this;
        }

        public Builder withIncomingResponsesPropertyPrefix(final String propertyPrefix) {
            this.incomingResponsesPropertyPrefix = propertyPrefix;
            return this;
        }

        public Builder withIncomingRequestsPropertyPrefix(final String propertyPrefix) {
            this.incomingRequestsPropertyPrefix = propertyPrefix;
            return this;
        }

        public Builder withOutgoingResponsesPropertyPrefix(final String propertyPrefix) {
            this.outgoingResponsesPropertyPrefix = propertyPrefix;
            return this;
        }

        public Builder withParallelRequestsAllowed(final boolean parallelRequestsAllowed) {
            this.parallelRequestsAllowed = parallelRequestsAllowed;
            return this;
        }

        public ProtocolInfo build() {
            return new ProtocolInfo(this);
        }
    }
}
