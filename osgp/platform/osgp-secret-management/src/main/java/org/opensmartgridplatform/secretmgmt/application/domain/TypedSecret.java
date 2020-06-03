package org.opensmartgridplatform.secretmgmt.application.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * TypedSecret stores HexEncoded secrets
 */
@Getter
@Setter
public class TypedSecret {
    SecretType  secretType;
    String      secret;
}


