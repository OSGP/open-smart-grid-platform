/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.jasper.sessionproviders;

import org.osgp.adapter.protocol.jasper.sessionproviders.exceptions.SessionProviderException;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.shared.exceptionhandling.FunctionalException;

public abstract class SessionProvider {

    @Autowired
    protected SessionProviderMap sessionProviderMap;

    public abstract String getIpAddress(String iccId) throws SessionProviderException, FunctionalException;
}
