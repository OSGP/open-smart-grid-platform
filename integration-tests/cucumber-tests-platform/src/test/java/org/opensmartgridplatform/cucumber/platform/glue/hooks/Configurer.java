/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.glue.hooks;

import java.lang.reflect.Type;
import java.util.Locale;
import java.util.Map;

import cucumber.api.TypeRegistry;
import cucumber.api.TypeRegistryConfigurer;
import io.cucumber.cucumberexpressions.ParameterByTypeTransformer;
import io.cucumber.datatable.TableCellByTypeTransformer;
import io.cucumber.datatable.TableEntryByTypeTransformer;
import io.cucumber.datatable.dependency.com.fasterxml.jackson.databind.ObjectMapper;

public class Configurer implements TypeRegistryConfigurer {
    @Override
    public void configureTypeRegistry(final TypeRegistry registry) {

        final JacksonTableTransformer jacksonTableTransformer = new JacksonTableTransformer();
        registry.setDefaultParameterTransformer(jacksonTableTransformer);
        registry.setDefaultDataTableEntryTransformer(jacksonTableTransformer);
        registry.setDefaultDataTableCellTransformer(jacksonTableTransformer);
    }

    @Override
    public Locale locale() {
        return Locale.ENGLISH;
    }

    private static final class JacksonTableTransformer
            implements ParameterByTypeTransformer, TableEntryByTypeTransformer, TableCellByTypeTransformer {
        private final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public <T> T transform(final Map<String, String> entry, final Class<T> type,
                final TableCellByTypeTransformer cellTransformer) {
            return this.objectMapper.convertValue(entry, type);
        }

        @Override
        public <T> T transform(final String value, final Class<T> cellType) {
            return this.objectMapper.convertValue(value, cellType);
        }

        @Override
        public Object transform(final String value, final Type type) throws Throwable {
            return this.objectMapper.convertValue(value, this.objectMapper.constructType(type));
        }
    }
}