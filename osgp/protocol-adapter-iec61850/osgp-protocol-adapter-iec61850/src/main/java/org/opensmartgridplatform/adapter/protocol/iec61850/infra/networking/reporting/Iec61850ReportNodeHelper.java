/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.reporting;

import static java.util.stream.Collectors.toCollection;

import com.beanit.openiec61850.ModelNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.rtu.RtuReadCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.rtu.RtuReadCommandFactory;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.ReadOnlyNodeContainer;
import org.opensmartgridplatform.dto.valueobjects.microgrids.MeasurementDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.MeasurementFilterDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Iec61850ReportNodeHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850ReportNodeHelper.class);

  private final Set<String> nodesUsingId;
  private final Pattern nodePattern;
  private final int indexGroup;
  private final Set<String> compositeNodes;

  /**
   * Returns a report handler that deals with node node names in which the ID number should be
   * included. The index number is based on the difference between the node name and the node
   * reference.
   *
   * <p>To obtain a helper that uses a pattern to determine the index, use {@link
   * #Iec61850ReportNodeHelper(Set, Pattern, int)} instead.
   *
   * <p>For a helper that knows how to handle composite nodes, use {@link
   * #Iec61850ReportNodeHelper(Set, Set)} or {@link #Iec61850ReportNodeHelper(Set, Pattern, int,
   * Set)}.
   *
   * @param nodesUsingId data attributes with descriptions of nodes for which to include the ID
   *     number in the name
   */
  public Iec61850ReportNodeHelper(final Set<DataAttribute> nodesUsingId) {
    this(nodesUsingId, null, -1);
  }

  /**
   * Returns a report handler that deals with node node names in which the ID number should be
   * included. The index number is based on the provided {@code nodePattern} and {@code index}.
   *
   * <p>To obtain a helper that uses the node name and reference to determine the index, use {@link
   * #Iec61850ReportNodeHelper(Set)} instead.
   *
   * <p>For a helper that knows how to handle composite nodes, use {@link
   * #Iec61850ReportNodeHelper(Set, Set)} or {@link #Iec61850ReportNodeHelper(Set, Pattern, int,
   * Set)}.
   *
   * @param nodesUsingId data attributes with descriptions of nodes for which to include the ID
   *     number in the name
   * @param nodePattern a pattern matching a node reference containing a group for the index number
   * @param indexGroup the index of the group from {@code nodePattern} that has the ID number for
   *     the node
   */
  public Iec61850ReportNodeHelper(
      final Set<DataAttribute> nodesUsingId, final Pattern nodePattern, final int indexGroup) {

    this(nodesUsingId, nodePattern, indexGroup, Collections.emptySet());
  }

  /**
   * Returns a report handler that deals with node node names in which the ID number should be
   * included. The index number is based on the difference between the node name and the node
   * reference.
   *
   * <p>To obtain a helper that uses a pattern to determine the index, use {@link
   * #Iec61850ReportNodeHelper(Set, Pattern, int)} instead.
   *
   * @param nodesUsingId data attributes with descriptions of nodes for which to include the ID
   *     number in the name
   * @param compositeNodes names of composite nodes to be handles by this helper
   */
  public Iec61850ReportNodeHelper(
      final Set<DataAttribute> nodesUsingId, final Set<String> compositeNodes) {
    this(nodesUsingId, null, -1, compositeNodes);
  }

  /**
   * Returns a report handler that deals with node node names in which the ID number should be
   * included. The index number is based on the provided {@code nodePattern} (if {@code not null})
   * and {@code index}.
   *
   * <p>To obtain a helper that uses the node name and reference to determine the index, use {@link
   * #Iec61850ReportNodeHelper(Set, Set)} instead.
   *
   * <p>If {@code nodePattern == null} this helper will use the node name and reference to determine
   * the node index.
   *
   * @param nodesUsingId data attributes with descriptions of nodes for which to include the ID
   *     number in the name
   * @param nodePattern a pattern matching a node reference containing a group for the index number
   * @param indexGroup the index of the group from {@code nodePattern} that has the ID number for
   *     the node
   * @param compositeNodes names of composite nodes to be handles by this helper
   */
  public Iec61850ReportNodeHelper(
      final Set<DataAttribute> nodesUsingId,
      final Pattern nodePattern,
      final int indexGroup,
      final Set<String> compositeNodes) {

    if (nodesUsingId == null) {
      this.nodesUsingId = Collections.emptySet();
    } else {
      this.nodesUsingId =
          nodesUsingId.stream()
              .map(DataAttribute::getDescription)
              .collect(toCollection(TreeSet::new));
    }
    this.nodePattern = nodePattern;
    this.indexGroup = indexGroup;
    if (compositeNodes == null) {
      this.compositeNodes = Collections.emptySet();
    } else {
      this.compositeNodes = new TreeSet<>(compositeNodes);
    }
  }

  public List<MeasurementDto> getMeasurements(
      final ReadOnlyNodeContainer nodeContainer,
      final RtuReadCommandFactory<MeasurementDto, MeasurementFilterDto> commandFactory) {

    if (this.isCompositeNode(nodeContainer.getFcmodelNode())) {
      return this.getMeasurementsForCompositeNode(nodeContainer, commandFactory);
    }
    return this.getMeasurementsForNonCompositeNode(nodeContainer, commandFactory);
  }

  private boolean useId(final String nodeName) {
    return this.nodesUsingId.contains(nodeName);
  }

  private boolean isCompositeNode(final ModelNode node) {
    return this.compositeNodes.contains(node.getName());
  }

  private List<MeasurementDto> getMeasurementsForCompositeNode(
      final ReadOnlyNodeContainer nodeContainer,
      final RtuReadCommandFactory<MeasurementDto, MeasurementFilterDto> commandFactory) {

    final List<MeasurementDto> measurements = new ArrayList<>();
    for (final ModelNode child : nodeContainer.getFcmodelNode().getChildren()) {
      final RtuReadCommand<MeasurementDto> command =
          commandFactory.getCommand(this.getChildCommandName(child));
      if (command == null) {
        LOGGER.warn("No command found for node {}", child.getReference().getName());
      } else {
        measurements.add(command.translate(nodeContainer.getChild(child.getName())));
      }
    }
    return measurements;
  }

  private List<MeasurementDto> getMeasurementsForNonCompositeNode(
      final ReadOnlyNodeContainer nodeContainer,
      final RtuReadCommandFactory<MeasurementDto, MeasurementFilterDto> commandFactory) {

    final List<MeasurementDto> measurements = new ArrayList<>();
    final RtuReadCommand<MeasurementDto> command =
        commandFactory.getCommand(this.getCommandName(nodeContainer));
    if (command == null) {
      LOGGER.warn(
          "No command found for node {}", nodeContainer.getFcmodelNode().getReference().getName());
    } else {
      measurements.add(command.translate(nodeContainer));
    }
    return measurements;
  }

  private String getCommandName(final ReadOnlyNodeContainer member) {

    final String nodeName = member.getFcmodelNode().getName();
    if (this.useId(nodeName)) {
      final String refName = member.getFcmodelNode().getReference().toString();
      return nodeName + this.getIndex(nodeName, refName);
    } else {
      return nodeName;
    }
  }

  private String getChildCommandName(final ModelNode child) {

    final String nodeName = child.getParent().getName() + "." + child.getName();
    if (this.useId(nodeName)) {
      final String refName = child.getReference().toString();
      return nodeName + this.getIndex(nodeName, refName);
    } else {
      return nodeName;
    }
  }

  private String getIndex(final String nodeName, final String reference) {
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

  private String getIndexBasedOnNodeNameAndReference(
      final String nodeName, final String reference) {
    final int startIndex = reference.length() - nodeName.length() - 2;
    return reference.substring(startIndex, startIndex + 1);
  }
}
