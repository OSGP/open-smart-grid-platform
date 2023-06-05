// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands;

import com.beanit.openiec61850.Fc;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.joda.time.DateTime;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects.DeviceMessageLog;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.Function;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.LogicalNode;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.NodeContainer;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.SubDataAttribute;
import org.opensmartgridplatform.adapter.protocol.iec61850.services.DeviceMessageLoggingService;
import org.opensmartgridplatform.dto.valueobjects.CertificationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Iec61850UpdateSslCertificateCommand {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(Iec61850UpdateSslCertificateCommand.class);

  private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

  private final DeviceMessageLoggingService loggingService;

  public Iec61850UpdateSslCertificateCommand(final DeviceMessageLoggingService loggingService) {
    this.loggingService = loggingService;
  }

  public void pushSslCertificateToDevice(
      final Iec61850Client iec61850Client,
      final DeviceConnection deviceConnection,
      final CertificationDto certification)
      throws ProtocolAdapterException {
    final Function<Void> function =
        new Function<Void>() {

          @Override
          public Void apply(final DeviceMessageLog deviceMessageLog)
              throws ProtocolAdapterException {

            LOGGER.info("Reading the certificate authority url");
            final NodeContainer sslConfiguration =
                deviceConnection.getFcModelNode(
                    LogicalDevice.LIGHTING,
                    LogicalNode.STREET_LIGHT_CONFIGURATION,
                    DataAttribute.CERTIFICATE_AUTHORITY_REPLACE,
                    Fc.CF);
            iec61850Client.readNodeDataValues(
                deviceConnection.getConnection().getClientAssociation(),
                sslConfiguration.getFcmodelNode());

            // Removing trailing and leading slashes (if present) from the
            // domain and the URL.
            String adjustedDomain = certification.getCertificateDomain();
            if (adjustedDomain.endsWith("/")) {
              adjustedDomain = adjustedDomain.substring(0, adjustedDomain.length() - 1);
            }

            String adjustedUrl = certification.getCertificateUrl();
            if (adjustedUrl.startsWith("/")) {
              adjustedUrl = adjustedUrl.substring(1, adjustedUrl.length());
            }

            final String fullUrl = adjustedDomain.concat("/").concat(adjustedUrl);

            LOGGER.info("Updating the certificate download url to {}", fullUrl);
            sslConfiguration.writeString(SubDataAttribute.URL, fullUrl);

            deviceMessageLog.addVariable(
                LogicalNode.STREET_LIGHT_CONFIGURATION,
                DataAttribute.CERTIFICATE_AUTHORITY_REPLACE,
                Fc.CF,
                SubDataAttribute.URL,
                fullUrl);

            final NodeContainer clock =
                deviceConnection.getFcModelNode(
                    LogicalDevice.LIGHTING,
                    LogicalNode.STREET_LIGHT_CONFIGURATION,
                    DataAttribute.CLOCK,
                    Fc.CF);
            iec61850Client.readNodeDataValues(
                deviceConnection.getConnection().getClientAssociation(), clock.getFcmodelNode());

            final DateTime deviceTime = new DateTime(clock.getDate(SubDataAttribute.CURRENT_TIME));
            final Date oneMinuteFromNow = deviceTime.plusMinutes(1).toDate();

            LOGGER.info("Updating the certificate download start time to: {}", oneMinuteFromNow);
            sslConfiguration.writeDate(SubDataAttribute.START_TIME, oneMinuteFromNow);

            deviceMessageLog.addVariable(
                LogicalNode.STREET_LIGHT_CONFIGURATION,
                DataAttribute.CERTIFICATE_AUTHORITY_REPLACE,
                Fc.CF,
                SubDataAttribute.START_TIME,
                Iec61850UpdateSslCertificateCommand.this.simpleDateFormat.format(oneMinuteFromNow));

            Iec61850UpdateSslCertificateCommand.this.loggingService.logMessage(
                deviceMessageLog,
                deviceConnection.getDeviceIdentification(),
                deviceConnection.getOrganisationIdentification(),
                false);

            return null;
          }
        };

    iec61850Client.sendCommandWithRetry(
        function, "UpdateSslCertificate", deviceConnection.getDeviceIdentification());
  }
}
