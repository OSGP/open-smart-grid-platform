/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig;

public class DlmsExtendedRegister extends DlmsRegister {

    public DlmsExtendedRegister(final DlmsObjectType type, final String obisCode, final int scaler,
            final RegisterUnit unit, final Medium medium) {
        super(type, 4, obisCode, scaler, unit, medium);
    }
}
