package com.alliander.osgp.adapter.ws.endpointinterceptors;

import java.lang.annotation.Annotation;

import org.springframework.core.MethodParameter;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.adapter.method.MethodArgumentResolver;

/**
 * Resolves a specified property from the MessageContext against an Annotated
 * MethodParameter.
 */
public class AnnotationMethodArgumentResolver implements MethodArgumentResolver {
    private final String contextPropertyName;
    private final Class<? extends Annotation> parameterAnnotation;

    public AnnotationMethodArgumentResolver(final String contextPropertyName,
            final Class<? extends Annotation> parameterAnnotation) {
        this.contextPropertyName = contextPropertyName;
        this.parameterAnnotation = parameterAnnotation;
    }

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(this.parameterAnnotation);
    }

    @Override
    public Object resolveArgument(final MessageContext messageContext, final MethodParameter parameter)
            throws Exception {
        if (messageContext.containsProperty(this.contextPropertyName)) {
            return messageContext.getProperty(this.contextPropertyName);
        }

        throw new UnsupportedOperationException();
    }
}
