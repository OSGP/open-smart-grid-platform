package org.opensmartgridplatform.secretmgmt.application.services;

import org.opensmartgridplatform.secretmgmt.application.domain.SecretType;
import org.opensmartgridplatform.secretmgmt.application.domain.TypedSecret;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.EncryptionDelegate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SecretManagementService implements SecretManagement {

    private EncryptionDelegate encryptionDelegate;

    public SecretManagementService(EncryptionDelegate encryptionDelegate) {
        this.encryptionDelegate = encryptionDelegate;
    }

    @Override
    public void storeSecrets(String deviceIdentification, List<TypedSecret> secrets) throws Exception {

    }

    @Override
    public List<TypedSecret> retrieveSecrets(String deviceIdentification, List<SecretType> secretTypes) throws Exception {
        return null;
    }

}
