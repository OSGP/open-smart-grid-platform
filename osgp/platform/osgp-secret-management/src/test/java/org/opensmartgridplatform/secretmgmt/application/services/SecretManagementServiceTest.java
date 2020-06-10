package org.opensmartgridplatform.secretmgmt.application.services;

import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.secretmgmt.application.domain.SecretType;
import org.opensmartgridplatform.secretmgmt.application.repository.DbEncryptedSecretRepository;
import org.opensmartgridplatform.secretmgmt.application.repository.DbEncryptionKeyRepository;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.EncryptionDelegate;
import org.springframework.data.domain.Page;

@ExtendWith(MockitoExtension.class)
public class SecretManagementServiceTest {
    @InjectMocks
    SecretManagementService service;
    @Mock
    EncryptionDelegate encryptionDelegate;
    @Mock
    DbEncryptedSecretRepository secretRepository;
    @Mock
    DbEncryptionKeyRepository keyRepository;

    @Test
    public void retrieveSecrets_noSecrets() throws Exception {
        when(this.secretRepository.findValidOrderedByKeyValidFrom(anyString(), any(), any(), any(), any())).thenReturn(
                Page.empty());
        assertThatIllegalStateException().isThrownBy(() -> this.service.retrieveSecrets("SOME_DEVICE",
                Arrays.asList(SecretType.E_METER_MASTER_KEY)));
    }
}
