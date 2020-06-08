package org.opensmartgridplatform.secretmgmt.application.endpoints;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.GetSecretsRequest;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.GetSecretsResponse;
import org.opensmartgridplatform.secretmgmt.application.services.SecretManagementService;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.providers.EncryptionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureDataJpa
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SecretManagementEndpointIT {

    @Autowired
    private SecretManagementEndpoint secretManagementEndpoint;

    @Autowired
    private SecretManagementService secretManagementService;

    @Autowired
    private EncryptionProvider soapSecretEncryptionProvider;

    //@BeforeAll

    @BeforeEach
    public void beforeEachTest() {
        secretManagementEndpoint = new SecretManagementEndpoint(secretManagementService, soapSecretEncryptionProvider);
    }

    @Test
    public void getSecretsBySoapCall() throws Exception {
        GetSecretsRequest request = new GetSecretsRequest();
        //GetSecretsResponse response = secretManagementEndpoint.getSecretsRequest(request);
    }
}
