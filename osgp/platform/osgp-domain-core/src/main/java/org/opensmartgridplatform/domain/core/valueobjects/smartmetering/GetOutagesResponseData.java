/**
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.List;

public class GetOutagesResponseData extends ActionResponse implements Serializable {

    private static final long serialVersionUID = 4966055518516878043L;

    private List<Outage> outages;

    public GetOutagesResponseData(final List<Outage> outages) {
        this.outages = outages;
    }

    public List<Outage> getOutages() {
        return this.outages;
    }

}
