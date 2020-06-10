package org.opensmartgridplatform.secretmgmt.application;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.secretmgmt.application.domain.DbEncryptionKeyReference;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.providers.EncryptionProviderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Transactional
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
public class MyIT {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    TestEntityManager testEntityManager;

    @Test
    public void testStuff() {
        //final String NAMESPACE_URI = "http://www.opensmartgridplatform.org/schemas/security/secretmanagement/2020/05";
        final DbEncryptionKeyReference keyReference = new DbEncryptionKeyReference();
        keyReference.setReference("ref");
        keyReference.setEncryptionProviderType(EncryptionProviderType.JRE);
        keyReference.setVersion(1L);
        this.testEntityManager.persist(keyReference);
        final String soapRequest = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns=\"http://www.opensmartgridplatform.org/schemas/security/secretmanagement/2020/05\">\n"
                + "   <soapenv:Header/>\n" + "   <soapenv:Body>\n" + "      <ns:getSecretsRequest>\n"
                + "         <ns:DeviceId>E0054002019112319</ns:DeviceId>\n" + "         <ns:SecretTypes>\n"
                + "            <ns:SecretType>E_METER_AUTHENTICATION_KEY</ns:SecretType>\n"
                + "         </ns:SecretTypes>\n" + "      </ns:getSecretsRequest>\n" + "   </soapenv:Body>\n"
                + "</soapenv:Envelope>";
        try {
            final MvcResult result = this.mockMvc.perform(post("http://localhost:8080/ws/SecretManagement")
                    .contentType(MediaType.APPLICATION_XML)
                    .content(soapRequest)
                    .accept(MediaType.APPLICATION_XML))
                    .andExpect(status().isOk())
                    .andReturn();
            final String response = result.getResponse().getContentAsString();
            System.out.println(String.format("Response = '%s'",response));
        } catch(final Exception exc) {
            Assertions.fail("Error", exc);
        }
    }
}
