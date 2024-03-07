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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.opensmartgridplatform.shared.security.EncryptionProviderType;

@Entity
@Table(name = "encryption_key_reference")
@Getter
@Setter
public class DbEncryptionKeyReference {
  @Id
  @SequenceGenerator(
      name = "encryption_key_seq_gen",
      sequenceName = "encryption_key_reference_id_seq",
      allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "encryption_key_seq_gen")
  private Long id;

  private Instant creationTime;
  private Instant modificationTime;
  private Long version; // for optimistic locking

  @Enumerated(EnumType.STRING)
  private EncryptionProviderType encryptionProviderType;

  private String reference;
  private Instant validFrom;
  private Instant validTo;
  private String modifiedBy;
}
