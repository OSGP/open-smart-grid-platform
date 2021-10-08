/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.dynamic;

import com.fasterxml.jackson.databind.node.ObjectNode;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.openmuc.jdlms.ObisCode;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/dynamic")
public class DlmsAttributeValuesResource {

  private final DlmsAttributeValuesCache cache = new DlmsAttributeValuesCache();

  @DELETE
  public Response clearAttributeValues() {
    this.cache.clearAllValues();
    return Response.ok().build();
  }

  @GET
  @Path("/{classId}/{obisCode}")
  public Response getAttributeValues(
      @PathParam("classId") final int classId, @PathParam("obisCode") final ObisCode obisCode) {

    final ObjectNode value = this.cache.getAttributeValues(obisCode);
    if (value == null) {
      return Response.status(Status.NO_CONTENT).build();
    }
    return Response.ok(value).build();
  }

  @PUT
  @Path("/{classId}/{obisCode}")
  public Response storeAttributeValues(
      @PathParam("classId") final int classId,
      @PathParam("obisCode") final ObisCode obisCode,
      final ObjectNode attributeValues) {

    this.cache.storeAttributeValues(obisCode, attributeValues);
    return Response.ok().build();
  }

  @GET
  @Path("/{classId}/{obisCode}/{attributeId}")
  public Response getAttributeValue(
      @PathParam("classId") final int classId,
      @PathParam("obisCode") final ObisCode obisCode,
      @PathParam("attributeId") final int attributeId) {
    final ObjectNode value = this.cache.getAttributeValue(obisCode, attributeId);
    if (value == null) {
      return Response.status(Status.NO_CONTENT).build();
    }
    return Response.ok(value).build();
  }

  @PUT
  @Path("/{classId}/{obisCode}/{attributeId}")
  public Response setAttributeValue(
      @PathParam("classId") final int classId,
      @PathParam("obisCode") final ObisCode obisCode,
      @PathParam("attributeId") final int attributeId,
      final ObjectNode attributeValue) {
    this.cache.storeAttributeValue(obisCode, attributeId, attributeValue);
    return Response.ok().build();
  }
}
