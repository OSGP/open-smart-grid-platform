/*
 * Copyright 2021 Alliander N.V.
 */
package org.opensmartgridplatform.adapter.ws.endpointinterceptors;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation which indicates that a method parameter should be bound to the MaxScheduleTime in
 * SoapHeader.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface MaxScheduleTime {}
