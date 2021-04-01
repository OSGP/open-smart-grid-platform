/**
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

import lombok.Data;

@Data
public class SetSubscriptionInformationResponseData implements Serializable {

    private static final long serialVersionUID = 333099329546171974L;

    private String meId;

    private String esn;

    private String uimId;

    private String eqId;

    private String ipAddress;

    private String mdn;

    private Integer btsId;

    private Integer cellId;

    private String status;

    private String custCode;

    private String supplierReferenceId;

}
