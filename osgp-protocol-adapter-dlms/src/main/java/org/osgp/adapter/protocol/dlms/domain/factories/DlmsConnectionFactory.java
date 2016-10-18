/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.factories;

import java.io.IOException;

import javax.inject.Provider;
import javax.naming.OperationNotSupportedException;

import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.shared.exceptionhandling.TechnicalException;

@Component
public class DlmsConnectionFactory {

    @Autowired
    private Provider<Hls5Connector> hls5ConnectorProvider;

    /**
     * Returns an open connection using the appropriate security settings for
     * the device.
     *
     * @param device
     *            The device to connect to. This reference can be updated when
     *            the invalid but correctable connection credentials are
     *            detected.
     * @return an open connection
     * @throws IOException
     * @throws OperationNotSupportedException
     */
    public DlmsConnectionHolder getConnection(final DlmsDevice device) throws TechnicalException {
        if (device.isHls5Active()) {
            final Hls5Connector connector = this.hls5ConnectorProvider.get();
            connector.setDevice(device);
            return connector.connect();
        } else {
            throw new UnsupportedOperationException("Only HLS 5 connections are currently supported");
        }
    }
}
