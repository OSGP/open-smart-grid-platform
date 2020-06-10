package org.opensmartgridplatform.secretmgmt.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.ws.test.server.RequestCreators.withPayload;

import java.util.Date;

import org.apache.tomcat.util.buf.HexUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.secretmgmt.application.domain.DbEncryptedSecret;
import org.opensmartgridplatform.secretmgmt.application.domain.DbEncryptionKeyReference;
import org.opensmartgridplatform.secretmgmt.application.domain.SecretType;
import org.opensmartgridplatform.secretmgmt.application.repository.DbEncryptedSecretRepository;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.providers.EncryptionProviderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.test.server.MockWebServiceClient;
import org.springframework.ws.test.server.ResponseMatchers;
import org.springframework.xml.transform.StringSource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Transactional
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
public class MyIT {
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DbEncryptedSecretRepository secretRepository;

    @Autowired
    TestEntityManager testEntityManager;

    @Test
    public void testStuff() {
        DbEncryptionKeyReference keyReference = new DbEncryptionKeyReference();
        keyReference.setReference("1");
        keyReference.setEncryptionProviderType(EncryptionProviderType.JRE);
        keyReference.setVersion(1L);
        keyReference.setValidFrom(new Date(System.currentTimeMillis()-60000));
        keyReference = this.testEntityManager.persist(keyReference);
        DbEncryptedSecret secret = new DbEncryptedSecret();
        secret.setDeviceIdentification("E0054002019112319");
        secret.setSecretType(SecretType.E_METER_AUTHENTICATION_KEY);
        secret.setEncryptionKeyReference(keyReference);
        secret.setEncodedSecret(HexUtils.toHexString("janDik4President".getBytes()));
        secret = this.testEntityManager.persist(secret);
        this.testEntityManager.flush();
        assertThat(this.secretRepository.count()).isEqualTo(1);
        final String namespace = "http://www.opensmartgridplatform.org/schemas/security/secretmanagement/2020/05";
        final String soapRequest = "<ns:getSecretsRequest xmlns:ns=\""+namespace+"\">\n"
                + "         <ns:DeviceId>E0054002019112319</ns:DeviceId>\n"
                + "         <ns:SecretTypes>\n"
                + "            <ns:SecretType>E_METER_AUTHENTICATION_KEY</ns:SecretType>\n"
                + "         </ns:SecretTypes>\n"
                + "      </ns:getSecretsRequest>";
        try {
            final StringSource requestSource = new StringSource(soapRequest);
            final MockWebServiceClient mockWebServiceClient = MockWebServiceClient.createClient(this.applicationContext);
            mockWebServiceClient.sendRequest(withPayload(requestSource)).andExpect(ResponseMatchers.noFault());
            //System.out.println(String.format("Response = '%s'",response));
        } catch(final Exception exc) {
            Assertions.fail("Error", exc);
        }
    }
}
