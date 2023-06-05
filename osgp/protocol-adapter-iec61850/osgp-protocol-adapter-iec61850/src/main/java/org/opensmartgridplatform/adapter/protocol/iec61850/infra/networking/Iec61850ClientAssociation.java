// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking;

import com.beanit.openiec61850.ClientAssociation;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.reporting.Iec61850ClientBaseEventListener;

/**
 * Helper class, intended to make the ClientEventListener with a ClientAssociation available for
 * access after the association has been set up.
 *
 * <p>This way the SqNum of the buffered report data can be made available to the listener before
 * reporting is enabled on the association. The listener can then use the SqNum to determine if
 * reports may have been received earlier already.
 */
public class Iec61850ClientAssociation {

  private final ClientAssociation clientAssociation;
  private final Iec61850ClientBaseEventListener reportListener;

  public Iec61850ClientAssociation(
      final ClientAssociation clientAssociation,
      final Iec61850ClientBaseEventListener reportListener) {
    this.clientAssociation = clientAssociation;
    this.reportListener = reportListener;
  }

  public ClientAssociation getClientAssociation() {
    return this.clientAssociation;
  }

  public Iec61850ClientBaseEventListener getReportListener() {
    return this.reportListener;
  }
}
