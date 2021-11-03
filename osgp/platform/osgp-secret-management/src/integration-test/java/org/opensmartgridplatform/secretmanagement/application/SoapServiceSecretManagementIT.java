/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.secretmanagement.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.ws.test.server.RequestCreators.withSoapEnvelope;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.persistence.EntityManager;
import javax.xml.namespace.QName;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.secretmanagement.application.domain.DbEncryptedSecret;
import org.opensmartgridplatform.secretmanagement.application.domain.DbEncryptionKeyReference;
import org.opensmartgridplatform.secretmanagement.application.domain.SecretStatus;
import org.opensmartgridplatform.secretmanagement.application.domain.SecretType;
import org.opensmartgridplatform.secretmanagement.application.repository.DbEncryptedSecretRepository;
import org.opensmartgridplatform.shared.security.EncryptionProviderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.test.server.MockWebServiceClient;
import org.springframework.ws.test.server.ResponseMatchers;

@SpringBootTest
@EnableAutoConfiguration(exclude = FlywayAutoConfiguration.class)
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@Transactional
@TestPropertySource(properties = {"max.minutes.for.new.key.to.be.activated=5"})
public class SoapServiceSecretManagementIT {

  /**
   * The AES keys must be configured with the following values: db key:
   * hex:1cb340f6edab9d9b3f2912877c9ed161 soap key: hex:8ff36ab298aa8c240d1bb1185a138fe1
   *
   * <p>The plaintext secrets for meter 'E0000000000000000' are:
   *
   * <p>hex: 4b2a8fe42b9a211f557d4f1cc2f03f11 (E_METER_AUTHENTICATION, not a real key) hex:
   * 7f3a2c8b9d65f32aaa74fbee752b8c5a (E_METER_ENCRYPTION_KEY_UNICAST, not a real key)
   *
   * <p>The db-encrypted secrets are:
   * hex:e4e6fe6af967f5ca2b523f5917425a802a488c9d73fa3ae0a8d3151e4a6a1a44 (E_METER_AUTHENTICATION)
   * hex:f1f3113322acc27bc5454fcf7765a5996930fccef67d7d6fdf90f882c7b98a1d
   * (E_METER_ENCRYPTION_KEY_UNICAST)
   *
   * <p>The soap-encrypted secrets are:
   * hex:863d92d1176312adab58714361f00e998c5c0bd6bdf5a406611a44e5323e251f (E_METER_AUTHENTICATION)
   * hex:006a607aaa8ad3b37a6e5a41d93b06434d30032dd42b9412ff93e51980f66328
   * (E_METER_ENCRYPTION_KEY_UNICAST)
   */
  private static final String E_METER_AUTHENTICATION_KEY_ENCRYPTED_FOR_DB =
      "e4e6fe6af967f5ca2b523f5917425a802a488c9d73fa3ae0a8d3151e4a6a1a44";

  private static final String E_METER_ENCRYPTION_KEY_UNICAST_ENCRYPTED_FOR_DB =
      "f1f3113322acc27bc5454fcf7765a5996930fccef67d7d6fdf90f882c7b98a1d";

  private static final String DEVICE_IDENTIFICATION = "E0000000000000000";

  @Autowired private ApplicationContext applicationContext;

  @Autowired private DbEncryptedSecretRepository secretRepository;

  @Autowired private EntityManager testEntityManager;

  private MockWebServiceClient mockWebServiceClient;

  @BeforeEach
  public void setupTest() {
    this.mockWebServiceClient = MockWebServiceClient.createClient(this.applicationContext);
    this.createTestData();
  }

  @Test
  public void getSecretsRequest() throws IOException {

    /**
     * Note that the output depends, besides the value of the keys, also on both the db key and the
     * soap key.
     */
    assertThat(this.secretRepository.count()).isEqualTo(2);
    final Resource request = new ClassPathResource("test-requests/getSecrets.xml");
    this.mockWebServiceClient
        .sendRequest(withSoapEnvelope(request))
        .andExpect(
            (request2, response) -> {
              final OutputStream outStream = new ByteArrayOutputStream();
              response.writeTo(outStream);
              final String outputString = outStream.toString();
              assertThat(outputString.contains("<ns2:Result>OK</ns2:Result>")).isTrue();
              assertThat(outputString.contains("E_METER_AUTHENTICATION")).isTrue();
              assertThat(outputString.contains("E_METER_ENCRYPTION_KEY_UNICAST")).isTrue();
            });
  }

  @Test
  public void getSecretsWithCorrelationUidInHeader() throws IOException {
    final Resource request = new ClassPathResource("test-requests/getSecretsEnvelope.xml");
    this.mockWebServiceClient
        .sendRequest(withSoapEnvelope(request))
        .andExpect(
            (request2, response) -> {
              final Iterator<SoapHeaderElement> headerElements =
                  ((SoapMessage) response)
                      .getSoapHeader()
                      .examineHeaderElements(
                          new QName(
                              "http://www.opensmartgridplatform.org/schemas/security/secretmanagement",
                              "correlationUid"));
              assertThat(headerElements).hasNext();
              final SoapHeaderElement header = headerElements.next();
              assertThat(header.getText()).isEqualTo("123456");
              assertThat(headerElements).isExhausted();
            });
  }

