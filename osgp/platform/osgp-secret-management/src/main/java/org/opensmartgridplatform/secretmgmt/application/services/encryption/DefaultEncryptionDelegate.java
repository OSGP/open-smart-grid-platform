package org.opensmartgridplatform.secretmgmt.application.services.encryption;

import org.opensmartgridplatform.secretmgmt.application.services.encryption.providers.JreEncryptionProvider;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.providers.EncryptionProvider;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.providers.EncryptionProviderType;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.providers.HsmEncryptionProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class is used to encrypt/decrypt typed secrets by either the HardwareSecurityModule or by the JRE
 */
public class DefaultEncryptionDelegate implements EncryptionDelegate {

    private final List<EncryptionProvider> providers = new ArrayList<>();

    public DefaultEncryptionDelegate(File jreKeystore) {
        JreEncryptionProvider jreEncryptionProvider = new JreEncryptionProvider();
        jreEncryptionProvider.setKeystore(jreKeystore);
        providers.add(new JreEncryptionProvider());

        providers.add(new HsmEncryptionProvider());
    }

    @Override
    public EncryptedSecret encrypt(EncryptionProviderType encType, Secret secret, String keyReference) throws Exception {
        Optional<EncryptionProvider> oep = providers.stream().filter(ep -> ep.getType().equals(encType)).findFirst();
        return oep.isPresent()?oep.get().encrypt(secret, keyReference):null;
    }

    @Override
    public Secret decrypt(EncryptedSecret secret, String keyReference) throws Exception {
        EncryptionProviderType encType = secret.getType();
        Optional<EncryptionProvider> oep = providers.stream().filter(ep -> ep.getType().equals(encType)).findFirst();
        return oep.isPresent()?oep.get().decrypt(secret, keyReference):null;
    }
}

