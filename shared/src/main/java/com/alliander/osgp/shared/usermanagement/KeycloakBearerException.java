/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.shared.usermanagement;

/**
 * Exception signalling an issue with the bearer authorization token used with a
 * call to the Keycloak REST API.
 * <p>
 * If this exception occurs and a token was used, it may have expired. A retry
 * of the call after obtaining a new access token may help to recover from the
 * exception.
 * <p>
 * In order to satisfy the above documentation, this exception should probably
 * only be thrown when the Keycloak API responds with the literal text "Bearer"
 * instead of a JSON or empty response.
 */
public class KeycloakBearerException extends KeycloakClientException {

    private static final long serialVersionUID = -1540745002060657648L;

    public KeycloakBearerException() {
        super("No valid bearer token present");
    }

    public KeycloakBearerException(final String message) {
        super(message);
    }
}
