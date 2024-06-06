// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.secretmanagement.application.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

/**
 * Encrypted secret, which should not be mutated, because modifications should result in a new
 * version (record in the DB). Historic entries remain in the DB table and the current secret should
 * be determined via a query on creationTime.
 */
@Entity
@Table(name = "encrypted_secret")
@Getter
@Setter
public class DbEncryptedSecret {
  @Id
  @SequenceGenerator(
      name = "encrypted_secret_seq_gen",
      sequenceName = "encrypted_secret_id_seq",
      allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "encrypted_secret_seq_gen")
  private Long id;

  private Instant creationTime;
  private String deviceIdentification;

  @Enumerated(EnumType.STRING)
  private SecretType secretType;

  @Enumerated(EnumType.STRING)
  private SecretStatus secretStatus;

  private String encodedSecret;
  @ManyToOne private DbEncryptionKeyReference encryptionKeyReference;
}
