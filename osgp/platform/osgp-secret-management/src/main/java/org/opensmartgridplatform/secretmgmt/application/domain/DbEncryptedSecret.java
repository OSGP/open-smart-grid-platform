package org.opensmartgridplatform.secretmgmt.application.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import lombok.Getter;
import lombok.Setter;

/**
 * Encrypted secret.
 * This an immutable object. Historic entries are saved in the DB.
 */
@Entity(name="EncryptedSecret")
@Getter
@Setter
public class DbEncryptedSecret {
    Long id;
    Date creationTime;
    String deviceIdentification;
    @Enumerated(EnumType.STRING)
    SecretType secretType;
    String encodedSecret;
    DbEncryptionKeyReference encryptionKeyReference;
}
