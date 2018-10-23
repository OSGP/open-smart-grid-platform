/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.domain.entities;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embeddable;

@Embeddable
public final class NotificationWebServiceLookupKey implements Serializable {

    private static final long serialVersionUID = -6367887742782273500L;

    private String organisationIdentification;
    private String applicationName;

    public NotificationWebServiceLookupKey() {
        /*
         * Public no-argument constructor, required for JPA composite primary
         * key class.
         */
    }

    public NotificationWebServiceLookupKey(final String organisationIdentification) {
        this(organisationIdentification, "");
    }

    public NotificationWebServiceLookupKey(final String organisationIdentification, final String applicationName) {
        this.organisationIdentification = Objects.requireNonNull(organisationIdentification,
                "organisationIdentification must not be null");
        this.applicationName = Objects.requireNonNull(applicationName, "applicationName must not be null");
    }

    public String getOrganisationIdentification() {
        return this.organisationIdentification;
    }

    public String getApplicationName() {
        return this.applicationName;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof NotificationWebServiceLookupKey)) {
            return false;
        }
        final NotificationWebServiceLookupKey other = (NotificationWebServiceLookupKey) obj;
        return Objects.equals(this.getOrganisationIdentification(), other.getOrganisationIdentification())
                && Objects.equals(this.getApplicationName(), other.getApplicationName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getOrganisationIdentification(), this.getApplicationName());
    }

    @Override
    public String toString() {
        return String.format("%s[organisation=%s, application=%s]", this.getClass().getSimpleName(),
                this.getOrganisationIdentification(), this.getApplicationName());
    }

}
