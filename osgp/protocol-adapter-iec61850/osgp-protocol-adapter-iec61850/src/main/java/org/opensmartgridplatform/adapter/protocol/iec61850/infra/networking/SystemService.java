// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking;

import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.NodeException;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import org.opensmartgridplatform.dto.valueobjects.microgrids.GetDataSystemIdentifierDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.SetDataSystemIdentifierDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.SystemFilterDto;

public interface SystemService {
  GetDataSystemIdentifierDto getData(
      SystemFilterDto systemFilter, final Iec61850Client client, final DeviceConnection connection)
      throws NodeException;

  void setData(
      SetDataSystemIdentifierDto systemIdentifier,
      final Iec61850Client client,
      DeviceConnection connection)
      throws NodeException;
}
