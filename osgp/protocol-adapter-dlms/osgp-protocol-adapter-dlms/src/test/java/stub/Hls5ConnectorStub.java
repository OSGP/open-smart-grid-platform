// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package stub;

import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.TcpConnectionBuilder;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.stub.DlmsConnectionStub;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsDeviceAssociation;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.Hls5Connector;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.SecurityKeyProvider;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

public class Hls5ConnectorStub extends Hls5Connector {

  public Hls5ConnectorStub() {
    super(null, 0, 0, DlmsDeviceAssociation.PUBLIC_CLIENT, null, null);
  }

  @Override
  protected void setSecurity(
      final MessageMetadata messageMetadata,
      final DlmsDevice device,
      final SecurityKeyProvider keyProvider,
      final TcpConnectionBuilder tcpConnectionBuilder) {
    //
  }

  @Override
  public DlmsConnection connect(
      final MessageMetadata messageMetadata,
      final DlmsDevice device,
      final DlmsMessageListener dlmsMessageListener) {
    return new DlmsConnectionStub();
  }
}
