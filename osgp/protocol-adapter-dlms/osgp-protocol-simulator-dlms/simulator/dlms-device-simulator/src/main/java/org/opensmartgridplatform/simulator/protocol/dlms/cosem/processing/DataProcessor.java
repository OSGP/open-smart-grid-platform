// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing;

/**
 * Convenient combination of DataObjectCreator and FilterProvider, because their use is closely
 * related.
 */
public interface DataProcessor extends DataObjectCreator, FilterProvider {}