  @Test
  public void getSecretsRequestNoStoredSecretType() {

    /**
     * Note that the output depends, besides the value of the keys, also on both the db key and the
     * soap key.
     */
    assertThat(this.secretRepository.count()).isEqualTo(2);
    final Resource request =
        new ClassPathResource("test-requests/getSecrets_noStoredSecretType.xml");
    try {
      this.mockWebServiceClient
          .sendRequest(withSoapEnvelope(request))
          .andExpect(
              (request2, response) -> {
                final OutputStream outStream = new ByteArrayOutputStream();
                response.writeTo(outStream);
                final String outputString = outStream.toString();
                assertThat(outputString.contains("<ns2:Result>OK</ns2:Result>")).isTrue();
                assertThat(outputString.contains("E_METER_AUTHENTICATION")).isTrue();
                assertThat(outputString.contains("E_METER_MASTER")).isTrue();
                assertThat(outputString.contains("E_METER_ENCRYPTION_KEY_UNICAST")).isTrue();
              });
    } catch (final Exception exc) {
      Assertions.fail("Error", exc);
    }
  }

  @Test
  public void storeSecretsRequest() {

    /**
     * Note that the output depends, besides the value of the keys, also on both the db key and the
     * soap key.
     */
    assertThat(this.secretRepository.count()).isEqualTo(2);

    final Resource request = new ClassPathResource("test-requests/storeSecrets.xml");
    final Resource expectedResponse = new ClassPathResource("test-responses/storeSecrets.xml");
    try {
      this.mockWebServiceClient
          .sendRequest(withSoapEnvelope(request))
          .andExpect(ResponseMatchers.noFault())
          .andExpect(ResponseMatchers.payload(expectedResponse));
    } catch (final Exception exc) {
      Assertions.fail("Error", exc);
    }

    // test the effects by looking in the repositories
    assertThat(this.secretRepository.count()).isEqualTo(4);
  }

  @Test
  public void storeSecretsRequestAlreadyNewSecretPresent() throws IOException {

    /**
     * Note that the output depends, besides the value of the keys, also on both the db key and the
     * soap key.
     */
    assertThat(this.secretRepository.count()).isEqualTo(2);

    final Resource storeRequest = new ClassPathResource("test-requests/storeSecrets.xml");
    final Resource expectedStoreResponse = new ClassPathResource("test-responses/storeSecrets.xml");
    // Store secrets
    this.mockWebServiceClient
        .sendRequest(withSoapEnvelope(storeRequest))
        .andExpect(ResponseMatchers.noFault())
        .andExpect(ResponseMatchers.payload(expectedStoreResponse));
    // Store secrets again, while previously stored secret still have status NEW
    final String errorMessage =
        "There is/are secrets of type E_METER_AUTHENTICATION_KEY for device E0000000000000000 with "
            + "status NEW created less than 5 minutes old. No key with status NEW will be stored. Wait"
            + " at least 5 minutes before starting a request requiring NEW keys to be stored.";
    this.mockWebServiceClient
        .sendRequest(withSoapEnvelope(storeRequest))
        .andExpect(ResponseMatchers.serverOrReceiverFault(errorMessage));
  }

  @Test
  public void activateSecretsRequest() throws IOException {

    /**
     * Note that the output depends, besides the value of the keys, also on both the db key and the
     * soap key.
     */
    assertThat(this.secretRepository.count()).isEqualTo(2);

    final Resource storeRequest = new ClassPathResource("test-requests/storeSecrets.xml");
    final Resource activateRequest = new ClassPathResource("test-requests/activateSecrets.xml");
    final Resource expectedStoreResponse = new ClassPathResource("test-responses/storeSecrets.xml");
    final Resource expectedActivateResponse =
        new ClassPathResource("test-responses/activateSecrets.xml");
    // Store secrets
    this.mockWebServiceClient
        .sendRequest(withSoapEnvelope(storeRequest))
        .andExpect(ResponseMatchers.noFault())
        .andExpect(ResponseMatchers.payload(expectedStoreResponse));
    this.mockWebServiceClient
        .sendRequest(withSoapEnvelope(activateRequest))
        .andExpect(ResponseMatchers.noFault())
        .andExpect(ResponseMatchers.payload(expectedActivateResponse));
  }

