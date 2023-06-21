// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.logging.domain.repositories;

import org.opensmartgridplatform.logging.domain.entities.WebServiceMonitorLogItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebServiceMonitorLogRepository
    extends JpaRepository<WebServiceMonitorLogItem, Long> {}
