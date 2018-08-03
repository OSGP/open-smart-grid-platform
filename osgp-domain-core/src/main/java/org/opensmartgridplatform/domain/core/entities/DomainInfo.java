/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.entities;

import java.util.Objects;

import javax.persistence.Entity;

import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;

/**
 * Class containing information about a domain and the domain's destination
 * queues.
 */
@Entity
public class DomainInfo extends AbstractEntity {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 2722628660439903065L;

    private String domain;
    private String domainVersion;
    private String incomingDomainRequestsQueue;
    private String outgoingDomainResponsesQueue;
    private String outgoingDomainRequestsQueue;
    private String incomingDomainResponsesQueue;

    @SuppressWarnings("unused")
    private DomainInfo() {
        // Default constructor for Hibernate
    }

    /**
     * Construct a DomainInfo instance.
     *
     * @param domain
     *            The name of the domain.
     * @param version
     *            The version of the domain.
     * @param requestsQueueOut
     *            The queue where domain requests will be routed to.
     * @param responsesQueueOut
     *            The queue where domain responses will be routed to.
     * @param outgoingRequestsQueue
     *            The queue where incoming domain requests will be routed to.
     * @param incomingResponsesQueue
     *            The queue where incoming domain responses will be routed to.
     */
    public DomainInfo(final String domain, final String domainVersion, final String incomingDomainRequestsQueue,
            final String outgoingDomainResponsesQueue, final String outgoingDomainRequestsQueue,
            final String incomingDomainResponsesQueue) {
        this.domain = domain;
        this.domainVersion = domainVersion;
        this.incomingDomainRequestsQueue = incomingDomainRequestsQueue;
        this.outgoingDomainResponsesQueue = outgoingDomainResponsesQueue;
        this.outgoingDomainRequestsQueue = outgoingDomainRequestsQueue;
        this.incomingDomainResponsesQueue = incomingDomainResponsesQueue;
    }

    public static String getKey(final String domain, final String domainVersion) {
        return createKey(domain, domainVersion);
    }

    public String getKey() {
        return createKey(this.domain, this.domainVersion);
    }

    private static String createKey(final String protocol, final String version) {
        return protocol + "-" + version;
    }

    /**
     * The name of the domain.
     *
     * @return The name of the domain.
     */
    public String getDomain() {
        return this.domain;
    }

    /**
     * The version of the domain.
     *
     * @return The version of the domain.
     */
    public String getDomainVersion() {
        return this.domainVersion;
    }

    /**
     * The queue where the domain request message listener will listen for
     * received domain requests.
     *
     * @return The queue where incoming domain requests will be send to.
     */
    public String getIncomingDomainRequestsQueue() {
        return this.incomingDomainRequestsQueue;
    }

    /**
     * The queue where the domain response message sender will send domain
     * responses to.
     *
     * @return The queue where outgoing domain responses will be send to.
     */
    public String getOutgoingDomainResponsesQueue() {
        return this.outgoingDomainResponsesQueue;
    }

    /**
     * The queue where the domain request message sender will send domain
     * requests to.
     *
     * @return The queue where outgoing domain requests will be send to.
     */
    public String getOutgoingDomainRequestsQueue() {
        return this.outgoingDomainRequestsQueue;
    }

    /**
     * The queue where the domain response message listener will listen for
     * received domain responses.
     *
     * @return The queue where incoming domain responses will be received.
     */
    public String getIncomingDomainResponsesQueue() {
        return this.incomingDomainResponsesQueue;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DomainInfo)) {
            return false;
        }
        final DomainInfo domainInfo = (DomainInfo) o;
        return Objects.equals(this.getKey(), domainInfo.getKey());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.getKey());
    }
}
