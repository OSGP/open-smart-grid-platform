/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.secretmanagement.application.services.encryption.providers;

import org.opensmartgridplatform.secretmanagement.application.services.encryption.EncryptedSecret;
import org.opensmartgridplatform.secretmanagement.application.services.encryption.EncryptionProviderType;
import org.opensmartgridplatform.secretmanagement.application.services.encryption.Secret;

public interface EncryptionProvider {
    EncryptedSecret encrypt(Secret secret, String keyReference);
    Secret decrypt(EncryptedSecret secret, String keyReference);
    EncryptionProviderType getType();
}

