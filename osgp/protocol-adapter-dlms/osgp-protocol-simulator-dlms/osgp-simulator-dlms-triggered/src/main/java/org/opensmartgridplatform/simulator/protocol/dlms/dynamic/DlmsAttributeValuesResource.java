// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.dynamic;

import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
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
