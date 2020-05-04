/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package stub;

import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceResponseMessageSender;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;

public class DeviceResponseMessageSenderStub extends DeviceResponseMessageSender {

    private int responseMessagesSent;

    @Override
    public void send(final ResponseMessage responseMessage) {
        responseMessagesSent++;
    }

    public int getResponseMessagesSent() {
        return responseMessagesSent;
    }

}
