/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.reporting;

import static java.util.stream.Collectors.toCollection;

import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openmuc.openiec61850.ModelNode;

import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.ReadOnlyNodeContainer;

public class Iec61850ReportNodeHelper {

    private final Set<String> nodesUsingId;
    private final Pattern nodePattern;
    private final int indexGroup;

    public Iec61850ReportNodeHelper(final Set<DataAttribute> nodesUsingId) {
        this(nodesUsingId, null, -1);
    }

    public Iec61850ReportNodeHelper(final Set<DataAttribute> nodesUsingId, final Pattern nodePattern,
            final int indexGroup) {

        this.nodesUsingId = nodesUsingId.stream().map(DataAttribute::getDescription)
                .collect(toCollection(TreeSet::new));
        this.nodePattern = nodePattern;
        this.indexGroup = indexGroup;
    }

    public boolean useId(final String nodeName) {
        return this.nodesUsingId.contains(nodeName);
    }

    public String getCommandName(final ReadOnlyNodeContainer member) {

        final String nodeName = member.getFcmodelNode().getName();
        if (this.useId(nodeName)) {
            final String refName = member.getFcmodelNode().getReference().toString();
            return nodeName + this.getIndex(nodeName, refName);
        } else {
            return nodeName;
        }
    }

    public String getChildCommandName(final ModelNode child) {

        final String nodeName = child.getParent().getName() + "." + child.getName();
        if (this.useId(nodeName)) {
            final String refName = child.getReference().toString();
            return nodeName + this.getIndex(nodeName, refName);
        } else {
            return nodeName;
        }
    }

    public String getIndex(final String nodeName, final String reference) {
        if (this.nodePattern == null) {
            return this.getIndexBasedOnNodeNameAndReference(nodeName, reference);
        }
        return this.getIndexBasedOnPattern(reference);
    }

    private String getIndexBasedOnPattern(final String reference) {
        final Matcher reportMatcher = this.nodePattern.matcher(reference);
        if (reportMatcher.matches()) {
            return reportMatcher.group(this.indexGroup);
        }
        return "";
    }

    private String getIndexBasedOnNodeNameAndReference(final String nodeName, final String reference) {
        final int startIndex = reference.length() - nodeName.length() - 2;
        return reference.substring(startIndex, startIndex + 1);
    }
}
