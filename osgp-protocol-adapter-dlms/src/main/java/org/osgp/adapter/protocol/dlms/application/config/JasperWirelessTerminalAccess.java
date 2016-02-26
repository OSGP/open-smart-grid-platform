/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.config;

public class JasperWirelessTerminalAccess extends JasperWirelessAbstractAccess {

    private String terminalUri;

    public JasperWirelessTerminalAccess(final String terminalUri, final String licenseKey, final String username,
            final String password, final String apiVersion) {
        super(licenseKey, username, password, apiVersion);
        this.terminalUri = terminalUri;
    }

    public String getTerminalUri() {
        return this.terminalUri;
    }

}
