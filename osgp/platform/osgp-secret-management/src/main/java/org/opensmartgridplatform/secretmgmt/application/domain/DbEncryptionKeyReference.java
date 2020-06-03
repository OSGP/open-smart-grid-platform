package org.opensmartgridplatform.secretmgmt.application.domain;

import java.util.Date;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import lombok.Getter;
import lombok.Setter;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.providers.EncryptionProviderType;

@Getter
@Setter
public class DbEncryptionKeyReference {
    Long id;
    Date creationTime;
    Date modificationTime;
    Long version;
    @Enumerated(EnumType.STRING)
    EncryptionProviderType encryptionProviderType;
    Long encryptionKeyVersion;
    Date valid_from;
    Date valid_to;
}
