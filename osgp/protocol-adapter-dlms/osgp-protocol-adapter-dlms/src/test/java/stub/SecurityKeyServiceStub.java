/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package stub;

import org.opensmartgridplatform.adapter.protocol.dlms.application.services.SecurityKeyService;

public class SecurityKeyServiceStub extends SecurityKeyService {
    public SecurityKeyServiceStub(/*DlmsDeviceRepository dlmsDeviceRepository, EncryptionService encryptionService,
            RsaEncryptionService rsaEncryptionService*/) {
        super(null, null, null);
    }
}
