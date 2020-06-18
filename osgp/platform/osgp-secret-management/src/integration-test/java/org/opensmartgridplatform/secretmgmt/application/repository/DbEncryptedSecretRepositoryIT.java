package org.opensmartgridplatform.secretmgmt.application.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.apache.tomcat.util.buf.HexUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.secretmgmt.application.domain.DbEncryptedSecret;
import org.opensmartgridplatform.secretmgmt.application.domain.DbEncryptionKeyReference;
import org.opensmartgridplatform.secretmgmt.application.domain.SecretType;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.providers.EncryptionProviderType;
import org.springframework.beans.factory.annotation.Autowired;

public class DbEncryptedSecretRepositoryIT extends AbstractRepositoryIT {
    @Autowired
    private DbEncryptedSecretRepository repository;

    private static final String DEVICE_IDENTIFICATION = "TestDevice00123";

    private DbEncryptedSecret dbEncryptedSecret;

    @BeforeEach
    public void persistTestData() {
        DbEncryptionKeyReference encryptionKey = new DbEncryptionKeyReference();
        encryptionKey.setCreationTime(new Date());
        encryptionKey.setReference("keyRef1");
        encryptionKey.setEncryptionProviderType(EncryptionProviderType.HSM);
        encryptionKey.setValidFrom(new Date(System.currentTimeMillis() - 60000));
        encryptionKey.setVersion(1L);
        encryptionKey = this.entityManager.persist(encryptionKey);
        final DbEncryptedSecret instance = new DbEncryptedSecret();
        instance.setDeviceIdentification(DEVICE_IDENTIFICATION);
        instance.setSecretType(SecretType.E_METER_AUTHENTICATION_KEY);
        instance.setEncodedSecret(HexUtils.toHexString("$3cr3t".getBytes()));
        instance.setEncryptionKeyReference(encryptionKey);
        instance.setCreationTime(new Date());
        this.dbEncryptedSecret = this.entityManager.persist(instance);
        this.entityManager.flush();
    }

    @Test
    public void findIdOfValidMostRecent() {
        assertThat(this.repository.count()).isEqualTo(1);
        final Long id = this.repository.findIdOfValidMostRecent(this.dbEncryptedSecret.getDeviceIdentification(),
                this.dbEncryptedSecret.getSecretType().name(), new Date());
        assertThat(id).isEqualTo(this.dbEncryptedSecret.getId());
    }
}
