package org.opensmartgridplatform.secretmgmt.application.endpoints;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.GetSecretsRequest;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.GetSecretsResponse;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.OsgpResultType;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.SecretType;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.SecretTypes;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.StoreSecretsRequest;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.StoreSecretsResponse;
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

    private static final String E_METER_AUTHENTICATION_KEY_ENCRYPTED_FOR_SOAP = "74efc062231e81c9e006bb56c5dec38631210c5073511606a203ba748fcdc794";
    private static final String E_METER_AUTHENTICATION_KEY_ENCRYPTED_FOR_DB   = "35c6d2af323bd3c4a588692dfcf4235fd20c2bd39bcf8672b6e65d515940150f";

    private static final String E_METER_ENCRYPTION_KEY_UNICAST_ENCRYPTED_FOR_SOAP = "3dca51832c70e372460796ca01acbab769fd330c9b936246a01d4e97f8c5bc26";
    private static final String E_METER_ENCRYPTION_KEY_UNICAST_ENCRYPTED_FOR_DB   = "7c737a402bdef7a0819f47ae9b625e2d8531e6c5d7603c4e4982c45175c4e063";

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

    /**
     * Tests the results of getting 2 known types of secrets for one specific device.
     * The 2 secret types are specified along with a device id.
     * The secrets will be read from the database (entities), decrypted and
     * re-encrypted for soap transmission.
     */
    @Test
    public void getSecretsBySoapCall() {

        GetSecretsRequest request = new GetSecretsRequest();

        //build request message structure
        SecretTypes secretTypesToGet = new SecretTypes();
        List<SecretType> secretTypeList = secretTypesToGet.getSecretType();
        request.setSecretTypes(secretTypesToGet);

        //add test details
        request.setDeviceId(DEVICE_IDENTIFICATION);

        //add key types to get
        secretTypeList.add(SecretType.E_METER_AUTHENTICATION_KEY);
        secretTypeList.add(SecretType.E_METER_ENCRYPTION_KEY_UNICAST);

        //call the service endpoint
        GetSecretsResponse response = secretManagementEndpoint.getSecretsRequest(request);

        //verify the results
        assertThat(response.getResult()).isEqualTo(OsgpResultType.OK);
        TypedSecrets typedSecrets = response.getTypedSecrets();

        List<TypedSecret> listOfTypedSecrets = typedSecrets.getTypedSecret();

        assertThat(listOfTypedSecrets.size()).isEqualTo(2);

        assertThat(listOfTypedSecrets.get(0).getSecret()).isEqualTo(E_METER_AUTHENTICATION_KEY_ENCRYPTED_FOR_SOAP);
        assertThat(listOfTypedSecrets.get(1).getSecret()).isEqualTo(E_METER_ENCRYPTION_KEY_UNICAST_ENCRYPTED_FOR_SOAP);

        assertThat(listOfTypedSecrets.get(0).getType()).isEqualTo(SecretType.E_METER_AUTHENTICATION_KEY);
        assertThat(listOfTypedSecrets.get(1).getType()).isEqualTo(SecretType.E_METER_ENCRYPTION_KEY_UNICAST);
    }

    /**
     * This test verifies the result of an invalid soap request.
     */
    @Test
    public void tryGetSecretsBySoapCallWithException() {
        GetSecretsRequest request = new GetSecretsRequest();
        GetSecretsResponse response = secretManagementEndpoint.getSecretsRequest(request);
        assertThat(response.getResult()).isEqualTo(OsgpResultType.NOT_OK);
    }

    /**
     * Tests the results of storing 2 types of secrets for one specific device.
     * The 2 secret types and secrets itself are specified together with a device id.
     * The secrets will decrypted (using soap key), then will be encrypted for
     * storage in the database (using db key).
     */
    @Test
    public void storeSecretsRequestBySoapCall() {

        StoreSecretsRequest request = new StoreSecretsRequest();

        request.setDeviceId(DEVICE_IDENTIFICATION);

        TypedSecrets typedSecrets = new TypedSecrets();
        List<TypedSecret> typedSecretList = typedSecrets.getTypedSecret();

        TypedSecret typedSecret1 = new TypedSecret();
        typedSecret1.setType(SecretType.E_METER_AUTHENTICATION_KEY);
        typedSecret1.setSecret(E_METER_AUTHENTICATION_KEY_ENCRYPTED_FOR_SOAP);

        TypedSecret typedSecret2 = new TypedSecret();
        typedSecret2.setType(SecretType.E_METER_ENCRYPTION_KEY_BROADCAST);
        typedSecret2.setSecret(E_METER_ENCRYPTION_KEY_UNICAST_ENCRYPTED_FOR_SOAP);

        typedSecretList.add(typedSecret1);
        typedSecretList.add(typedSecret2);
        
        request.setTypedSecrets(typedSecrets);
        
        StoreSecretsResponse response = secretManagementEndpoint.storeSecretsRequest(request);

        assertThat(response.getResult()).isEqualTo(OsgpResultType.OK);

    }

    /**
     * This test verifies the result of an invalid soap request.
     */
    @Test
    public void tryStoreSecretsBySoapCallWithException() {
        StoreSecretsRequest request = new StoreSecretsRequest();
        StoreSecretsResponse response = secretManagementEndpoint.storeSecretsRequest(request);
        assertThat(response.getResult()).isEqualTo(OsgpResultType.NOT_OK);
    }

    /**
     * Create test data for encrypted secrets and related encryptionkey reference(s).
     * So that the EncryptionService can encrypt and decrypt, using the JRE encryption provider.
     *
     * Two secrets (for two types of meter key secrets) and one reference key (valid as of now-1minute) is created.
     */
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
        encryptedSecret.setEncodedSecret(E_METER_AUTHENTICATION_KEY_ENCRYPTED_FOR_DB);
        encryptedSecret.setEncryptionKeyReference(encryptionKey);

        encryptedSecret = this.entityManager.persist(encryptedSecret);

        DbEncryptedSecret encryptedSecret2 = new DbEncryptedSecret();
        encryptedSecret2.setDeviceIdentification(DEVICE_IDENTIFICATION);
        encryptedSecret2.setSecretType(org.opensmartgridplatform.secretmgmt.application.domain.SecretType.E_METER_ENCRYPTION_KEY_UNICAST);
        encryptedSecret2.setEncodedSecret(E_METER_ENCRYPTION_KEY_UNICAST_ENCRYPTED_FOR_DB);
        encryptedSecret2.setEncryptionKeyReference(encryptionKey);

        encryptedSecret2 = this.entityManager.persist(encryptedSecret2);

        this.entityManager.flush();
    }
}
