package util;

import org.opensmartgridplatform.adapter.protocol.dlms.application.services.SecurityKeyService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.shared.security.EncryptionService;
import org.opensmartgridplatform.shared.security.RsaEncryptionService;

/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
public class SecurityKeyServiceStub extends SecurityKeyService {
    public SecurityKeyServiceStub(/*DlmsDeviceRepository dlmsDeviceRepository, EncryptionService encryptionService,
            RsaEncryptionService rsaEncryptionService*/) {
        super(null, null, null);
    }
}
