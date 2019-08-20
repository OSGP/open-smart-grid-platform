/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;

/**
 * Contains the current @{@link DlmsDevice} to be used by one single @{@link CommandExecutor} session.
 *
 * This class prevents passing device instances across lower level methods when evaluating the Protocol + Versions
 * (e.g. SMR / 4, 5)
 */
public class CommandExecutorDeviceContext {

    private final DlmsDevice dlmsDevice;

    public CommandExecutorDeviceContext(DlmsDevice dlmsDevice) {
        this.dlmsDevice = dlmsDevice;
    }

    public DlmsDevice getDlmsDevice() {
        return dlmsDevice;
    }

    public boolean isSMR5() {
        return Protocol.isSMR5(this.dlmsDevice.getProtocol(), this.dlmsDevice.getProtocolVersion());
    }
}
