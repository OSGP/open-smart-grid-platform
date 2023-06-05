// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.microgrids.glue.steps.database.ws;

import org.opensmartgridplatform.adapter.ws.domain.repositories.ResponseDataRepository;
import org.springframework.stereotype.Repository;

@Repository("wsMicrogridsResponseDataRepository")
public interface WsMicrogridsResponseDataRepository extends ResponseDataRepository {}
