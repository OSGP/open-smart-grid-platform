/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

public class PushSetupAlarmDto extends AbstractPushSetupDto {

    private static final long serialVersionUID = -3541154908239512383L;

    public static class Builder extends AbstractPushSetupDto.AbstractBuilder {

        @Override
        public PushSetupAlarmDto build() {
            final AbstractPushSetupDto abstractPushSetupDto = AbstractPushSetupDto.newBuilder()
                    .withLogicalName(this.logicalName).withPushObjectList(this.pushObjectList)
                    .withSendDestinationAndMethod(this.sendDestinationAndMethod)
                    .withCommunicationWindow(this.communicationWindow)
                    .withRandomisationStartInterval(this.randomisationStartInterval)
                    .withNumberOfRetries(this.numberOfRetries).withRepetitionDelay(this.repetitionDelay).build();
            return new PushSetupAlarmDto(abstractPushSetupDto);
        }
    }

    public PushSetupAlarmDto(final AbstractPushSetupDto abstractPushSetupDto) {
        super(abstractPushSetupDto);
    }

}
