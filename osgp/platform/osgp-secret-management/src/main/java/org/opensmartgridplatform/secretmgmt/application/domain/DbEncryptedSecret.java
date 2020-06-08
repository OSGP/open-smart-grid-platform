package org.opensmartgridplatform.secretmgmt.application.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * Encrypted secret.
 * This is an immutable object. Historic entries are saved in the DB.
 */
@Entity
@Table(name = "EncryptedSecret")
@Getter
@Setter
public class DbEncryptedSecret {
    @Id
    @SequenceGenerator(name = "encrypted_secret_seq_gen", sequenceName = "encrypted_secret_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "encrypted_secret_seq_gen")
    Long id;
    Date creationTime;
    String deviceIdentification;
    @Enumerated(EnumType.STRING)
    SecretType secretType;
    String encodedSecret;
    @ManyToOne
    DbEncryptionKeyReference encryptionKeyReference;
}
