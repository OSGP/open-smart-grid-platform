package org.opensmartgridplatform.secretmgmt.application.domain;

import java.util.Date;

public class TypedSecret {

    SecretType  secretType;
    String      secret;
    Date        validFrom;
    Date        validTo;
    int         kekReference;
}


