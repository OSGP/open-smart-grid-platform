/**
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;

public class DlmsModemInfo extends DlmsObject {
    private static final int CLASS_ID_MODEM_INFO = 47;

    public static final int ATTRIBUTE_ID_OPERATOR = 2;
    public static final int ATTRIBUTE_ID_MODEM_REGISTRATION_STATUS = 3;
    public static final int ATTRIBUTE_ID_CIRCUIT_SWITCHED_STATUS = 4;
    public static final int ATTRIBUTE_ID_PACKET_SWITCHED_STATUS = 5;
    public static final int ATTRIBUTE_ID_CELL_INFO = 6;
    public static final int ATTRIBUTE_ID_ADJACENT_CELLS = 7;
    public static final int ATTRIBUTE_ID_CAPTURE_TIME = 8;

    private final CommunicationMethod communicationMethod;

    public DlmsModemInfo(final DlmsObjectType type, final String obisCode, final CommunicationMethod communicationMethod) {
        super(type, CLASS_ID_MODEM_INFO, obisCode);

        this.communicationMethod = communicationMethod;
    }

    public CommunicationMethod getCommunicationMethod() {
        return this.communicationMethod;
    }
}
