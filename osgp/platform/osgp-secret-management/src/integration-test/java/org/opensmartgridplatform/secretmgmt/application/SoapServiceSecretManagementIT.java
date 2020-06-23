package org.opensmartgridplatform.secretmgmt.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.ws.test.server.RequestCreators.withPayload;

import java.io.IOException;
import java.util.Date;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.secretmgmt.application.domain.DbEncryptedSecret;
import org.opensmartgridplatform.secretmgmt.application.domain.DbEncryptionKeyReference;
import org.opensmartgridplatform.secretmgmt.application.repository.DbEncryptedSecretRepository;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.providers.EncryptionProviderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.test.server.MockWebServiceClient;
import org.springframework.ws.test.server.ResponseMatchers;

@SpringBootTest
@Transactional
@EnableAutoConfiguration(exclude = FlywayAutoConfiguration.class)
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
public class SoapServiceSecretManagementIT {

    /**
     * The AES keys must be configured with the following values:
     * db key: hex:1cb340f6edab9d9b3f2912877c9ed161
     * soap key: hex:8ff36ab298aa8c240d1bb1185a138fe1
     *
     * The plantext secrets for meter 'E0054002019112319' are:
     *
     * hex: 72b8fc276644a60ccefdf219fbee1a49 (E_METER_AUTHENTICATION)
     * hex: a3d5883fe56cf12b1a7cb5a686da6064 (E_METER_ENCRYPTION_KEY_UNICAST)
     *
     * The db-encrypted secrets are: hex:35c6d2af323bd3c4a588692dfcf4235fd20c2bd39bcf8672b6e65d515940150f
     * (E_METER_AUTHENTICATION)
     * hex:7c737a402bdef7a0819f47ae9b625e2d8531e6c5d7603c4e4982c45175c4e063 (E_METER_ENCRYPTION_KEY_UNICAST)
     *
     * The soap-encrypted secrets are: hex:74efc062231e81c9e006bb56c5dec38631210c5073511606a203ba748fcdc794
     * (E_METER_AUTHENTICATION)
     * hex:3dca51832c70e372460796ca01acbab769fd330c9b936246a01d4e97f8c5bc26 (E_METER_ENCRYPTION_KEY_UNICAST)
     */

    private static final String E_METER_AUTHENTICATION_KEY_ENCRYPTED_FOR_DB =
            "35c6d2af323bd3c4a588692dfcf4235fd20c2bd39bcf8672b6e65d515940150f";
    private static final String E_METER_ENCRYPTION_KEY_UNICAST_ENCRYPTED_FOR_DB =
            "7c737a402bdef7a0819f47ae9b625e2d8531e6c5d7603c4e4982c45175c4e063";

    private static final String DEVICE_IDENTIFICATION = "E0054002019112319";

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DbEncryptedSecretRepository secretRepository;

    @Autowired
    private EntityManager testEntityManager;

    private MockWebServiceClient mockWebServiceClient;

    @BeforeEach
    public void setupTest() {
        this.mockWebServiceClient = MockWebServiceClient.createClient(this.applicationContext);
        this.createTestData();
    }

    @Test
    public void getSecretsRequest() {

        /**
         * Note that the output depends, besides the value of the keys, also on both the db key and the soap key.
         */
        assertThat(this.secretRepository.count()).isEqualTo(2);
        final Resource request = new ClassPathResource("test-requests/getSecrets.xml");
        final Resource expectedResponse = new ClassPathResource("test-responses/getSecrets.xml");
        try {
            this.mockWebServiceClient.sendRequest(withPayload(request))
            .andExpect(
                    ResponseMatchers.payload(expectedResponse));
        } catch (final Exception exc) {
            Assertions.fail("Error", exc);
        }
    }

    @Test
    public void storeSecretsRequest() {

        /**
         * Note that the output depends, besides the value of the keys, also on both the db key and the soap key.
         */
        assertThat(this.secretRepository.count()).isEqualTo(2);

        final Resource request = new ClassPathResource("test-requests/storeSecrets.xml");
        final Resource expectedResponse = new ClassPathResource("test-responses/storeSecrets.xml");
        try {
            this.mockWebServiceClient.sendRequest(withPayload(request)).andExpect(ResponseMatchers.noFault()).andExpect(
                    ResponseMatchers.payload(expectedResponse));
        } catch (final Exception exc) {
            Assertions.fail("Error", exc);
        }

        //test the effects by looking in the repositories
        assertThat(this.secretRepository.count()).isEqualTo(4);
    }

