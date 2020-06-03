package org.opensmartgridplatform.secretmgmt.application.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import lombok.Getter;
import lombok.Setter;

@Entity(name="EncryptedSecret")
@Getter
@Setter
public class DbEncryptedSecret {
    Long id;
    Date creationTime;
    Date modificationTime;
    Long lockingVersion; //version used for optimistic locking
    String deviceIdentification;
    @Enumerated(EnumType.STRING)
    SecretType secretType;
    String encodedSecret;
    DbEncryptionKeyReference encryptionKeyReference;
}
