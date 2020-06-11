package org.opensmartgridplatform.secretmgmt.application;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.opensmartgridplatform.secretmgmt.application.domain.DbEncryptedSecret;
import org.opensmartgridplatform.secretmgmt.application.domain.DbEncryptionKeyReference;
import org.opensmartgridplatform.secretmgmt.application.repository.DbEncryptedSecretRepository;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.providers.EncryptionProviderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.test.server.MockWebServiceClient;
import org.springframework.ws.test.server.ResponseMatchers;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.ws.test.server.RequestCreators.withPayload;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SoapServiceSecretManagementIT {

    private static final String E_METER_AUTHENTICATION_KEY_ENCRYPTED_FOR_SOAP = "74efc062231e81c9e006bb56c5dec38631210c5073511606a203ba748fcdc794";
    private static final String E_METER_AUTHENTICATION_KEY_ENCRYPTED_FOR_DB   = "35c6d2af323bd3c4a588692dfcf4235fd20c2bd39bcf8672b6e65d515940150f";

    private static final String E_METER_ENCRYPTION_KEY_UNICAST_ENCRYPTED_FOR_SOAP = "3dca51832c70e372460796ca01acbab769fd330c9b936246a01d4e97f8c5bc26";
    private static final String E_METER_ENCRYPTION_KEY_UNICAST_ENCRYPTED_FOR_DB   = "7c737a402bdef7a0819f47ae9b625e2d8531e6c5d7603c4e4982c45175c4e063";

    private static final String DEVICE_IDENTIFICATION="E0054002019112319";

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DbEncryptedSecretRepository secretRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private MockWebServiceClient mockWebServiceClient;

    @BeforeAll
    public void setupClient() {
        this.mockWebServiceClient = MockWebServiceClient.createClient(this.applicationContext);
    }

    @BeforeEach
    public void beforeEachCreateTestData() {
        createTestData();
    }

    @Test
    public void testGetSecretsRequest() {

        assertThat(this.secretRepository.count()).isEqualTo(2);

        final Resource request = new ClassPathResource("test-requests/getSecrets.xml");
        final Resource expectedResponse = new ClassPathResource("test-responses/getSecrets.xml");
        try {
            this.mockWebServiceClient.sendRequest(withPayload(request))
                    .andExpect(ResponseMatchers.noFault())
                    .andExpect(ResponseMatchers.payload(expectedResponse));
        } catch(final Exception exc) {
            Assertions.fail("Error", exc);
        }
    }

    /**
     * Create test data for encrypted secrets and related encryptionkey reference(s).
     * So that the EncryptionService can encrypt and decrypt, using the JRE encryption provider.
     *
     * Two secrets (for two types of meter key secrets) and one reference key (valid as of now-1minute) is created.
     */
    private void createTestData() {
        DbEncryptionKeyReference encryptionKey = new DbEncryptionKeyReference();
        encryptionKey.setCreationTime(new Date());
        encryptionKey.setReference("1");
        encryptionKey.setEncryptionProviderType(EncryptionProviderType.JRE);
        encryptionKey.setValidFrom(new Date(System.currentTimeMillis()-60000));
        encryptionKey.setVersion(1L);
        this.testEntityManager.persist(encryptionKey);

        DbEncryptedSecret encryptedSecret = new DbEncryptedSecret();
        encryptedSecret.setDeviceIdentification(DEVICE_IDENTIFICATION);
        encryptedSecret.setSecretType(org.opensmartgridplatform.secretmgmt.application.domain.SecretType.E_METER_AUTHENTICATION_KEY);
        encryptedSecret.setEncodedSecret(E_METER_AUTHENTICATION_KEY_ENCRYPTED_FOR_DB);
        encryptedSecret.setEncryptionKeyReference(encryptionKey);

        this.testEntityManager.persist(encryptedSecret);

        DbEncryptedSecret encryptedSecret2 = new DbEncryptedSecret();
        encryptedSecret2.setDeviceIdentification(DEVICE_IDENTIFICATION);
        encryptedSecret2.setSecretType(org.opensmartgridplatform.secretmgmt.application.domain.SecretType.E_METER_ENCRYPTION_KEY_UNICAST);
        encryptedSecret2.setEncodedSecret(E_METER_ENCRYPTION_KEY_UNICAST_ENCRYPTED_FOR_DB);
        encryptedSecret2.setEncryptionKeyReference(encryptionKey);

        this.testEntityManager.persist(encryptedSecret2);

        this.testEntityManager.flush();
    }
}
