/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.helpers;

public class Protocol {

    private final ProtocolType type;
    private final String protocol;
    private final String version;

    public Protocol(final ProtocolType type, final String protocol, final String version) {
        super();
        this.type = type;
        this.protocol = protocol;
        this.version = version;
    }

    public ProtocolType getType() {
        return this.type;
    }

    public String getProtocol() {
        return this.protocol;
    }

    public String getVersion() {
        return this.version;
    }

    public enum ProtocolType {
        OSLP,
        DSMR,
        DLMS;
    }

}
