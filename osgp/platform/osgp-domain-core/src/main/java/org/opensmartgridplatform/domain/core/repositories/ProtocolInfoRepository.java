// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.repositories;

import org.opensmartgridplatform.domain.core.entities.ProtocolInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProtocolInfoRepository extends JpaRepository<ProtocolInfo, Long> {

  default ProtocolInfo findByProtocolAndProtocolVersion(
      final String protocol, final String protocolVersion) {
    return this.findByProtocolAndProtocolVersionAndProtocolVariant(protocol, protocolVersion, null);
  }

  ProtocolInfo findByProtocolAndProtocolVersionAndProtocolVariant(
      String protocol, String protocolVersion, String protocolVariant);
}
