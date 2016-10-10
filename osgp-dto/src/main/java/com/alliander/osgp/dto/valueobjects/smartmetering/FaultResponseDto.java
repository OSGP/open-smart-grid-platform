/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.util.Objects;

public class FaultResponseDto extends ActionResponseDto {

    private static final long serialVersionUID = -2599283959144295334L;

    private final Integer code;
    private final String message;
    private final String component;
    private final String innerException;
    private final String innerMessage;
    private final FaultResponseParametersDto parameters;

    public FaultResponseDto(final Integer code, final String message, final String component,
            final String innerException, final String innerMessage, final FaultResponseParametersDto parameters) {
        super(OsgpResultTypeDto.NOT_OK, null, null);
        Objects.requireNonNull("message", message);
        this.code = code;
        this.message = message;
        this.component = component;
        this.innerException = innerException;
        this.innerMessage = innerMessage;
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FaultResponseDto[");
        if (this.hasCode()) {
            sb.append("code=").append(this.code).append(", ");
        }
        sb.append("message=").append(this.message);
        if (this.hasComponent()) {
            sb.append(", component=").append(this.component);
        }
        if (this.hasInnerException()) {
            sb.append(", innerException=").append(this.innerException);
        }
        if (this.hasInnerMessage()) {
            sb.append(", innerMessage=").append(this.innerMessage);
        }
        if (this.hasParameters()) {
            sb.append(", ").append(this.parameters);
        }
        return sb.append(']').toString();
    }

    public boolean hasCode() {
        return this.code != null;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public boolean hasComponent() {
        return this.component != null;
    }

    public String getComponent() {
        return this.component;
    }

    public boolean hasInnerException() {
        return this.innerException != null;
    }

    public String getInnerException() {
        return this.innerException;
    }

    public boolean hasInnerMessage() {
        return this.innerMessage != null;
    }

    public String getInnerMessage() {
        return this.innerMessage;
    }

    public boolean hasParameters() {
        return this.parameters != null;
    }

    public FaultResponseParametersDto getParameters() {
        return this.parameters;
    }
}
