// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package stub;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionFactory;

public class DlmsConnectionFactoryStub extends DlmsConnectionFactory {
  public DlmsConnectionFactoryStub() {
    super(new Hls5ConnectorStub(), null, null, null);
  }
}
