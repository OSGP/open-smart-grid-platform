/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.stub;

import org.openmuc.jdlms.DlmsConnection;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;

public class DlmsConnectionManagerStub extends DlmsConnectionManager {

  private final DlmsConnectionStub dlmsConnectionStub;

  public DlmsConnectionManagerStub(final DlmsConnectionStub dlmsConnectionStub) {
    super(null, null, null, null);

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
