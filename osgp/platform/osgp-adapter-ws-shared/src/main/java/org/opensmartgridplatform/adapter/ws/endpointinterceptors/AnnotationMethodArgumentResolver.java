//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.endpointinterceptors;

import java.lang.annotation.Annotation;
import org.springframework.core.MethodParameter;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.adapter.method.MethodArgumentResolver;

/** Resolves a specified property from the MessageContext against an Annotated MethodParameter. */
public class AnnotationMethodArgumentResolver implements MethodArgumentResolver {
  private final String contextPropertyName;
  private final Class<? extends Annotation> parameterAnnotation;
  private final boolean optional;

  /**
   * Creates a resolver for an argument.
   *
   * <p>If optional is set to false, it will throw an exception if the given property is not found
   * on the message context. If set to true, it will return 'null' if the property is missing.
   *
   * @param contextPropertyName Name of the property in the message context
   * @param parameterAnnotation Annotation on the method parameter
   * @param optional Is the property allowed to be missing from the message context?
   */
  public AnnotationMethodArgumentResolver(
      final String contextPropertyName,
      final Class<? extends Annotation> parameterAnnotation,
      final boolean optional) {
    this.contextPropertyName = contextPropertyName;
    this.parameterAnnotation = parameterAnnotation;
    this.optional = optional;
  }

  /**
   * Creates a resolver for a mandatory argument. Throws an exception if the given property is not
   * found on the message context.
   *
   * @param contextPropertyName Name of the property in the message context
   * @param parameterAnnotation Annotation on the method parameter
   */
  public AnnotationMethodArgumentResolver(
      final String contextPropertyName, final Class<? extends Annotation> parameterAnnotation) {
    this(contextPropertyName, parameterAnnotation, false);
  }

  @Override
  public boolean supportsParameter(final MethodParameter parameter) {
    return parameter.hasParameterAnnotation(this.parameterAnnotation);
  }

  @Override
  public Object resolveArgument(
      final MessageContext messageContext, final MethodParameter parameter) {
    if (messageContext.containsProperty(this.contextPropertyName)) {
      return messageContext.getProperty(this.contextPropertyName);
    } else if (this.optional) {
      return null;
    }

    throw new UnsupportedOperationException(
        String.format("argument %s not found in message context", this.contextPropertyName));
  }
}
