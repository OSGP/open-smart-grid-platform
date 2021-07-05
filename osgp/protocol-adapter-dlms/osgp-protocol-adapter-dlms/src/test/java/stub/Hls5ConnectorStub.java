/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package stub;

import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.TcpConnectionBuilder;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.stub.DlmsConnectionStub;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsDeviceAssociation;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.Hls5Connector;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;

public class Hls5ConnectorStub extends Hls5Connector {

  public Hls5ConnectorStub() {
    super(null, 0, 0, DlmsDeviceAssociation.PUBLIC_CLIENT);
  }

  @Override
  protected void setSecurity(
      final String correlationUid,
      final DlmsDevice device,
      final TcpConnectionBuilder tcpConnectionBuilder) {
    //
  }

  @Override
  public DlmsConnection connect(
      final String correlationUid,
      final DlmsDevice device,
      final DlmsMessageListener dlmsMessageListener) {
    return new DlmsConnectionStub();
  }
}
