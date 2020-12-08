/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.exceptions;

/**
 * Exception indicating that the device's session has terminated after its invocation counter has been read.
 * The session being terminated is due to a current limitation in the OpenMUC jDLMS library that does not allow a
 * single TCP connection to be used for connecting to both the Public Client and the Management Client of the device
 * successively.
 */
public class DeviceSessionTerminatedAfterReadingInvocationCounterException extends RuntimeException implements SilentException {
    private static final long serialVersionUID = -5829526280696092035L;

    public DeviceSessionTerminatedAfterReadingInvocationCounterException(final String deviceIdentification) {
        super(String.format("The session of device %s has terminated after its invocation counter has been read. ",
                deviceIdentification));
    }
}
