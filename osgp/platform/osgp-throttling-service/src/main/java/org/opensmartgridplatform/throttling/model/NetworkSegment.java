// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling.model;

public record NetworkSegment(short throttlingConfigId, int baseTransceiverStationId, int cellId) {}
