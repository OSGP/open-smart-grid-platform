package org.opensmartgridplatform.secretmgmt.application.endpoints;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.Caller;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.GetSecretsRequest;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.GetSecretsResponse;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.SecretType;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.SecretTypes;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.TypedSecret;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.TypedSecrets;
import org.opensmartgridplatform.secretmgmt.application.domain.DbEncryptedSecret;
import org.opensmartgridplatform.secretmgmt.application.domain.DbEncryptionKeyReference;
import org.opensmartgridplatform.secretmgmt.application.services.SecretManagementService;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.providers.EncryptionProvider;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.providers.EncryptionProviderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false, excludeAutoConfiguration = FlywayAutoConfiguration.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SecretManagementEndpointIT {

    private static final String DEVICE_IDENTIFICATION="E0054002019112319";

    @Autowired
    private SecretManagementEndpoint secretManagementEndpoint;

    @Autowired
    private SecretManagementService secretManagementService;

    @Autowired
    private EncryptionProvider soapSecretEncryptionProvider;

    @Autowired
    private TestEntityManager entityManager;

    //@BeforeAll

    @BeforeEach
    public void beforeEachTest() {
        secretManagementEndpoint = new SecretManagementEndpoint(secretManagementService, soapSecretEncryptionProvider);
        createTestData();
    }

    @Test
    public void getSecretsBySoapCall() {
        GetSecretsRequest request = new GetSecretsRequest();
        Caller caller = new Caller();
        caller.setUserName("soap-api-user");
        caller.setOrganisationIdentification("soap-user-org");
        caller.setApplicationName("TEST");
        request.setCaller(caller);

        SecretTypes secretTypesToGet = new SecretTypes();

        List<SecretType> secretTypeList = secretTypesToGet.getSecretType();

        secretTypeList.add(SecretType.E_METER_AUTHENTICATION_KEY);
        secretTypeList.add(SecretType.E_METER_ENCRYPTION_KEY_UNICAST);

        request.setSecretTypes(secretTypesToGet);
        request.setCorrelationUid("1234");
        request.setDeviceId(DEVICE_IDENTIFICATION);

        GetSecretsResponse response = secretManagementEndpoint.getSecretsRequest(request);

        TypedSecrets typedSecrets = response.getTypedSecrets();

        List<TypedSecret> listOfTypedSecrets = typedSecrets.getTypedSecret();

        assertThat(listOfTypedSecrets.size()).isEqualTo(2);

    }

    public void createTestData() {
        DbEncryptionKeyReference encryptionKey = new DbEncryptionKeyReference();
        encryptionKey.setCreationTime(new Date());
        encryptionKey.setReference("1");
        encryptionKey.setEncryptionProviderType(EncryptionProviderType.JRE);
        encryptionKey.setValidFrom(new Date(System.currentTimeMillis()-60000));
        encryptionKey.setVersion(1L);
        encryptionKey = this.entityManager.persist(encryptionKey);

        DbEncryptedSecret encryptedSecret = new DbEncryptedSecret();
        encryptedSecret.setDeviceIdentification(DEVICE_IDENTIFICATION);
        encryptedSecret.setSecretType(org.opensmartgridplatform.secretmgmt.application.domain.SecretType.E_METER_AUTHENTICATION_KEY);
        encryptedSecret.setEncodedSecret("e768f22cefde87f9ec1b535047799e2150042a0ada765081e0a652c0995bf3d8");
        encryptedSecret.setEncryptionKeyReference(encryptionKey);

        encryptedSecret = this.entityManager.persist(encryptedSecret);

        DbEncryptedSecret encryptedSecret2 = new DbEncryptedSecret();
        encryptedSecret2.setDeviceIdentification(DEVICE_IDENTIFICATION);
        encryptedSecret2.setSecretType(org.opensmartgridplatform.secretmgmt.application.domain.SecretType.E_METER_ENCRYPTION_KEY_UNICAST);
        encryptedSecret2.setEncodedSecret("754dd7be970a47492b7b6007dc08f1dbd2a5e8b72af7738a680984e1b5a1215c");
        encryptedSecret2.setEncryptionKeyReference(encryptionKey);

        encryptedSecret2 = this.entityManager.persist(encryptedSecret2);

        this.entityManager.flush();
    }
}
