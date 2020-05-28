package org.opensmartgridplatform.secretmgmt.application.services;

import org.opensmartgridplatform.secretmgmt.application.domain.SecretType;
import org.opensmartgridplatform.secretmgmt.application.domain.TypedSecret;

import java.util.List;

public interface SecretManagement {
    void storeSecrets(String deviceIdentification, List<TypedSecret> secrets) throws Exception;
    List<TypedSecret> retrieveSecrets(String deviceIdentification, List<SecretType> secretTypes) throws Exception;
    TypedSecret generateSecret(SecretType secretType) throws Exception;
}
