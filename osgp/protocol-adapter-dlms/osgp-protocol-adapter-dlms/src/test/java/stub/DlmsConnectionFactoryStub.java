/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package stub;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionFactory;

public class DlmsConnectionFactoryStub extends DlmsConnectionFactory {
  public DlmsConnectionFactoryStub() {
    super(new Hls5ConnectorStub(), null, null, null);
  }
}
