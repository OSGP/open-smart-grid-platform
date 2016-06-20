/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.core.application.mapping.ws;

import javax.xml.datatype.XMLGregorianCalendar;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

import org.joda.time.DateTime;

import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.StatusGroup;
import com.alliander.osgp.domain.core.entities.ScheduledTask;

public class ScheduledTaskConverter extends
        CustomConverter<ScheduledTask, com.alliander.osgp.adapter.ws.schema.core.devicemanagement.ScheduledTask> {

    @Override
    public com.alliander.osgp.adapter.ws.schema.core.devicemanagement.ScheduledTask convert(final ScheduledTask source,
            final Type<? extends com.alliander.osgp.adapter.ws.schema.core.devicemanagement.ScheduledTask> arg1) {
        final com.alliander.osgp.adapter.ws.schema.core.devicemanagement.ScheduledTask scheduledTask = new com.alliander.osgp.adapter.ws.schema.core.devicemanagement.ScheduledTask();
        scheduledTask.setCreationTime(this.mapperFacade.map(new DateTime(source.getCreationTime()),
                XMLGregorianCalendar.class));
        scheduledTask.setDeviceIdentification(source.getDeviceIdentification());
        scheduledTask.setDomain(source.getDomain());
        scheduledTask.setDomainVersion(source.getDomainVersion());
        scheduledTask.setErrorLog(source.getErrorLog());
        scheduledTask.setMessageType(source.getMessageType());
        scheduledTask.setModificationTime(this.mapperFacade.map(new DateTime(source.getModificationTime()),
                XMLGregorianCalendar.class));
        scheduledTask.setOrganisationIdentification(source.getOrganisationIdentification());
        scheduledTask.setScheduledTime(this.mapperFacade.map(new DateTime(source.getscheduledTime().getTime()),
                XMLGregorianCalendar.class));
        scheduledTask.setStatus(StatusGroup.valueOf(source.getStatus().name()));

        return scheduledTask;
    }
}