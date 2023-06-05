// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.iec60870.domain;

import org.openmuc.j60870.ASdu;

/** Interface for generation of ASDUs by the 60870 simulator or by test cases. */
public interface Iec60870AsduGenerator {

  ASdu getNextAsdu();
}
