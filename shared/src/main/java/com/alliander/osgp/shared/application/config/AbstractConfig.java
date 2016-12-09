/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.shared.application.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

/**
 * Base class for Configuration classes.
 */
public abstract class AbstractConfig {

    /**
     * Qualifier to detect a class path type resource
     */
    private static final String CLASS_PATH_QUALIFIER = "class path resource [";
    
    /**
     * Qualifier to detect a file type resource (i.e. /etc/osp/xxx.properties)
     */
    private static final String RESOURCE_PATH_QUALIFIER = "URL [file:";
    
    /**
     * Qualifier to detect a specific global.properties file resource (i.e. /etc/osp/global.properties)
     */
    private static final String RESOURCE_GLOBAL_PATH_QUALIFIER = "global.properties";
    
    /**
     * Standard spring environment (autowired with setter) 
     */
    protected Environment environment;
  
    /**
     * Default implementation to resolve ${} values in annotations.  
     * @return static PropertySourcesPlaceholderConfigurer
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
        PropertySourcesPlaceholderConfigurer ppc = new PropertySourcesPlaceholderConfigurer();
        ppc.setIgnoreUnresolvablePlaceholders(true);
        return ppc; 
    }
    
    /**
     * Special setter for Spring environment, which reorders the property sources in defined order (high to lowest priority):
     * - environment config
     * - local config files
     * - global config files
     * - classpath config files
     * @param configurableEnvironment Spring environment
     */
    @Autowired
    public void setConfigurableEnvironment(final ConfigurableEnvironment configurableEnvironment) {
        this.environment = configurableEnvironment;
        reorderEnvironment(configurableEnvironment);
    }

    private static void reorderEnvironment(final ConfigurableEnvironment configurableEnvironment) {
        List<PropertySource<?>> env = new ArrayList<>();  
        List<PropertySource<?>> file = new ArrayList<>();  
        List<PropertySource<?>> global = new ArrayList<>();
        List<PropertySource<?>> classpath = new ArrayList<>();
        
        MutablePropertySources sources = configurableEnvironment.getPropertySources();

        // Divide property sources in groups 
        for (PropertySource<?> source : sources) {
            if (source.getName().contains(RESOURCE_GLOBAL_PATH_QUALIFIER)) {
                global.add(source);
            } else if (source.getName().startsWith(RESOURCE_PATH_QUALIFIER)) {
                file.add(source);
            } else if (source.getName().startsWith(CLASS_PATH_QUALIFIER)) {
                classpath.add(source);
            } else {
                env.add(source);
            }
        }

        // Re-add all property sources in correct priority order
        addSources(sources, env);
        addSources(sources, file);
        addSources(sources, global);
        addSources(sources, classpath);
    }
    
    private static void addSources(MutablePropertySources sources, List<PropertySource<?>> sourcesToAdd) {
        // Remove and add each source
        for (PropertySource<?> source : sourcesToAdd) {
            sources.remove(source.getName());
            sources.addLast(source);
        }
    }
}
