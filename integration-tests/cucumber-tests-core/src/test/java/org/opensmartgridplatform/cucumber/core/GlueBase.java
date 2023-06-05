// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.core;

import org.springframework.test.context.ContextConfiguration;

/** Glue base which tells which ApplicationContext to use. */
@ContextConfiguration(classes = ApplicationContext.class)
public abstract class GlueBase {}
