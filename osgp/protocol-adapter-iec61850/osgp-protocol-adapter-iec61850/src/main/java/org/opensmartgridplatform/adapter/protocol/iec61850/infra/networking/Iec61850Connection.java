/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking;

import org.joda.time.DateTime;
import com.beanit.openiec61850.ClientAssociation;
import com.beanit.openiec61850.ServerModel;

import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.IED;

public class Iec61850Connection {

    private final Iec61850ClientAssociation clientAssociation;

    private final ServerModel serverModel;

    private final DateTime connectionStartTime;

    private IED ied;

    public Iec61850Connection(final Iec61850ClientAssociation clientAssociation, final ServerModel serverModel) {
        this.clientAssociation = clientAssociation;
        this.serverModel = serverModel;
        this.connectionStartTime = null;
    }

    public Iec61850Connection(final Iec61850ClientAssociation clientAssociation, final ServerModel serverModel,
            final DateTime connectionStartTime, final IED ied) {
        this.clientAssociation = clientAssociation;
        this.serverModel = serverModel;
        this.connectionStartTime = connectionStartTime;
        this.ied = ied;
    }

    public Iec61850ClientAssociation getIec61850ClientAssociation() {
        return this.clientAssociation;
    }

    public ClientAssociation getClientAssociation() {
        return this.clientAssociation == null ? null : this.clientAssociation.getClientAssociation();
    }

    public ServerModel getServerModel() {
        return this.serverModel;
    }

    public DateTime getConnectionStartTime() {
        return this.connectionStartTime;
    }

    public IED getIed() {
        return this.ied;
    }
}