    @Test
    public void getSecretsRequest_noSecretTypes() {

        /**
         * Note that the output depends, besides the value of the keys, also on both the db key and the soap key.
         */
        assertThat(this.secretRepository.count()).isEqualTo(2);

        final Resource request = new ClassPathResource("test-requests/invalidGetSecrets.xml");

        try {
            this.mockWebServiceClient.sendRequest(withPayload(request)).andExpect(
                    ResponseMatchers.serverOrReceiverFault("Missing input: secret types"));
        } catch (final Exception exc) {
            Assertions.fail("Error", exc);
        }
    }

    @Test
    public void setSecretsRequest_noSecrets() {

        /**
         * Note that the output depends, besides the value of the keys, also on both the db key and the soap key.
         */
        assertThat(this.secretRepository.count()).isEqualTo(2);

        final Resource request = new ClassPathResource("test-requests/invalidStoreSecrets.xml");

        try {
            this.mockWebServiceClient.sendRequest(withPayload(request)).andExpect(
                    ResponseMatchers.serverOrReceiverFault("Missing input: typed secrets"));
        } catch (final Exception exc) {
            Assertions.fail("Error", exc);
        }
    }

    @Test
    public void setSecretsRequest_identicalSecrets() throws IOException {

        /**
         * Note that the output depends, besides the value of the keys, also on both the db key and the soap key.
         */
        assertThat(this.secretRepository.count()).isEqualTo(2);

        final Resource request = new ClassPathResource("test-requests/storeSecrets.xml");
        final Resource expectedResponse = new ClassPathResource("test-responses/storeSecrets.xml");
        //Store secrets
        this.mockWebServiceClient.sendRequest(withPayload(request))
                .andExpect(ResponseMatchers.noFault())
                .andExpect(ResponseMatchers.payload(expectedResponse));
        //Store identical secrets again
        final String errorMessage = "Secret is identical to current secret (E0054002019112319, E_METER_AUTHENTICATION_KEY)";
        this.mockWebServiceClient.sendRequest(withPayload(request)).andExpect(
                    ResponseMatchers.serverOrReceiverFault(errorMessage));
    }

    /**
     * Create test data for encrypted secrets and related encryptionkey reference(s).
     * So that the EncryptionService can encrypt and decrypt, using the JRE encryption provider.
     *
     * Two secrets (for two types of meter key secrets) and one reference key (valid as of now-1minute) is created.
     */
    private void createTestData() {
        final DbEncryptionKeyReference encryptionKey = new DbEncryptionKeyReference();
        encryptionKey.setCreationTime(new Date());
        encryptionKey.setReference("1");
        encryptionKey.setEncryptionProviderType(EncryptionProviderType.JRE);
        encryptionKey.setValidFrom(new Date(System.currentTimeMillis() - 60000));
        encryptionKey.setVersion(1L);
        this.testEntityManager.persist(encryptionKey);
        final DbEncryptedSecret encryptedSecret = new DbEncryptedSecret();
        encryptedSecret.setCreationTime(new Date());
        encryptedSecret.setDeviceIdentification(DEVICE_IDENTIFICATION);
        encryptedSecret.setSecretType(
                org.opensmartgridplatform.secretmgmt.application.domain.SecretType.E_METER_AUTHENTICATION_KEY);
        encryptedSecret.setEncodedSecret(E_METER_AUTHENTICATION_KEY_ENCRYPTED_FOR_DB);
        encryptedSecret.setEncryptionKeyReference(encryptionKey);

        this.testEntityManager.persist(encryptedSecret);

        final DbEncryptedSecret encryptedSecret2 = new DbEncryptedSecret();
        encryptedSecret2.setCreationTime(new Date());
        encryptedSecret2.setDeviceIdentification(DEVICE_IDENTIFICATION);
        encryptedSecret2.setSecretType(
                org.opensmartgridplatform.secretmgmt.application.domain.SecretType.E_METER_ENCRYPTION_KEY_UNICAST);
        encryptedSecret2.setEncodedSecret(E_METER_ENCRYPTION_KEY_UNICAST_ENCRYPTED_FOR_DB);
        encryptedSecret2.setEncryptionKeyReference(encryptionKey);

        this.testEntityManager.persist(encryptedSecret2);

        this.testEntityManager.flush();
    }
}
