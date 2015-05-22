/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.acceptancetests;

import java.util.List;
import java.util.Set;

import org.givwenzen.GivWenZenException;
import org.givwenzen.ICustomParserFinder;
import org.givwenzen.parse.MethodParameterParser;
import org.givwenzen.reflections.Reflections;
import org.givwenzen.reflections.ReflectionsBuilder;

/**
 * Copied from GivWenZen to get around fixed package directories (bdd.parse)
 */
public class ScopedCustomParserFinder implements ICustomParserFinder {

    private String basePackage;

    public ScopedCustomParserFinder(final String basePackage) {
        this.basePackage = basePackage;
    }

    @Override
    public void addCustomParsers(final List<MethodParameterParser> accumulatedParsers) {
        final Reflections reflections = new ReflectionsBuilder().basePackage(this.basePackage).subTypeScanner().build();
        final Set<Class<? extends MethodParameterParser>> classes = reflections.getSubTypesOf(MethodParameterParser.class);
        for (final Class<? extends MethodParameterParser> aClass : classes) {
            try {
                accumulatedParsers.add(aClass.newInstance());
            } catch (final InstantiationException e) {
                throw new GivWenZenException("Unable to instantiate " + aClass.getName()
                        + ".  The usual cause of this is the class is an interface or abstract class.", e);
            } catch (final IllegalAccessException e) {
                throw new GivWenZenException("Unable to access the constructor for " + aClass.getName()
                        + ".  The usual cause of this is the constructor is private or protected.", e);
            }
        }
    }
}
