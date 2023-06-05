// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services;

import java.io.IOException;
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.ie.IeQualifierOfInterrogation;
import org.openmuc.j60870.ie.InformationObject;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.LogItem;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.RequestMetadata;
import org.opensmartgridplatform.iec60870.QualifierOfInterrogation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GeneralInterrogationService {

  private static final int QUALIFIER_OF_INTERROGATION_ID =
      QualifierOfInterrogation.INTERROGATED_BY_STATION.getId();

  private static final int ORIGINATOR_ADDRESS = 0;

  @Autowired private LoggingService loggingService;

  public void sendGeneralInterrogation(
      final ClientConnection deviceConnection, final RequestMetadata requestMetadata)
      throws IOException {
    final String connectedDevice =
        deviceConnection.getConnectionParameters().getDeviceIdentification();
    final int commonAddress = deviceConnection.getConnectionParameters().getCommonAddress();

    deviceConnection
        .getConnection()
        .interrogation(
            commonAddress,
            CauseOfTransmission.ACTIVATION,
            new IeQualifierOfInterrogation(QUALIFIER_OF_INTERROGATION_ID));

    // interrogation command creates this asdu internally, however we
    // need it here as well for logging...
    final ASdu asdu =
        new ASdu(
            ASduType.C_IC_NA_1,
            false,
            CauseOfTransmission.ACTIVATION,
            false,
            false,
            ORIGINATOR_ADDRESS,
            commonAddress,
            new InformationObject(
                0, new IeQualifierOfInterrogation(QUALIFIER_OF_INTERROGATION_ID)));

    final LogItem logItem =
        new LogItem(
            connectedDevice,
            requestMetadata.getOrganisationIdentification(),
            false,
            asdu.toString());

    this.loggingService.log(logItem);
  }
}
