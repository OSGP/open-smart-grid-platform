// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.builders.entities;

import java.time.Instant;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.opensmartgridplatform.cucumber.platform.core.builders.Builder;
import org.opensmartgridplatform.cucumber.platform.smartmetering.SecurityKey;
import org.opensmartgridplatform.secretmanagement.application.domain.DbEncryptedSecret;
import org.opensmartgridplatform.secretmanagement.application.domain.DbEncryptionKeyReference;
import org.opensmartgridplatform.secretmanagement.application.domain.SecretStatus;
import org.opensmartgridplatform.secretmanagement.application.domain.SecretType;

public class SecretBuilder implements Builder<DbEncryptedSecret> {

  private SecretType secretType;
  private String key = SecurityKey.SECURITY_KEY_A.getDatabaseKey();
  private SecretStatus status;
  private String deviceIdentification;
  private Instant creationTime;
  private DbEncryptionKeyReference encryptionKeyReference;

  public SecretBuilder withSecretType(final SecretType secretType) {
    this.secretType = secretType;
    return this;
  }

  public SecretType getSecretType() {
    return this.secretType;
  }

  public SecretBuilder withSecurityKeyType(final SecurityKeyType securityKeyType) {
    this.secretType = SecretType.valueOf(securityKeyType.toSecretType().value());
    return this;
  }

  public SecurityKeyType getSecurityKeyType() {
    return SecurityKeyType.fromSecretType(
        org.opensmartgridplatform.ws.schema.core.secret.management.SecretType.valueOf(
            this.secretType.name()));
  }

  public SecretBuilder withKey(final String key) {
    this.key = key;
    return this;
  }

  public SecretBuilder withDeviceIdentification(final String deviceIdentification) {
    this.deviceIdentification = deviceIdentification;
    return this;
  }

  public SecretBuilder withSecretStatus(final SecretStatus status) {
    this.status = status;
    return this;
  }

  public SecretBuilder withCreationTime(final Instant creationTime) {
    this.creationTime = creationTime;
    return this;
  }

  public SecretBuilder withEncryptionKeyReference(
      final DbEncryptionKeyReference encryptionKeyReference) {
    this.encryptionKeyReference = encryptionKeyReference;
    return this;
  }

  @Override
  public DbEncryptedSecret build() {
    final DbEncryptedSecret securityKey = new DbEncryptedSecret();
    securityKey.setDeviceIdentification(this.deviceIdentification);
    securityKey.setSecretType(this.secretType);
    securityKey.setEncodedSecret(this.key);
    securityKey.setSecretStatus(this.status == null ? SecretStatus.ACTIVE : this.status);
    securityKey.setCreationTime(this.creationTime == null ? Instant.now() : this.creationTime);
    securityKey.setEncryptionKeyReference(this.encryptionKeyReference);
    return securityKey;
  }
}
