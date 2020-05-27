package org.opensmartgridplatform.secretmgmt.application.services;

import org.opensmartgridplatform.secretmgmt.application.services.encryption.EncryptionDelegate;
import org.springframework.stereotype.Service;

@Service
public class SecretMangementService {

    private final EncryptionDelegate encryptionDelegate;

        public SecretMangementService(EncryptionDelegate encryptionDelegate) {
        this.encryptionDelegate = encryptionDelegate;
    }


}
