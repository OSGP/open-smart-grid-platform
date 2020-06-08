package org.opensmartgridplatform.secretmgmt.application.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.providers.EncryptionProviderType;

@Entity
@Table(name = "EncryptionKeyReference")
@Getter
@Setter
public class DbEncryptionKeyReference {
    @Id
    @SequenceGenerator(name = "encryption_key_seq_gen", sequenceName = "encryption_key_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "encryption_key_seq_gen")
    Long id;
    Date creationTime;
    Date modificationTime;
    Long version; //for optimistic locking
    @Enumerated(EnumType.STRING)
    EncryptionProviderType encryptionProviderType;
    String reference;
    Date validFrom;
    Date validTo;
    String modifiedBy;
}
