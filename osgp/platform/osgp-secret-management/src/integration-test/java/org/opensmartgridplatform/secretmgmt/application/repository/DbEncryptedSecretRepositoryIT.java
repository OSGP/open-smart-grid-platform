package org.opensmartgridplatform.secretmgmt.application.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.secretmgmt.application.domain.DbEncryptedSecret;
import org.opensmartgridplatform.secretmgmt.application.domain.DbEncryptionKeyReference;
import org.opensmartgridplatform.secretmgmt.application.domain.SecretType;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.providers.EncryptionProviderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest(showSql = false, excludeAutoConfiguration = FlywayAutoConfiguration.class)
@TestPropertySource(properties = { "spring.jpa.hibernate.ddl-auto=update", "spring.main.banner-mode=off" })
public class DbEncryptedSecretRepositoryIT {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DbEncryptedSecretRepository repository;

    private static final String DEVICE_IDENTIFICATION = "TestDevice00123";

    @BeforeEach
    public void persistTestData() {
        DbEncryptionKeyReference encryptionKey = new DbEncryptionKeyReference();
        encryptionKey.setCreationTime(new Date());
        encryptionKey.setReference("keyRef1");
        encryptionKey.setEncryptionProviderType(EncryptionProviderType.HSM);
        encryptionKey.setValidFrom(new Date(System.currentTimeMillis() - 60000));
        encryptionKey.setVersion(1L);
        encryptionKey = this.entityManager.persist(encryptionKey);
        DbEncryptedSecret instance = new DbEncryptedSecret();
        instance.setDeviceIdentification(DEVICE_IDENTIFICATION);
        instance.setSecretType(SecretType.E_METER_AUTHENTICATION_KEY);
        instance.setEncodedSecret("$3cr3t");
        instance.setEncryptionKeyReference(encryptionKey);
        instance = this.entityManager.persist(instance);
        this.entityManager.flush();
    }

    @Test
    public void find() {
        final Page<DbEncryptedSecret> resultPage = this.repository.findValidOrderedByKeyValidFrom(DEVICE_IDENTIFICATION,
                SecretType.E_METER_AUTHENTICATION_KEY, EncryptionProviderType.HSM, new Date(), Pageable.unpaged());
        assertThat(resultPage.toList().size()).isEqualTo(1);
        final DbEncryptedSecret result = resultPage.toList().get(0);
        assertThat(result.getDeviceIdentification()).isEqualTo(DEVICE_IDENTIFICATION);
        assertThat(result.getEncryptionKeyReference()).isNotNull();
        final DbEncryptionKeyReference keyReference = result.getEncryptionKeyReference();
        assertThat(keyReference.getEncryptionProviderType()).isEqualTo(EncryptionProviderType.HSM);
    }
}
