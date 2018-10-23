/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.factories;

import javax.inject.Provider;

import org.openmuc.jdlms.DlmsConnection;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.DomainHelperService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;

@Component
public class DlmsConnectionFactory {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DlmsConnectionFactory.class);

    @Autowired
    @Qualifier("hls5Connector")
    private Provider<DlmsConnector> hls5ConnectorProvider;

    @Autowired
    @Qualifier("lls1Connector")
    private Provider<DlmsConnector> lls1ConnectorProvider;

    @Autowired
    @Qualifier("lls0Connector")
    private Provider<DlmsConnector> lls0ConnectorProvider;

    @Autowired
    private DomainHelperService domainHelperService;

    /**
     * Returns an open connection using the appropriate security settings for
     * the device.
     *
     * @param device
     *            The device to connect to. This reference can be updated when
     *            the invalid but correctable connection credentials are
     *            detected.
     * @param dlmsMessageListener
     *            A message listener that will be provided to the
     *            {@link DlmsConnection} that is initialized if the given
     *            {@code device} is in {@link DlmsDevice#isInDebugMode() debug
     *            mode}. If this is {@code null} no DLMS device communication
     *            debug logging will be done.
     * @return a holder providing access to an open DLMS connection as well as
     *         an optional message listener active in the connection.
     * @throws OsgpException
     *             in case of a TechnicalException or FunctionalException
     */
    public DlmsConnectionHolder getConnection(final DlmsDevice device, final DlmsMessageListener dlmsMessageListener)
            throws OsgpException {

        DlmsConnector connector;
        if (device.isHls5Active()) {
            connector = this.hls5ConnectorProvider.get();
        } else if (device.isLls1Active()) {
            connector = this.lls1ConnectorProvider.get();
        } else if (device.communicateUnencrypted()) {
            connector = this.lls0ConnectorProvider.get();
        } else {
            LOGGER.error("Only HLS 5, LLS 1 and public (LLS 0) connections are currently supported");
            throw new FunctionalException(FunctionalExceptionType.UNSUPPORTED_COMMUNICATION_SETTING,
                    ComponentType.PROTOCOL_DLMS);
        }

        final DlmsConnectionHolder holder = new DlmsConnectionHolder(connector, device, dlmsMessageListener,
                this.domainHelperService);
        holder.connect();
        return holder;
    }
}
