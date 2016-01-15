/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.integrationtests.domain.commands;

import com.alliander.osgp.dto.valueobjects.smartmetering.Channel;
import com.alliander.osgp.dto.valueobjects.smartmetering.ChannelQuery;

public class TestChannelQuery implements ChannelQuery {

    @Override
    public Channel getChannel() {
        return Channel.NONE;
    }

}
