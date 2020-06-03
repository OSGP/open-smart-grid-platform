package org.opensmartgridplatform.secretmgmt.application.services;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.opensmartgridplatform.secretmgmt.application.domain.DbEncryptedSecret;
import org.opensmartgridplatform.secretmgmt.application.domain.DbEncryptionKeyReference;
import org.opensmartgridplatform.secretmgmt.application.domain.SecretType;
import org.opensmartgridplatform.secretmgmt.application.domain.TypedSecret;
import org.opensmartgridplatform.secretmgmt.application.repository.DbEncryptedSecretRepository;
import org.opensmartgridplatform.secretmgmt.application.repository.DbEncryptionKeyRepository;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.EncryptionDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SecretManagementService implements SecretManagement {

    private final EncryptionDelegate encryptionDelegate;
    private final DbEncryptedSecretRepository secretRepository;
    private final DbEncryptionKeyRepository keyRepository;

    @Autowired
    public SecretManagementService(final EncryptionDelegate encryptionDelegate,
            final DbEncryptedSecretRepository secretRepository,
            final DbEncryptionKeyRepository keyRepository) {
        this.encryptionDelegate = encryptionDelegate;
        this.secretRepository = secretRepository;
        this.keyRepository = keyRepository;
    }

    @Override
    public void storeSecrets(final String deviceIdentification, final List<TypedSecret> secrets) throws Exception {
        //@formatter:off
        secrets.stream()
                .map(s -> this.createEncrypted(deviceIdentification, s, this.getKey(s)))
                .forEach(e -> this.secretRepository.save(e));
        //@formatter:on
    }

    private DbEncryptionKeyReference getKey(final TypedSecret typedSecret) {
        //TODO: determine encryption provider type?
        final Date now = new Date(); //TODO: UTC?
        final Page<DbEncryptionKeyReference> keyRefsPage = this.keyRepository.findValidOrderedByValidFrom(now,
                PageRequest.of(1, 1));
        return keyRefsPage.stream().findFirst().get();
    }

    private DbEncryptedSecret createEncrypted(final String deviceIdentification, final TypedSecret typedSecret,
            final DbEncryptionKeyReference keyReference) {
        final DbEncryptedSecret encryptedSecret = new DbEncryptedSecret();
        encryptedSecret.setDeviceIdentification(deviceIdentification);
        encryptedSecret.setEncodedSecret(typedSecret.getSecret());   //TODO encode & encrypt
        encryptedSecret.setSecretType(typedSecret.getSecretType());
        encryptedSecret.setEncryptionKeyReference(keyReference);
        return encryptedSecret;
    }

    @Override
    public List<TypedSecret> retrieveSecrets(final String deviceIdentification, final List<SecretType> secretTypes)
            throws Exception {
        final Pageable pageable = PageRequest.of(1, 1);
        final Date now = new Date(); //TODO: UTC?
        //@formatter:off
        return secretTypes.stream()
                .map(s -> this.secretRepository.findOrderedByKeyValidFrom(deviceIdentification, s, now, pageable))
                .map(page -> page.stream().findFirst().get())
                .map(es -> this.getTypedSecret(es))
                .collect(Collectors.toList());
        //@formatter:on
    }

    private TypedSecret getTypedSecret(final DbEncryptedSecret dbEncryptedSecret) {
        if (dbEncryptedSecret != null) {
            final TypedSecret typedSecret = new TypedSecret();
            //TODO: this should be decoded & decrypted using encryptiondelegate
            typedSecret.setSecret(dbEncryptedSecret.getEncodedSecret());
            typedSecret.setSecretType(dbEncryptedSecret.getSecretType());
            return typedSecret;
        } else {
            return null;
        }
    }
}
