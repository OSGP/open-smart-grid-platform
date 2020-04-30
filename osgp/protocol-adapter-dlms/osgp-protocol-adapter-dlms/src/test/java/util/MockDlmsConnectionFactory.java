package util;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionFactory;

/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
public class MockDlmsConnectionFactory extends DlmsConnectionFactory {
    public MockDlmsConnectionFactory(/*Hls5Connector hls5Connector, Lls1Connector lls1Connector,
            Lls0Connector lls0Connector, DomainHelperService domainHelperService*/) {
        super(new Hls5ConnectorStub(), null, null, null);
    }

}
