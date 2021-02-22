package org.opensmartgridplatform.adapter.protocol.dlms.domain.factories;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;

public interface SecurityKeyProvider {
    byte[] getKey(String deviceIdentification, SecurityKeyType type);
}
