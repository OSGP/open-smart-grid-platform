// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.stub;

import org.openmuc.jdlms.DlmsConnection;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;

public class DlmsConnectionManagerStub extends DlmsConnectionManager {

  private final DlmsConnectionStub dlmsConnectionStub;

  public DlmsConnectionManagerStub(final DlmsConnectionStub dlmsConnectionStub) {
    super(null, null, null, null, null);

    this.dlmsConnectionStub = dlmsConnectionStub;
  }

  @Override
  public DlmsConnection getConnection() {
    return this.dlmsConnectionStub;
  }

  @Override
  public void close() {
    this.dlmsConnectionStub.close();
  }
}
