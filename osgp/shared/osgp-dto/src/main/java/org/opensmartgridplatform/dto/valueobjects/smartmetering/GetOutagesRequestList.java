/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.List;

public class GetOutagesRequestList implements Serializable {

    private static final long serialVersionUID = -9041475699103580130L;

    private List<GetOutagesRequestDto> requestDtoList;

    public GetOutagesRequestList(final List<GetOutagesRequestDto> requestDtoList) {
        this.requestDtoList = requestDtoList;
    }

    public List<GetOutagesRequestDto> getGetOutagesRequestList() {
        return this.requestDtoList;
    }
}