  @Test
  public void activateSecretsRequestNoNewSecret() throws IOException {

    /**
     * Note that the output depends, besides the value of the keys, also on both the db key and the
     * soap key.
     */
    assertThat(this.secretRepository.count()).isEqualTo(2);

    final Resource activateRequest = new ClassPathResource("test-requests/activateSecrets.xml");
    // Store secrets
    this.mockWebServiceClient
        .sendRequest(withSoapEnvelope(activateRequest))
        .andExpect(
            ResponseMatchers.serverOrReceiverFault(
                "Expected 1 new secrets of type E_METER_AUTHENTICATION_KEY for device E0000000000000000, but 0 new secret(s) present"));
  }

  @Test
  public void getSecretsRequestNoSecretTypes() {

    /**
     * Note that the output depends, besides the value of the keys, also on both the db key and the
     * soap key.
     */
    assertThat(this.secretRepository.count()).isEqualTo(2);

    final Resource request = new ClassPathResource("test-requests/invalidGetSecrets.xml");

    try {
      this.mockWebServiceClient
          .sendRequest(withSoapEnvelope(request))
          .andExpect(ResponseMatchers.serverOrReceiverFault("Missing input: secret types"));
    } catch (final Exception exc) {
      Assertions.fail("Error", exc);
    }
  }

  @Test
  public void storeSecretsRequestNoSecrets() {

    /**
     * Note that the output depends, besides the value of the keys, also on both the db key and the
     * soap key.
     */
    assertThat(this.secretRepository.count()).isEqualTo(2);

    final Resource request = new ClassPathResource("test-requests/invalidStoreSecrets.xml");

    try {
      this.mockWebServiceClient
          .sendRequest(withSoapEnvelope(request))
          .andExpect(ResponseMatchers.serverOrReceiverFault("Missing input: typed secrets"));
    } catch (final Exception exc) {
      Assertions.fail("Error", exc);
    }
  }

  @Test
  public void generateAndStoreSecrets() throws IOException {
    final Resource generateAndStoreRequest =
        new ClassPathResource("test-requests/generateAndStoreSecrets.xml");
    this.mockWebServiceClient
        .sendRequest(withSoapEnvelope(generateAndStoreRequest))
        .andExpect(ResponseMatchers.noFault())
        .andExpect(
            (request, response) -> {
              final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
              response.writeTo(outputStream);
              assertThat(outputStream.toString()).contains("Result>OK");
            });
    final List<DbEncryptedSecret> authKeys =
        this.secretRepository.findSecrets(
            DEVICE_IDENTIFICATION, SecretType.E_METER_AUTHENTICATION_KEY, SecretStatus.NEW);
    assertThat(authKeys).hasSize(1);
    final DbEncryptedSecret authKey = authKeys.get(0);
    assertThat(authKey.getEncodedSecret()).hasSize(64);
  }

  @Test
  public void generateAndStoreSecretsAlreadyNewSecretPresent() throws IOException {
    // Store secrets
    final Resource storeRequest = new ClassPathResource("test-requests/storeSecrets.xml");
    this.mockWebServiceClient
        .sendRequest(withSoapEnvelope(storeRequest))
        .andExpect(ResponseMatchers.noFault());
    // Generate and store secret: this should result in a fault message
    final Resource generateAndStoreRequest =
        new ClassPathResource("test-requests/generateAndStoreSecrets.xml");
    this.mockWebServiceClient
        .sendRequest(withSoapEnvelope(generateAndStoreRequest))
        .andExpect(ResponseMatchers.serverOrReceiverFault());
  }

  /**
   * Create test data for encrypted secrets and related encryptionkey reference(s). So that the
   * EncryptionService can encrypt and decrypt, using the JRE encryption provider.
   *
   * <p>Two secrets (for two types of meter key secrets) and one reference key (valid as of
   * now-1minute) is created.
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
        org.opensmartgridplatform.secretmanagement.application.domain.SecretType
            .E_METER_AUTHENTICATION_KEY);
    encryptedSecret.setEncodedSecret(E_METER_AUTHENTICATION_KEY_ENCRYPTED_FOR_DB);
    encryptedSecret.setSecretStatus(SecretStatus.ACTIVE);
    encryptedSecret.setEncryptionKeyReference(encryptionKey);

    this.testEntityManager.persist(encryptedSecret);

    final DbEncryptedSecret encryptedSecret2 = new DbEncryptedSecret();
    encryptedSecret2.setCreationTime(new Date());
    encryptedSecret2.setDeviceIdentification(DEVICE_IDENTIFICATION);
    encryptedSecret2.setSecretType(
        org.opensmartgridplatform.secretmanagement.application.domain.SecretType
            .E_METER_ENCRYPTION_KEY_UNICAST);
    encryptedSecret2.setEncodedSecret(E_METER_ENCRYPTION_KEY_UNICAST_ENCRYPTED_FOR_DB);
    encryptedSecret2.setSecretStatus(SecretStatus.ACTIVE);
    encryptedSecret2.setEncryptionKeyReference(encryptionKey);

    this.testEntityManager.persist(encryptedSecret2);

    this.testEntityManager.flush();
  }
}
